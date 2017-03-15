package com.cstb.vigiphone3.data.database;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.preference.PreferenceManager;

import com.cstb.vigiphone3.data.model.Emitter;
import com.cstb.vigiphone3.data.model.RecordingRow;
import com.cstb.vigiphone3.data.model.RecordingRow_Table;
import com.raizlabs.android.dbflow.sql.language.SQLite;

import java.util.List;


public class Utils {

    private static Utils instanceUtils;

    public static Utils getinstance() {
        if (instanceUtils == null) {
            instanceUtils = new Utils();
        }
        return instanceUtils;
    }

    public List<RecordingRow> getAllRecordingRows(){
        return SQLite.select()
                .from(RecordingRow.class)
                .queryList();
    }

    public List<Emitter> getAllEmitters(){
        return SQLite.select()
                .from(Emitter.class)
                .queryList();
    }

    public long getEmittersCount(){
        return SQLite.selectCountOf().from(Emitter.class).count();
    }


    public long getRecordingRowsCount(){
        return SQLite.selectCountOf().from(RecordingRow.class).count();
    }

    public RecordingRow getRecordingRow(int id){
        return SQLite.select()
                .from(RecordingRow.class)
                .where(RecordingRow_Table.id.eq(id))
                .querySingle();
    }

    private static boolean isWifiEnabled(Context context){
        ConnectivityManager connectivityManager = (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = connectivityManager.getActiveNetworkInfo();
        return netInfo != null && netInfo.getType() == ConnectivityManager.TYPE_WIFI && netInfo.isConnected();
    }

    private static boolean isDataEnabled(Context context){
        ConnectivityManager connectivityManager = (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = connectivityManager.getActiveNetworkInfo();
        return netInfo != null && netInfo.getType() == ConnectivityManager.TYPE_MOBILE && netInfo.isConnected();
    }

    public static boolean canWeConnect(Context context) {
        SharedPreferences SP = PreferenceManager.getDefaultSharedPreferences(context);
        boolean test = SP.getBoolean("wifiOnly", false);

        return test && isWifiEnabled(context) || (!test && (isWifiEnabled(context) || isDataEnabled(context)));
    }
}