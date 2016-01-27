package com.devoxx.connection.cfp;

import com.devoxx.connection.cfp.model.ConferenceApiModel;

import java.util.List;

import retrofit.Call;
import retrofit.http.GET;

public interface CfpApi {
    @GET("/cfpdevoxx/cfp.json")
    Call<List<ConferenceApiModel>> conferences();
}
