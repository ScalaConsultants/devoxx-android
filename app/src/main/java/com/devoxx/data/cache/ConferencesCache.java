package com.devoxx.data.cache;

import com.annimon.stream.Optional;
import com.devoxx.Configuration;
import com.devoxx.connection.cfp.model.ConferenceApiModel;
import com.devoxx.utils.AssetsUtil;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.EBean;
import org.androidannotations.annotations.RootContext;

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

    @Bean
    AssetsUtil assetsUtil;

    @Override
    public void upsert(List<ConferenceApiModel> rawData) {
        baseCache.upsert(serializeData(rawData), CONFERENCES_CACHE_KEY);
    }

    @Override
    public List<ConferenceApiModel> getData() {
        final Optional<String> optionalCache = baseCache.getData(CONFERENCES_CACHE_KEY);
        return deserializeData(optionalCache.orElse(fallbackData()));
    }

    @Override
    public boolean isValid() {
        return baseCache.isValid(CONFERENCES_CACHE_KEY, CACHE_LIFE_TIME_MS);
    }

    @Override
    public void clearCache(String query) {
        baseCache.clearCache(CONFERENCES_CACHE_KEY);
    }

    public void initWithFallbackData() {
        clearCache(null);
        final List<ConferenceApiModel> confs = deserializeData(fallbackData());
        upsert(confs);
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

    private String fallbackData() {
        return assetsUtil.loadStringFromAssets("data/cfp.json");
    }

    private List<ConferenceApiModel> deserializeData(String fromCache) {
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
