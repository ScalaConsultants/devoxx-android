package com.devoxx.data.downloader;

import android.content.Context;

import com.devoxx.connection.Connection;
import com.devoxx.connection.cfp.model.ConferenceApiModel;
import com.devoxx.data.cache.ConferencesCache;
import com.devoxx.data.conference.model.ConferenceDay;
import com.devoxx.utils.AssetsUtil;

import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.EBean;
import org.androidannotations.annotations.RootContext;

import java.io.IOException;
import java.util.List;

import retrofit.Call;
import retrofit.Response;

@EBean
public class ConferenceDownloader {

    @RootContext
    Context context;

    @Bean
    Connection connection;

    @Bean
    ConferencesCache conferencesCache;

    @Bean
    AssetsUtil assetsUtil;

    public void warmUp() {
        conferencesCache.clearCache(null);
        final String rawConferences = assetsUtil.loadStringFromAssets(context, "data/cfp.json");
        final List<ConferenceApiModel> confs = conferencesCache.deserializeData(rawConferences);
        conferencesCache.upsert(confs);
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
