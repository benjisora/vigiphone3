package com.cstb.vigiphone3.data.database;

import com.cstb.vigiphone3.data.model.RecordingRow;
import com.cstb.vigiphone3.data.model.RecordingRow_Table;
import com.raizlabs.android.dbflow.sql.language.SQLite;

import java.util.List;

/**
 * Created by Benjisora on 10/02/2017.
 */

public class Utils {

    private static Utils instanceUtils;

    public static Utils getinstance() {
        if (instanceUtils == null) {
            instanceUtils = new Utils();
        }
        return instanceUtils;
    }

    //QUERY: Path
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

/*
    //QUERY: Stop
    public List<Stop> getAllStops(){
        return SQLite.select()
                .from(Stop.class)
                .queryList();
    }

    public List<Stop> getStopForPath(int id){
        return SQLite.select()
                .from(Stop.class)
                .where(Stop_Table.id
                        .in(
                                SQLite.select()
                                        .from(StopGroups.class)
                                        .where(StopGroups_Table.id_line.eq(id))
                        )
                )
                .queryList();
    }

    public Stop getStop(double latitude, double longitude){
        return SQLite.select()
                .from(Stop.class)
                .where(Stop_Table.latitude.eq(latitude))
                .and(Stop_Table.longitude.eq(longitude))
                .querySingle();
    }

    //QUERY Favorite
    public List<Favorites> getFavorites(){
        return SQLite.select()
                .from(Favorites.class)
                .queryList();
    }

    public Boolean pathIsFav(int id){
        Favorites fav = SQLite.select()
                .from(Favorites.class)
                .where(Favorites_Table.id_path.eq(id))
                .querySingle();
        if(fav != null){
            return true;
        }
        return false;
    }*/
}