package com.cstb.vigiphone3.service;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.NotificationCompat;
import android.util.Log;

import com.cstb.vigiphone3.R;
import com.cstb.vigiphone3.data.database.MyApplication;
import com.cstb.vigiphone3.data.database.Utils;
import com.cstb.vigiphone3.data.model.RecordingRow;
import com.cstb.vigiphone3.network.NetworkService;
import com.cstb.vigiphone3.ui.MainActivity;

import java.util.List;

import br.com.goncalves.pugnotification.notification.Load;
import br.com.goncalves.pugnotification.notification.PugNotification;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RecordService extends Service {

    private final Handler recordHandler = new Handler();
    private final Handler tickerHandler = new Handler();
    private Runnable saveRunnableCode;
    private NetworkService networkService;
    private Call<RecordingRow> sendRecordings = null;
    private SharedPreferences SP;
    private PugNotification notification;
    private Load load;
    List<RecordingRow> allData;

    private BroadcastReceiver mStopReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.d("RecordService", "stop recording");
            SP.edit().putBoolean("isRecording", false).apply();
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

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        SP = PreferenceManager.getDefaultSharedPreferences(this.getBaseContext());
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
        SP.edit().putBoolean("isRecording", false).apply();
        if (sendRecordings != null) {
            sendRecordings.cancel();
        }
    }

    private void startListening() {
        LocalBroadcastManager.getInstance(this).registerReceiver(mStopReceiver, new IntentFilter("stopRecording"));
    }

    private void stopListening() {
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mStopReceiver);
    }

    public void saveDataPeriodically() {
        final Long startTimeStamp = System.currentTimeMillis();
        Runnable runnableCode = new Runnable() {
            @Override
            public void run() {
                boolean shouldSave = (SP.getBoolean("isRecording", false) && (SP.getBoolean("recordingInfinitely", true) || (!SP.getBoolean("recordingInfinitely", true) && System.currentTimeMillis() < startTimeStamp + SP.getInt("recordTime", 10000))));
                if (shouldSave) {

                    if (Utils.getinstance().getRecordingRowsCount() == SP.getInt("threshold", 100)) {
                        load.message(R.string.record_and_transfer).simple().build();
                        recordHandler.post(saveRunnableCode);
                    }

                    sendUpdateMessage("saveRecordingRow", System.currentTimeMillis());

                    recordHandler.postDelayed(this, SP.getInt("stepTime", 1000));

                } else {
                    Log.d("RecordService", "stop the recording");
                    SP.edit().putBoolean("isRecording", false).apply();
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
                if(SP.getBoolean("isRecording",false)){
                    sendUpdateMessage("updateTime", System.currentTimeMillis() - startTimeStamp);
                    tickerHandler.postDelayed(this, 1000);
                } else {
                    tickerHandler.removeCallbacks(this);
                }
            }
        };
        tickerHandler.post(ticker);
    }

    private void sendDataToDestination() {
        Log.d("RecordService", "Start saving onto destination");
        boolean shouldSaveOnServer = SP.getBoolean("saveOnServer", true);
        if (shouldSaveOnServer) {
            if (Utils.canWeConnect(RecordService.this)) {

                if (SP.getBoolean("isRecording", false)) {
                    load.message(R.string.record_and_transfer).simple().build();
                } else {
                    load.message(R.string.transfer).simple().build();
                }
                startSend();
            } else {
                Log.e("RecordService", "No connection available");
            }
        } else {
            sendToFile();
        }
    }

    private void sendToFile() {

    }

    private void startSend() {
        allData = Utils.getinstance().getAllRecordingRows();
        sendData(0, allData);
    }

    private void sendData(final int index, final List<RecordingRow> listTosend) {
        if (!listTosend.isEmpty()) {

            sendRecordings = networkService.sendRecording(listTosend.get(index));
            sendRecordings.enqueue(new Callback<RecordingRow>() {
                @Override
                public void onResponse(Call<RecordingRow> call, Response<RecordingRow> response) {
                    Log.d("RecordService", String.valueOf(response.code()) + " : " + listTosend.get(0).getId());
                    listTosend.get(index).delete();
                    listTosend.remove(index);
                    sendUpdateMessage("updateDatabase", null);
                    if (!listTosend.isEmpty()) {
                        sendData(0, listTosend);
                    } else {
                        if (SP.getBoolean("isRecording", false)) {
                            load.message(R.string.recording).simple().build();
                        } else {
                            notification.cancel(1);
                            stopSelf();
                        }
                    }
                }

                @Override
                public void onFailure(Call<RecordingRow> call, Throwable t) {
                    Log.e("RecordService", "Error during transfer, data will be kept until next try");
                    if (SP.getBoolean("isRecording", false)) {
                        load.message(R.string.recording).simple().build();
                    } else {
                        notification.cancel(1);
                        stopSelf();
                    }
                }
            });
        }
    }

    private void sendEndRecording(){
        Intent intent = new Intent("endRecording");
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
    }

    private void sendUpdateMessage(String title, Long time) {
        Intent intent = new Intent(title);
        if (time != null) {
            intent.putExtra("time", time);
        }
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
    }
}