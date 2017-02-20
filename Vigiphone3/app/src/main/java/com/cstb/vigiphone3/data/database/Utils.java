package com.cstb.vigiphone3.data.database;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

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

    public List<RecordingRow> getAllPaths(){
        return SQLite.select()
                .from(RecordingRow.class)
                .queryList();
    }

    public RecordingRow getPath(int id){
        return SQLite.select()
                .from(RecordingRow.class)
                .where(RecordingRow_Table.id.eq(id))
                .querySingle();
    }

    public static boolean isWifiConnected(Context context){
        ConnectivityManager connectivityManager = (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = connectivityManager.getActiveNetworkInfo();
        return netInfo != null && netInfo.getType() == ConnectivityManager.TYPE_WIFI && netInfo.isConnected();
    }
}