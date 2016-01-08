package io.scalac.degree.data.cache;

import android.content.Context;

import com.google.gson.Gson;

import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.EBean;
import org.androidannotations.annotations.RootContext;

import java.util.concurrent.TimeUnit;

import io.realm.Realm;
import io.scalac.degree.Configuration;
import io.scalac.degree.connection.model.SpeakerFullApiModel;
import io.scalac.degree.data.RealmProvider;
import io.scalac.degree.data.cache.model.CacheSpeakerObject;

@EBean
public class SpeakerCache implements DataCache<SpeakerFullApiModel, String> {

    public static final long CACHE_LIFE_TIME_MS =
            TimeUnit.MINUTES.toMillis(Configuration.SPEAKERS_CACHE_LIFE_TIME_MINS);

    @RootContext
    Context context;
    @Bean
    RealmProvider realmProvider;

    @Override
    public void upsert(String rawData, String query) {
        final Realm realm = realmProvider.getRealm();
        final CacheSpeakerObject object = realm.where(CacheSpeakerObject.class).
                equalTo(CacheSpeakerObject.Contract.QUERY, query).findFirst();
        if (object != null) {
            realm.beginTransaction();
            object.setRawData(rawData);
            object.setTimestamp(System.currentTimeMillis());
            realm.commitTransaction();
        } else {
            realm.beginTransaction();
            final CacheSpeakerObject cacheObject = realm
                    .createObject(CacheSpeakerObject.class);
            cacheObject.setRawData(rawData);
            cacheObject.setQuery(query);
            cacheObject.setTimestamp(System.currentTimeMillis());
            realm.commitTransaction();
        }
    }

    @Override
    public SpeakerFullApiModel getData() {
        throw new IllegalStateException("Not needed here!");
    }

    @Override
    public SpeakerFullApiModel getData(String query) {
        final Realm realm = realmProvider.getRealm();
        final String rawData = realm.where(CacheSpeakerObject.class)
                .equalTo(CacheSpeakerObject.Contract.QUERY, query).findFirst().getRawData();
        return new Gson().fromJson(rawData, SpeakerFullApiModel.class);
    }

    @Override
    public boolean isValid() {
        throw new IllegalStateException("Not needed here!");
    }

    @Override
    public boolean isValid(String query) {
        final Realm realm = realmProvider.getRealm();
        final CacheSpeakerObject cacheObject = realm
                .where(CacheSpeakerObject.class)
                .equalTo(CacheSpeakerObject.Contract.QUERY, query).findFirst();
        final boolean isCacheAvailable = cacheObject != null;
        return isCacheAvailable && (System.currentTimeMillis() -
                cacheObject.getTimestamp() < CACHE_LIFE_TIME_MS);
    }

    @Override
    public void clearCache() {
        final Realm realm = realmProvider.getRealm();
        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                realm.allObjects(CacheSpeakerObject.class).clear();
            }
        });
    }
}
