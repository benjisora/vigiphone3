package com.cstb.vigiphone3.ui;

import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.cstb.vigiphone3.R;
import com.cstb.vigiphone3.data.database.MyApplication;
import com.cstb.vigiphone3.data.model.RecordingRow;
import com.cstb.vigiphone3.data.model.RecordingRows;
import com.cstb.vigiphone3.network.NetworkService;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SplashScreen extends AppCompatActivity {

    private Call<RecordingRows> call = null;
    private Call<RecordingRow> sendRecordings = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        startIntentAndFinish(new Intent(SplashScreen.this, MainActivity.class));

/*
        if (shouldWeUpdate()) {
            NetworkService networkService = MyApplication.getNetworkServiceInstance();
            call = networkService.getAllRecordingRows();
            call.enqueue(new Callback<RecordingRows>() {
                @Override
                public void onResponse(Call<RecordingRows> call, Response<RecordingRows> response) {
                    updateSucceeded(response);
                }

                @Override
                public void onFailure(Call<RecordingRows> call, Throwable t) {
                    updateFailed(t);
                }
            });

            RecordingRow row = new RecordingRow();
            sendRecordings = networkService.sendRecordings(row);
            sendRecordings.enqueue(new Callback<RecordingRow>() {
                @Override
                public void onResponse(Call<RecordingRow> call, Response<RecordingRow> response) {
                    Log.d("sendRecordings", String.valueOf(response.code()));
                }

                @Override
                public void onFailure(Call<RecordingRow> call, Throwable t) {
                    updateFailed(t);
                }
            });

        } else {
            startIntentAndFinish(new Intent(SplashScreen.this, MainActivity.class));
        }
*/
    }

    public boolean shouldWeUpdate() {
        SharedPreferences SP = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
        return SP.getBoolean("udpates", true);
    }

    public void updateSucceeded(Response<RecordingRows> response) {

        if(response != null){
            RecordingRows p = response.body();
            for(RecordingRow row : p.rows){
                row.save();
            }
        }

        startIntentAndFinish(new Intent(SplashScreen.this, MainActivity.class));
    }

    public void updateFailed(Throwable t) {
        Log.e("RetrofitError", getString(R.string.log_error), t);
        Toast.makeText(this, R.string.error_fetching_data, Toast.LENGTH_SHORT).show();

        startIntentAndFinish(new Intent(SplashScreen.this, MainActivity.class));
    }

    public void startIntentAndFinish(Intent intent) {
        startActivity(intent);
        finish();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        if (call != null) {
            call.cancel();
        }
        if(sendRecordings!=null){
            sendRecordings.cancel();
        }
    }
}
