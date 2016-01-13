package io.scalac.degree.data.cache;

import com.annimon.stream.Optional;

import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.EBean;

import io.realm.Realm;
import io.realm.RealmResults;
import io.scalac.degree.data.RealmProvider;
import io.scalac.degree.data.cache.model.CacheObject;

@EBean
public class BaseCache implements QueryAwareRawCache {

    @Bean
    RealmProvider realmProvider;

    @Override
    public void upsert(String rawData, String query) {
        final Realm realm = realmProvider.getRealm();
        final CacheObject object = realm
                .where(CacheObject.class)
                .equalTo(CacheObject.Contract.QUERY, query)
                .findFirst();

        if (object != null) {
            realm.beginTransaction();
            object.setRawData(rawData);
            object.setTimestamp(System.currentTimeMillis());
            realm.commitTransaction();
        } else {
            realm.beginTransaction();
            final CacheObject cacheObject = realm
                    .createObject(CacheObject.class);
            cacheObject.setRawData(rawData);
            cacheObject.setQuery(query);
            cacheObject.setTimestamp(System.currentTimeMillis());
            realm.commitTransaction();
        }

        realm.close();
    }

    @Override
    public Optional<String> getData(String query) {
        final Realm realm = realmProvider.getRealm();
        final CacheObject object = realm
                .where(CacheObject.class)
                .equalTo(CacheObject.Contract.QUERY, query)
                .findFirst();
        return Optional.ofNullable((object != null) ? object.getRawData() : null);
    }

    @Override
    public boolean isValid(String query, long timestanp) {
        final Realm realm = realmProvider.getRealm();
        final CacheObject object = realm
                .where(CacheObject.class)
                .equalTo(CacheObject.Contract.QUERY, query)
                .findFirst();
        final boolean isCacheAvailable = object != null;
        final long cacheTime = isCacheAvailable ? object.getTimestamp() : 0;

        return isCacheAvailable && (System.currentTimeMillis() -
                cacheTime < timestanp);
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
}
