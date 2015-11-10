package io.scalac.degree.data.manager;

import org.androidannotations.annotations.Background;
import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.EBean;
import org.androidannotations.annotations.RootContext;

import android.content.Context;
import android.support.annotation.Nullable;

import java.io.IOException;

import io.realm.Realm;
import io.scalac.degree.connection.model.SpeakerFullApiModel;
import io.scalac.degree.data.RealmProvider;
import io.scalac.degree.data.dao.SpeakerDao;
import io.scalac.degree.data.downloader.SpeakersDownloader;
import io.scalac.degree.data.model.SpeakerDbModel;
import io.scalac.degree.utils.Logger;

/**
 * www.scalac.io
 * jacek.modrakowski@scalac.io
 * 29/10/2015
 */
@EBean
public class SpeakerDataManager extends AbstractDataManager<SpeakerFullApiModel> {

	@RootContext Context context;
	@Bean SpeakersDownloader speakersDownloader;
	@Bean SpeakerDao speakerDao;
	@Bean RealmProvider realmProvider;

	@Background public void fetchSpeaker(
			String confCode, String uuid,
			@Nullable IDataManagerListener<SpeakerFullApiModel> listener) {
		try {
			final Realm realm = realmProvider.getRealm();

			notifyAboutStart(listener);

			final SpeakerDbModel fromDao = speakerDao.getSpeakerByUuid(uuid);
			final SpeakerFullApiModel result;
			if (fromDao == null) {
				result = speakersDownloader.downloadSpeaker(confCode, uuid);
				final SpeakerDbModel dbModel = SpeakerDbModel.fromApiModel(realm, result);
				speakerDao.saveSpeaker(dbModel);
			} else {
				result = SpeakerFullApiModel.fromDb(fromDao);
				speakersDownloader.updateSpeakerAsync(confCode, uuid);
			}

			notifyAboutSuccess(listener, result);
		} catch (IOException e) {
			Logger.exc(e);
			notifyAboutFailed(listener);
		}
	}
}
