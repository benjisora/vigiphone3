package com.cstb.vigiphone3.data.database;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.v4.content.ContextCompat;
import android.util.Log;

import com.cstb.vigiphone3.network.NetworkService;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.mapbox.mapboxsdk.annotations.Icon;
import com.mapbox.mapboxsdk.annotations.IconFactory;
import com.raizlabs.android.dbflow.config.FlowConfig;
import com.raizlabs.android.dbflow.config.FlowManager;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * MyApplication class, used to create the retrofit and dbflow instances
 */
public class MyApplication extends Application {

    private static List<List<Icon>> icons;
    private static Retrofit retrofit;
    private static String url;
    private static SharedPreferences SP;

    //region intent messages
    public static final String locationChangedFromLocationService = "com.cstb.vigiphone3.service.locationService.locationChanged";
    public static final String sensorChangedFromSensorService = "com.cstb.vigiphone3.service.sensorService.sensorChanged";
    public static final String signalChangedFromSignalService = "com.cstb.vigiphone3.service.signalService.signalChanged";
    public static final String updateTimeFromRecordService = "com.cstb.vigiphone3.service.recordService.updateTime";
    public static final String updateViewFromServiceManager = "com.cstb.vigiphone3.service.serviceManager.updateView";
    public static final String saveRowFromRecordService = "com.cstb.vigiphone3.service.recordService.saveRow";
    public static final String stopRecordingFromRecordingFragment = "com.cstb.vigiphone3.fragment.recordingFragment.stopRecording";
    public static final String endRecordingFromRecordService = "com.cstb.vigiphone3.service.recordService.endRecording";
    public static final String updateMarkerFromServiceManager = "com.cstb.vigiphone3.service.serviceManager.updateMarker";
    public static final String updateDatabaseFromRecordService = "com.cstb.vigiphone3.service.recordService.updateDatabase";
    //endregion

    /**
     * Initializes the Database and the icons
     */
    @Override
    public void onCreate() {
        super.onCreate();
        SP = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
        url = SP.getString("url", "https://32a77ce1.ngrok.io/");
        FlowManager.init(new FlowConfig.Builder(this).build());
        icons = null;
        loadIcons(this.getApplicationContext());
    }

    /**
     * Creates a retrofit instance with a GsonParser
     *
     * @return The retrofit instance
     */
    public static Retrofit getRetrofitInstance() {
        if (retrofit == null) {
            Gson gson = new GsonBuilder().create();
            retrofit = new Retrofit.Builder()
                    .baseUrl(url)
                    .addConverterFactory(GsonConverterFactory.create(gson))
                    .build();
        }
        return retrofit;
    }

    /**
     * Creates the networkService implementation
     *
     * @return The said implementation
     */
    public static NetworkService getNetworkServiceInstance() {
        return getRetrofitInstance().create(NetworkService.class);
    }

    /**
     * Initializes the marker icons for the map
     *
     * @param context The caller's context
     */
    private void loadIcons(final Context context) {

        final Handler iconHanlder = new Handler();
        final Runnable runnable = new Runnable() {
            @Override
            public void run() {

                ArrayList<String> colorNames = new ArrayList<>();
                colorNames.add("red");
                colorNames.add("blue");
                colorNames.add("orange");
                colorNames.add("gray");
                colorNames.add("rank0");
                colorNames.add("rank1");
                colorNames.add("rank2");
                colorNames.add("rank3");

                for (int nameIndex = 0; nameIndex < colorNames.size(); nameIndex++) {
                    ArrayList<Icon> list = new ArrayList<>();
                    for (int degree = 0; degree <= 360; degree++) {
                        try {
                            list.add(IconFactory.getInstance(getApplicationContext())
                                    .fromDrawable(
                                            ContextCompat.getDrawable(
                                                    context,
                                                    context.getResources().getIdentifier(colorNames.get(nameIndex) + degree, "drawable", context.getPackageName())
                                            )
                                    )
                            );
                        } catch (Exception e) {
                            Log.e("MyApplication", "Can't find ressource at colorindex " + nameIndex + " and degree " + degree + " : ", e);
                        }
                    }
                    icons.add(list);
                }
            }
        };
        iconHanlder.post(runnable);
    }

    //region getters/setters

    public static boolean isRecording() {
        return SP.getBoolean("isRecording", false);
    }

    public static void setRecording(Boolean value) {
        SP.edit().putBoolean("isRecording", value).apply();
    }

    public static boolean isRecordingInfinitely() {
        return SP.getBoolean("recordingInfinitely", true);
    }

    public static void setRecordingInfinitely(Boolean value) {
        SP.edit().putBoolean("recordingInfinitely", value).apply();
    }

    public static boolean isSavingOnServer() {
        return SP.getBoolean("saveOnServer", true);
    }

    public static void setSavingOnServer(Boolean value) {
        SP.edit().putBoolean("saveOnServer", value).apply();
    }

    public static List<List<Icon>> getIcons() {
        return icons;
    }

    public static long getRecordTime() {
        return SP.getLong("recordTime", 10000);
    }

    public static void setRecordTime(long value) {
        SP.edit().putLong("recordTime", value).apply();
    }

    public static long getStep() {
        return SP.getLong("stepTime", 1000);
    }

    public static void setStep(long value) {
        SP.edit().putLong("stepTime", value).apply();
    }

    public static long getThreshold() {
        return SP.getLong("threshold", 100);
    }

    public static void setThreshold(long value) {
        SP.edit().putLong("threshold", value).apply();
    }

    public static String getFileName() {
        return SP.getString("fileName", "default");
    }

    public static void setFileName(String value) {
        SP.edit().putString("fileName", value).apply();
    }

    public static String getRecordingTableName() {
        return SP.getString("RecordingTable", "default_table");
    }

    public static String getEmittersTableName() {
        return SP.getString("MapTable", "emitters_table");
    }

    //endregion
}