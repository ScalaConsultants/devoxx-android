package com.devoxx.data.cache;

import com.annimon.stream.Optional;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.devoxx.connection.model.SpeakerFullApiModel;

import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.EBean;

import android.text.TextUtils;

import java.lang.reflect.Type;
import java.util.concurrent.TimeUnit;

import com.devoxx.Configuration;

@EBean
public class SpeakerCache implements DataCache<SpeakerFullApiModel, String> {

    public static final long CACHE_LIFE_TIME_MS =
            TimeUnit.MINUTES.toMillis(Configuration.SPEAKERS_CACHE_LIFE_TIME_MINS);

    @Bean
    BaseCache baseCache;

    @Override
    public void upsert(SpeakerFullApiModel model) {
        baseCache.upsert(serializeData(model), model.uuid);
    }

    @Override
    public SpeakerFullApiModel getData(String query) {
        final Optional<String> optionalData = baseCache.getData(query);
        return optionalData.isPresent() ? deserializeData(optionalData.get()) : null;
    }

    @Override
    public boolean isValid(String query) {
        return baseCache.isValid(query, CACHE_LIFE_TIME_MS);
    }

    @Override
    public void clearCache(String query) {
        baseCache.clearCache(query);
    }

    @Override
    public void upsert(String rawData, String query) {
        baseCache.upsert(rawData, query);
    }

    @Override
    public boolean isValid() {
        throw new IllegalStateException("Not needed here!");
    }

    @Override
    public SpeakerFullApiModel getData() {
        throw new IllegalStateException("Not needed here!");
    }

    private SpeakerFullApiModel deserializeData(String fromCache) {
        if (!TextUtils.isEmpty(fromCache)) {
            return new Gson().fromJson(fromCache, getType());
        } else {
            return null;
        }
    }

    private String serializeData(SpeakerFullApiModel data) {
        return new Gson().toJson(data);
    }

    private Type getType() {
        return new TypeToken<SpeakerFullApiModel>() {
        }.getType();
    }
}
