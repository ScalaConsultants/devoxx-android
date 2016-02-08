package com.devoxx.data.downloader;

import com.devoxx.connection.Connection;
import com.devoxx.connection.cfp.model.ConferenceApiModel;
import com.devoxx.data.cache.ConferencesCache;

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

    @Bean
    ConferencesCache conferencesCache;

    public void initWitStaticData() {
        conferencesCache.initWithFallbackData();
    }

    public List<ConferenceApiModel> fetchAllConferences() throws IOException {
        final List<ConferenceApiModel> result;
        if (!conferencesCache.isValid()) {
            final Call<List<ConferenceApiModel>> call = connection.getCfpApi().conferences();
            final Response<List<ConferenceApiModel>> response = call.execute();
            result = response.body();
            conferencesCache.upsert(result);
        } else {
            result = conferencesCache.getData();
        }
        return result;
    }
}
