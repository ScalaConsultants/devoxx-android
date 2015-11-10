package io.scalac.degree.data.dao;

import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.EBean;
import org.androidannotations.annotations.RootContext;

import android.content.Context;

import io.realm.Realm;
import io.scalac.degree.data.RealmProvider;
import io.scalac.degree.data.model.SpeakerDbModel;

/**
 * www.scalac.io
 * jacek.modrakowski@scalac.io
 * 30/10/2015
 */
@EBean
public class SpeakerDao {

	@RootContext Context context;
	@Bean RealmProvider realmProvider;

	public SpeakerDbModel saveSpeaker(SpeakerDbModel speakerDbModel) {
		final Realm realm = realmProvider.getRealm();
		realm.beginTransaction();
		realm.copyToRealm(speakerDbModel);
		realm.commitTransaction();
		return speakerDbModel;
	}

	public SpeakerDbModel getSpeakerByUuid(String uuid) {
		final Realm realm = realmProvider.getRealm();
		return realm.where(SpeakerDbModel.class)
				.equalTo("uuid", uuid).findFirst();
	}
}
