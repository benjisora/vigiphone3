package com.cstb.vigiphone3.network;

import com.cstb.vigiphone3.data.model.Emitters;
import com.cstb.vigiphone3.data.model.RecordingRow;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

/**
 *
 */
public interface NetworkService {

    /**
     * Retrieves the emitters from the server
     *
     * @param tableName   The table name
     * @param CID         The CellID
     * @param LAC         The Location Area Code
     * @param MCC         The Mobile Country Code
     * @param MNC         The Mobile Network Code
     * @param networkType The Network Type
     * @param north       The north coordinate visible on the map
     * @param south       The south coordinate visible on the map
     * @param east        The east coordinate visible on the map
     * @param west        The west coordinate visible on the map
     * @return A list of emitters
     */
    @FormUrlEncoded
    @POST("get_emitters.php")
    Call<Emitters> getEmitters(@Field("TABLE_NAME") String tableName,
                               @Field("CELL_ID") int CID,
                               @Field("LOCATION_AREA_CODE") int LAC,
                               @Field("MOBILE_COUNTRY_CODE") int MCC,
                               @Field("MONIBLE_NETWORK_CODE") int MNC,
                               @Field("NETWORK_TYPE") String networkType,
                               @Field("NORTH") double north,
                               @Field("SOUTH") double south,
                               @Field("EAST") double east,
                               @Field("WEST") double west);

    /**
     * Sends the RecordingRow to the server
     *
     * @param row The row to send
     * @return A row, unused
     */
    @POST("send_recordings.php")
    Call<RecordingRow> sendRecording(@Body RecordingRow row);

}
