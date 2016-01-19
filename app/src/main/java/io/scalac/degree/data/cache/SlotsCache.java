package io.scalac.degree.data.cache;

import com.annimon.stream.Optional;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.EBean;

import java.lang.reflect.Type;
import java.util.List;
import java.util.concurrent.TimeUnit;

import io.scalac.degree.Configuration;
import io.scalac.degree.connection.model.SlotApiModel;

@EBean
public class SlotsCache implements DataCache<List<SlotApiModel>, String> {

    private static final String SLOTS_KEY_SUFFIX = "slots_key_suffix";

    private static final long CACHE_LIFE_TIME_MS =
            TimeUnit.MINUTES.toMillis(Configuration.SLOTS_CACHE_TIME_MIN);

    @Bean
    BaseCache baseCache;

    @Override
    public void upsert(String rawData, String query) {
        clearCache(query);
        baseCache.upsert(rawData, getCacheKey(query));
    }

    @Override
    public List<SlotApiModel> getData(String query) {
        final Optional<String> optionalData = baseCache.getData(getCacheKey(query));
        return deserialize(optionalData.orElse("[]"));
    }

    @Override
    public boolean isValid(String query) {
        return baseCache.isValid(getCacheKey(query), CACHE_LIFE_TIME_MS);
    }

    @Override
    public void clearCache(String query) {
        baseCache.clearCache(getCacheKey(query));
    }

    @Override
    public List<SlotApiModel> getData() {
        throw new IllegalStateException("Not needed here!");
    }

    @Override
    public boolean isValid() {
        throw new IllegalStateException("Not needed here!");
    }


    @Override
    public void upsert(List<SlotApiModel> rawData) {
        throw new IllegalStateException("Not needed here!");
    }

    private Type getType() {
        return new TypeToken<List<SlotApiModel>>() {
        }.getType();
    }

    private List<SlotApiModel> deserialize(String data) {
        return new Gson().fromJson(data, getType());
    }

    private String getCacheKey(String query) {
        return String.format("%s_%s", query, SLOTS_KEY_SUFFIX);
    }
}
