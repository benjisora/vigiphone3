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
import okhttp3.internal.Util;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RecordService extends Service {

    private final Handler recordHandler = new Handler();
    private final Handler saveHandler = new Handler();
    private Runnable saveRunnableCode;
    private NetworkService networkService;
    private Call<RecordingRow> sendRecordings = null;
    private SharedPreferences SP;
    private PugNotification notification;
    private Load load;

    private BroadcastReceiver mStartReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            load.simple().build();
            saveDataPeriodically();
        }
    };

    private BroadcastReceiver mStopReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.d("RecordService", "stop recording");
            SP.edit().putBoolean("isRecording", false).apply();
            recordHandler.removeCallbacksAndMessages(null);
            load.message("Recording and transfer ongoing...").simple().build();
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
                sendToServer();
            }
        };

        load = notification.load().identifier(1)
                .ongoing(true)
                .title(R.string.app_name)
                .message("Recording ongoing...")
                .smallIcon(R.mipmap.ic_launcher)
                .largeIcon(R.mipmap.ic_launcher)
                .autoCancel(true)
                .click(MainActivity.class)
                .priority(NotificationCompat.PRIORITY_HIGH);

        return Service.START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d("RecordService", "service stopped");
        stopListening();
        notification.cancel(1);
    }

    private void startListening() {
        LocalBroadcastManager.getInstance(this).registerReceiver(mStartReceiver, new IntentFilter("startRecording"));
        LocalBroadcastManager.getInstance(this).registerReceiver(mStopReceiver, new IntentFilter("stopRecording"));
    }

    private void stopListening() {
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mStartReceiver);
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mStopReceiver);
    }

    private void sendToServer() {

        if (Utils.canWeConnect(RecordService.this)) {
/*
            while(Utils.getinstance().getRecordingRowsCount()!=0){
                sendRecordings = networkService.sendRecording(Utils.getinstance().getAllRecordingRows().get(0));
                sendRecordings.enqueue(new Callback<RecordingRow>() {
                    @Override
                    public void onResponse(Call<RecordingRow> call, Response<RecordingRow> response) {
                        Log.d("RecordService", String.valueOf(response.code()) + " : " + Utils.getinstance().getAllRecordingRows().get(0).getId());
                        Utils.getinstance().getAllRecordingRows().get(0).delete();
                    }

                    @Override
                    public void onFailure(Call<RecordingRow> call, Throwable t) {
                        Log.e("RecordService", "Problem during the call");
                    }
                });
            }
            Log.d("RecordService", "TOUT EST ENVOYE");
            notification.cancel(1);
*/
            //TODO : ENVOYER TOUTES LES ROWS, ET LES DELETE UNE A UNE
        } else {
            Log.e("RecordService", "No connection available");
        }

/*
            final int[] i = {0};
                sendRecordings = networkService.sendRecording(rows.get(0));
                sendRecordings.enqueue(new Callback<RecordingRow>() {
                    @Override
                    public void onResponse(Call<RecordingRow> call, Response<RecordingRow> response) {
                        Log.d("RecordService", String.valueOf(response.code()));

                        if(rows.isEmpty()){
                            Log.d("RecordService", "TOUT EST PARTI");
                            notification.cancel(1);
                        } else {
                            rows.get(0).delete();
                            sendRecordings = networkService.sendRecording(rows.get(0));
                            sendRecordings.enqueue(this);
                        }

                    }

                    @Override
                    public void onFailure(Call<RecordingRow> call, Throwable t) {
                        Log.e("RecordService", "Problem during the call");
                    }
                });
*/
    }

    public void saveDataPeriodically() {




        final Long startTimeStamp = System.currentTimeMillis();
        Runnable runnableCode = new Runnable() {
            @Override
            public void run() {
                boolean shouldSave = (SP.getBoolean("isRecording", false) && (SP.getBoolean("recordingInfinitely", true) || (!SP.getBoolean("recordingInfinitely", true) && System.currentTimeMillis() < startTimeStamp + SP.getInt("recordTime", 10000))));
                if (shouldSave) {

                    if (Utils.getinstance().getRecordingRowsCount() == SP.getInt("threshold", 100)) {
                        //TODO: SEND DATA
                        load.message("Recording and transfer ongoing...").simple().build();
                        recordHandler.post(saveRunnableCode);
                    }

                    sendUpdateMessage("saveRecordingRow", System.currentTimeMillis());
                    sendUpdateMessage("updateTime", System.currentTimeMillis() - startTimeStamp);
                    recordHandler.postDelayed(this, SP.getInt("stepTime", 1000));

                } else {
                    Log.d("RecordService", "stop the recording");
                    SP.edit().putBoolean("isRecording", false).apply();
                    recordHandler.removeCallbacks(this);
                    load.message("Transfer ongoing...").simple().build();
                    recordHandler.post(saveRunnableCode);
                }
            }
        };
        recordHandler.post(runnableCode);
    }

    private void sendUpdateMessage(String title, Long time) {
        Intent intent = new Intent(title);
        intent.putExtra("time", time);
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
    }
}