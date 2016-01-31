package com.devoxx.data.cache;

import com.annimon.stream.Optional;
import com.devoxx.Configuration;
import com.devoxx.connection.cfp.model.ConferenceApiModel;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.EBean;

import java.lang.reflect.Type;
import java.util.List;
import java.util.concurrent.TimeUnit;

@EBean
public class ConferencesCache implements DataCache<List<ConferenceApiModel>, String> {

    private static final String CONFERENCES_CACHE_KEY = "conferences_cache_key";

    public static final long CACHE_LIFE_TIME_MS =
            TimeUnit.MINUTES.toMillis(Configuration.CONFERENCES_CACHE_LIFE_TIME_MINS);

    @Bean
    BaseCache baseCache;

    @Override
    public void upsert(List<ConferenceApiModel> rawData) {
        baseCache.upsert(serializeData(rawData), CONFERENCES_CACHE_KEY);
    }

    @Override
    public List<ConferenceApiModel> getData() {
        final Optional<String> optionalCache = baseCache.getData(CONFERENCES_CACHE_KEY);
        // TODO Handle error from file?
        return deserializeData(optionalCache.orElse("[]"));
    }

    @Override
    public boolean isValid() {
        return baseCache.isValid(CONFERENCES_CACHE_KEY, CACHE_LIFE_TIME_MS);
    }

    @Override
    public void clearCache(String query) {
        baseCache.clearCache(CONFERENCES_CACHE_KEY);
    }

    @Override
    public void upsert(String rawData, String query) {
        throw new IllegalStateException("Not needed here!");
    }

    @Override
    public List<ConferenceApiModel> getData(String query) {
        throw new IllegalStateException("Not needed here!");
    }

    @Override
    public boolean isValid(String query) {
        throw new IllegalStateException("Not needed here!");
    }

    public List<ConferenceApiModel> deserializeData(String fromCache) {
        return new Gson().fromJson(fromCache, getType());
    }

    private String serializeData(List<ConferenceApiModel> data) {
        return new Gson().toJson(data);
    }

    private Type getType() {
        return new TypeToken<List<ConferenceApiModel>>() {
        }.getType();
    }
}
