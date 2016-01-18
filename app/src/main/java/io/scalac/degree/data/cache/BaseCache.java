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
        CacheObject cacheObject = realm
                .where(CacheObject.class)
                .equalTo(CacheObject.Contract.QUERY, query)
                .findFirst();

        realm.beginTransaction();
        if (cacheObject == null) {
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
        final CacheObject object = realm
                .where(CacheObject.class)
                .equalTo(CacheObject.Contract.QUERY, query)
                .findFirst();

        final Optional<String> result = Optional.ofNullable((object != null)
                ? object.getRawData() : null);

        realm.close();

        return result;
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
        realm.close();

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
