package com.devoxx.data.cache;

import com.annimon.stream.Optional;
import com.devoxx.data.RealmProvider;
import com.devoxx.data.cache.model.CacheObject;

import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.EBean;

import io.realm.Realm;
import io.realm.RealmResults;

@EBean
public class BaseCache implements QueryAwareRawCache {

    @Bean
    RealmProvider realmProvider;

    @Override
    public void upsert(String rawData, String query) {
        final Realm realm = realmProvider.getRealm();
        CacheObject cacheObject = fetchCacheObject(realm, query);

        realm.beginTransaction();
        if (cacheObject == null || !cacheObject.isValid()) {
            cacheObject = realm.createObject(CacheObject.class);
            cacheObject.setQuery(query);
        }
        cacheObject.setRawData(rawData);
        cacheObject.setTimestamp(System.currentTimeMillis());
        realm.commitTransaction();

        realm.close();
    }

    @Override
    public Optional<String> getData(String query) {
        final Realm realm = realmProvider.getRealm();
        final CacheObject object = fetchCacheObject(realm, query);
        final Optional<String> result = Optional.ofNullable((object != null && object.isValid())
                ? object.getRawData() : null);

        realm.close();

        return result;
    }

    @Override
    public boolean isValid(String query, long timestamp) {
        final Realm realm = realmProvider.getRealm();
        final CacheObject object = fetchCacheObject(realm, query);
        final boolean isCacheAvailable = object != null && object.isValid();
        final long cacheTime = isCacheAvailable ? object.getTimestamp() : 0;
        realm.close();

        return isCacheAvailable && (System.currentTimeMillis() -
                cacheTime < timestamp);
    }

    @Override
    public void clearCache(String query) {
        final Realm realm = realmProvider.getRealm();
        final RealmResults<CacheObject> object = realm
                .where(CacheObject.class)
                .equalTo(CacheObject.Contract.QUERY, query)
                .findAll();
        realm.beginTransaction();
        object.clear();
        realm.commitTransaction();
        realm.close();
    }

    private CacheObject fetchCacheObject(Realm realm, String query) {
        final CacheObject local = realm.where(CacheObject.class)
                .equalTo(CacheObject.Contract.QUERY, query)
                .findFirst();
        return local != null && local.isValid() ? local : null;
    }

    public void clearAllCache() {
        final Realm realm = realmProvider.getRealm();
        realm.beginTransaction();
        realm.allObjects(CacheObject.class).clear();
        realm.commitTransaction();
        realm.close();
    }
}
