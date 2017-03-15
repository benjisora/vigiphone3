package com.cstb.vigiphone3.service;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Handler;
import android.os.IBinder;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.NotificationCompat;
import android.util.Log;

import com.cstb.vigiphone3.R;
import com.cstb.vigiphone3.data.database.MyApplication;
import com.cstb.vigiphone3.data.database.Utils;
import com.cstb.vigiphone3.data.model.RecordingRow;
import com.cstb.vigiphone3.network.NetworkService;
import com.cstb.vigiphone3.ui.MainActivity;

import java.io.File;
import java.io.FileWriter;
import java.net.HttpURLConnection;
import java.util.List;

import br.com.goncalves.pugnotification.notification.Load;
import br.com.goncalves.pugnotification.notification.PugNotification;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RecordService extends Service {

    private final Handler recordHandler = new Handler();
    private final Handler tickerHandler = new Handler();
    List<RecordingRow> allData;
    private Runnable saveRunnableCode;
    private NetworkService networkService;
    private Call<RecordingRow> sendRecordings = null;
    private PugNotification notification;
    private Load load;

    /**
     * Stops the recording whenever the order is received
     */
    private BroadcastReceiver mStopReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.d("RecordService", "stop recording");
            MyApplication.setRecording(false);
            recordHandler.removeCallbacksAndMessages(null);
            tickerHandler.removeCallbacksAndMessages(null);
            load.message(R.string.transfer).simple().build();
            sendEndRecording();
            recordHandler.post(saveRunnableCode);
        }
    };

    public RecordService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        Log.d("RecordService", "service created");
    }

    /**
     * {@inheritDoc}
     * Creates a notification and saves data into the database
     */
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        startListening();
        networkService = MyApplication.getNetworkServiceInstance();
        notification = PugNotification.with(this);

        saveRunnableCode = new Runnable() {
            @Override
            public void run() {
                sendDataToDestination();
            }
        };

        load = notification.load()
                .identifier(1)
                .ongoing(true)
                .title(R.string.app_name)
                .message(R.string.recording)
                .smallIcon(R.mipmap.ic_launcher)
                .largeIcon(R.mipmap.ic_launcher)
                .autoCancel(true)
                .click(MainActivity.class)
                .priority(NotificationCompat.PRIORITY_HIGH);

        load.simple().build();
        saveDataPeriodically();

        return Service.START_NOT_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d("RecordService", "service stopped");
        stopListening();
        notification.cancel(1);
        MyApplication.setRecording(false);
        if (sendRecordings != null) {
            sendRecordings.cancel();
        }
    }

    private void startListening() {
        LocalBroadcastManager.getInstance(this).registerReceiver(mStopReceiver, new IntentFilter(MyApplication.stopRecordingFromRecordingFragment));
    }

    private void stopListening() {
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mStopReceiver);
    }

    /**
     * Saves data to the database periodically
     */
    public void saveDataPeriodically() {
        final Long startTimeStamp = System.currentTimeMillis();
        Runnable runnableCode = new Runnable() {
            @Override
            public void run() {
                boolean shouldSave = (MyApplication.isRecording() && (MyApplication.isRecordingInfinitely() || (!MyApplication.isRecordingInfinitely() && System.currentTimeMillis() < startTimeStamp + MyApplication.getRecordTime())));
                if (shouldSave) {

                    long databaseCount = Utils.getinstance().getRecordingRowsCount();

                    if (databaseCount != 0 && databaseCount % MyApplication.getThreshold() == 0) {
                        load.message(R.string.record_and_transfer).simple().build();
                        recordHandler.post(saveRunnableCode);
                    }

                    sendUpdateMessage(MyApplication.saveRowFromRecordService, System.currentTimeMillis());

                    recordHandler.postDelayed(this, MyApplication.getStep());

                } else {
                    Log.d("RecordService", "stop the recording");
                    MyApplication.setRecording(false);
                    recordHandler.removeCallbacks(this);
                    load.message(R.string.transfer).simple().build();
                    sendEndRecording();
                    recordHandler.post(saveRunnableCode);
                }
            }
        };
        recordHandler.post(runnableCode);

        final Runnable ticker = new Runnable() {
            @Override
            public void run() {
                if (MyApplication.isRecording()) {
                    sendUpdateMessage(MyApplication.updateTimeFromRecordService, System.currentTimeMillis() - startTimeStamp);
                    tickerHandler.postDelayed(this, 1000);
                } else {
                    tickerHandler.removeCallbacks(this);
                }
            }
        };
        tickerHandler.post(ticker);
    }

    /**
     * Sends the database to the chosen destination
     */
    private void sendDataToDestination() {
        Log.d("RecordService", "Start saving onto destination");

        if (MyApplication.isSavingOnServer()) {
            if (Utils.canWeConnect(RecordService.this)) {

                if (MyApplication.isRecording()) {
                    load.message(R.string.record_and_transfer).simple().build();
                } else {
                    load.message(R.string.transfer).simple().build();
                }
                sendToServer();
            } else {
                Log.e("RecordService", "No connection available");
            }
        } else {
            sendToFile();
        }
    }

    /**
     * Writes the database to a local file ( /android/data/com.cstb.vigiphone/ )
     */
    private void sendToFile() {

        List<RecordingRow> listToSave = Utils.getinstance().getAllRecordingRows();


        File file = new File(getExternalFilesDir(null), MyApplication.getFileName() + ".txt");

        Log.d("SENDFILE", file.getPath());

        if (file.exists()) {
            try {
                FileWriter filewriter = new FileWriter(file, true);
                filewriter.append(XMLFromRecordingRows(listToSave));
                for (RecordingRow r : listToSave) {
                    r.delete();
                }
                filewriter.flush();
                filewriter.close();
            } catch (Exception e) {
                Log.e("RecordService", "Error editing file : ", e);
            }
        } else {
            try {
                boolean didCreateFile = file.createNewFile();

                if (didCreateFile) {
                    FileWriter filewriter = new FileWriter(file);
                    filewriter.write(XMLFromRecordingRows(listToSave));
                    for (RecordingRow r : listToSave) {
                        r.delete();
                    }
                    filewriter.flush();
                    filewriter.close();
                }
            } catch (Exception e) {
                Log.e("RecordService", "Error creating file : ", e);
            }
        }
        if (MyApplication.isRecording()) {
            load.message(R.string.recording).simple().build();
        } else {
            notification.cancel(1);
            stopSelf();
        }
    }

    /**
     * Creates a string containing all the xml description of each RecordingRow
     *
     * @param list The list of RecordingRow to convert
     * @return The string to save into the file
     */
    private String XMLFromRecordingRows(List<RecordingRow> list) {
        String valueToWrite = "";
        for (RecordingRow r : list) {
            valueToWrite += r.XMLDescription();
        }
        return valueToWrite;
    }

    /**
     * Sends the database to the remote server
     */
    private void sendToServer() {
        allData = Utils.getinstance().getAllRecordingRows();
        sendDataToServer(0, allData);
    }

    /**
     * Recusrively sends each RecordingRow from the database to the remote server
     *
     * @param index      The index of the RecordingRow to send
     * @param listToSend The List to recursively send
     */
    private void sendDataToServer(final int index, final List<RecordingRow> listToSend) {
        if (!listToSend.isEmpty()) {

            sendRecordings = networkService.sendRecording(listToSend.get(index));
            sendRecordings.enqueue(new Callback<RecordingRow>() {
                @Override
                public void onResponse(Call<RecordingRow> call, Response<RecordingRow> response) {

                    if (response.code() == HttpURLConnection.HTTP_OK) {
                        listToSend.get(index).delete();
                    } else {
                        Log.d("RecordService", "Error code " + String.valueOf(response.code()) + ", address : " + call.request().url());
                    }
                    listToSend.remove(index);
                    sendUpdateMessage(MyApplication.updateDatabaseFromRecordService, null);
                    if (!listToSend.isEmpty()) {
                        sendDataToServer(0, listToSend);
                    } else {
                        if (MyApplication.isRecording()) {
                            load.message(R.string.recording).simple().build();
                        } else {
                            notification.cancel(1);
                            stopSelf();
                        }
                    }
                }

                @Override
                public void onFailure(Call<RecordingRow> call, Throwable t) {
                    Log.e("RecordService", "Error during transfer, data will be kept until next try", t);
                    if (MyApplication.isRecording()) {
                        load.message(R.string.recording).simple().build();
                    } else {
                        notification.cancel(1);
                        stopSelf();
                    }
                }
            });
        }
    }

    /**
     * Notifies the RecordingFragment that the recording has stopped
     */
    private void sendEndRecording() {
        Intent intent = new Intent(MyApplication.endRecordingFromRecordService);
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
    }

    /**
     * Sends the updated recording time to the RecordingFragment,
     * or notifies the RecordingFragment that the database has grown or shrunk
     *
     * @param title The intent message
     * @param value The recording time, or unused if it concerns the database
     */
    private void sendUpdateMessage(String title, Long value) {
        Intent intent = new Intent(title);
        if (value != null) {
            intent.putExtra("value", value);
        }
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
    }
}