package io.scalac.degree.data.cache;

import android.content.Context;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.EBean;
import org.androidannotations.annotations.RootContext;

import java.lang.reflect.Type;
import java.util.List;
import java.util.concurrent.TimeUnit;

import io.realm.Realm;
import io.scalac.degree.connection.model.SlotApiModel;
import io.scalac.degree.data.RealmProvider;
import io.scalac.degree.data.cache.model.SlotsCacheObject;

/**
 * www.scalac.io
 * jacek.modrakowski@scalac.io
 * 30/10/2015
 */
@EBean
public class SlotsCache implements DataCache<List<SlotApiModel>, String> {

    private static final long CACHE_LIFE_TIME_MS =
            TimeUnit.MINUTES.toMillis(5);

    @RootContext
    Context context;
    @Bean
    RealmProvider realmProvider;

    @Override
    public void storeData(String rawData, String query) {
        clearCache();

        final Realm realm = realmProvider.getRealm();
        realm.beginTransaction();
        final SlotsCacheObject cacheObject = realm
                .createObject(SlotsCacheObject.class);
        cacheObject.setRawData(rawData);
        cacheObject.setTimestamp(System.currentTimeMillis());
        realm.commitTransaction();
    }

    @Override
    public List<SlotApiModel> getData() {
        final Realm realm = realmProvider.getRealm();
        final SlotsCacheObject cacheObject = realm
                .where(SlotsCacheObject.class).findFirst();
        final String rawData = cacheObject.getRawData();
        return new Gson().fromJson(rawData, getType());
    }

    @Override
    public List<SlotApiModel> getData(String query) {
        throw new IllegalStateException("Not needed here!");
    }

    @Override
    public boolean isValid() {
        final Realm realm = realmProvider.getRealm();
        final SlotsCacheObject cacheObject = realm
                .where(SlotsCacheObject.class).findFirst();
        final boolean isObjectAvailable = cacheObject != null;
        return isObjectAvailable && (System.currentTimeMillis() -
                cacheObject.getTimestamp() < CACHE_LIFE_TIME_MS);
    }

    @Override
    public boolean isValid(String query) {
        throw new IllegalStateException("Not needed here!");
    }

    @Override
    public void clearCache() {
        final Realm realm = realmProvider.getRealm();
        realm.beginTransaction();
        realm.where(SlotsCacheObject.class).findAll().clear();
        realm.commitTransaction();
    }

    private Type getType() {
        return new TypeToken<List<SlotApiModel>>() {
        }.getType();
    }

}