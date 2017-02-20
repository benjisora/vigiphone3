package com.cstb.vigiphone3.network;

import com.cstb.vigiphone3.data.model.RecordingRow;
import com.cstb.vigiphone3.data.model.RecordingRows;

import java.util.List;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;

public interface NetworkService {

    @GET("recordings")
    Call<RecordingRows> getAllRecordingRows();

    @POST("save_recording")
    Call<RecordingRow> sendRecordings(@Body RecordingRow row);

}
