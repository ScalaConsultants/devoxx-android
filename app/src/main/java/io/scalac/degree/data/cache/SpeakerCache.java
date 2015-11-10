package io.scalac.degree.data.cache;

import com.google.gson.Gson;

import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.EBean;
import org.androidannotations.annotations.RootContext;

import android.content.Context;

import java.util.concurrent.TimeUnit;

import io.realm.Realm;
import io.scalac.degree.connection.model.SpeakerFullApiModel;
import io.scalac.degree.data.RealmProvider;
import io.scalac.degree.data.cache.model.SpeakerCacheObject;

/**
 * www.scalac.io
 * jacek.modrakowski@scalac.io
 * 30/10/2015
 */
@EBean
public class SpeakerCache implements DataCache<SpeakerFullApiModel, String> {

	private static final long CACHE_LIFE_TIME_MS =
			TimeUnit.SECONDS.toMillis(30);

	@RootContext Context context;
	@Bean RealmProvider realmProvider;

	@Override public void storeData(String rawData, String query) {
		final Realm realm = realmProvider.getRealm();
		realm.beginTransaction();
		final SpeakerCacheObject cacheObject = realm
				.createObject(SpeakerCacheObject.class);
		cacheObject.setRawData(rawData);
		cacheObject.setQuery(query);
		cacheObject.setTimestamp(System.currentTimeMillis());
		realm.commitTransaction();
	}

	@Override public SpeakerFullApiModel getData() {
		throw new IllegalStateException("Not needed here!");
	}

	@Override public SpeakerFullApiModel getData(String query) {
		final Realm realm = realmProvider.getRealm();
		final String rawData = realm.where(SpeakerCacheObject.class)
				.equalTo("query", query).findFirst().getRawData();
		return new Gson().fromJson(rawData, SpeakerFullApiModel.class);
	}

	@Override public boolean isValid() {
		throw new IllegalStateException("Not needed here!");
	}

	@Override public boolean isValid(String query) {
		final Realm realm = realmProvider.getRealm();
		final SpeakerCacheObject cacheObject = realm
				.where(SpeakerCacheObject.class)
				.equalTo("query", query).findFirst();
		final boolean isCacheAvailable = cacheObject != null;
		return isCacheAvailable && (System.currentTimeMillis() -
				cacheObject.getTimestamp() < CACHE_LIFE_TIME_MS);
	}

	@Override public void clearCache() {

	}
}
