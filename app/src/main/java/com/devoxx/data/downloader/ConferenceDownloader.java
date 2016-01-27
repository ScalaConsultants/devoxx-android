package com.devoxx.data.downloader;

import com.devoxx.connection.Connection;
import com.devoxx.connection.cfp.model.ConferenceApiModel;

import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.EBean;

import java.io.IOException;
import java.util.List;

import retrofit.Call;
import retrofit.Response;

@EBean
public class ConferenceDownloader {

    @Bean
    Connection connection;

    public List<ConferenceApiModel> fetchAllConferences() throws IOException {
        final Call<List<ConferenceApiModel>> call = connection.getCfpApi().conferences();
        final Response<List<ConferenceApiModel>> response = call.execute();
        return response.body();
    }
}
