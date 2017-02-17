package com.cstb.vigiphone3.data.database;

import android.app.Application;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.cstb.vigiphone3.network.NetworkService;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.raizlabs.android.dbflow.config.FlowConfig;
import com.raizlabs.android.dbflow.config.FlowManager;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by Benjisora on 17/02/2017.
 */

public class MyApplication extends Application {

    private static Retrofit retrofit;
    private static String url;

    @Override
    public void onCreate() {
        super.onCreate();
        SharedPreferences SP = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
        url = SP.getString("url", "https://079fcb60.ngrok.io/");

        FlowManager.init(new FlowConfig.Builder(this).build());
    }

    public static Retrofit getRetrofitInstance() {
        if(retrofit == null) {
            Gson gson = new GsonBuilder().create();
            retrofit  = new Retrofit.Builder()
                    .baseUrl(url)
                    .addConverterFactory(GsonConverterFactory.create(gson))
                    .build();
        }
        return retrofit;
    }

    public static NetworkService getNetworkServiceInstance() {
        return getRetrofitInstance().create(NetworkService.class);
    }
}