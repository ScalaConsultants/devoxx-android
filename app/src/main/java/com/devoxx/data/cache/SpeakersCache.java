package com.devoxx.data.cache;

import com.annimon.stream.Optional;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.EBean;

import java.lang.reflect.Type;
import java.util.List;
import java.util.concurrent.TimeUnit;

import com.devoxx.Configuration;
import com.devoxx.connection.model.SpeakerShortApiModel;

@EBean
public class SpeakersCache implements DataCache<List<SpeakerShortApiModel>, String> {

    private static final String SPEAKERS_CACHE_KEY = "speakers_cache_key";

    public static final long CACHE_LIFE_TIME_MS =
            TimeUnit.MINUTES.toMillis(Configuration.SPEAKERS_CACHE_LIFE_TIME_MINS);

    @Bean
    BaseCache baseCache;

    @Override
    public void upsert(List<SpeakerShortApiModel> rawData) {
        baseCache.upsert(serializeData(rawData), SPEAKERS_CACHE_KEY);
    }

    @Override
    public List<SpeakerShortApiModel> getData() {
        final Optional<String> optionalCache = baseCache.getData(SPEAKERS_CACHE_KEY);
        return deserializeData(optionalCache.orElse("[]"));
    }

    @Override
    public boolean isValid() {
        return baseCache.isValid(SPEAKERS_CACHE_KEY, CACHE_LIFE_TIME_MS);
    }

    @Override
    public void clearCache(String query) {
        baseCache.clearCache(SPEAKERS_CACHE_KEY);
    }

    @Override
    public void upsert(String rawData, String query) {
        throw new IllegalStateException("Not needed here!");
    }

    @Override
    public List<SpeakerShortApiModel> getData(String query) {
        throw new IllegalStateException("Not needed here!");
    }

    @Override
    public boolean isValid(String query) {
        throw new IllegalStateException("Not needed here!");
    }

    private List<SpeakerShortApiModel> deserializeData(String fromCache) {
        return new Gson().fromJson(fromCache, getType());
    }

    private String serializeData(List<SpeakerShortApiModel> data) {
        return new Gson().toJson(data);
    }

    private Type getType() {
        return new TypeToken<List<SpeakerShortApiModel>>() {
        }.getType();
    }
}
