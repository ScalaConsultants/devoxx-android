package com.devoxx.data.manager;

import com.devoxx.connection.model.SpeakerShortApiModel;
import com.devoxx.data.RealmProvider;
import com.devoxx.data.downloader.SpeakersDownloader;
import com.devoxx.data.model.RealmSpeaker;
import com.devoxx.data.model.RealmSpeakerShort;

import org.androidannotations.annotations.Background;
import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.EBean;

import java.io.IOException;
import java.util.List;

import io.realm.Realm;

@EBean
public class SpeakersDataManager extends AbstractDataManager<RealmSpeaker> {

    @Bean
    SpeakersDownloader speakersDownloader;

    @Bean
    RealmProvider realmProvider;

    public List<SpeakerShortApiModel> fetchSpeakersSync(final String confCode) throws IOException {
        return speakersDownloader.downloadSpeakersShortInfoList(confCode);
    }

    @Background
    public void fetchSpeakerAsync(
            final String confCode, final String uuid,
            final IDataManagerListener<RealmSpeaker> listener) {
        try {
            notifyAboutStart(listener);
            final RealmSpeaker speaker = speakersDownloader
                    .downloadSpeakerSync(confCode, uuid);
            notifyAboutSuccess(listener, speaker);
        } catch (IOException e) {
            notifyAboutFailed(listener);
        }
    }

    public RealmSpeaker getByUuid(String uuid) {
        final Realm realm = realmProvider.getRealm();
        final RealmSpeaker result = realm.where(RealmSpeaker.class).
                equalTo(RealmSpeaker.Contract.UUID, uuid).findFirst();
        realm.close();

        return result;
    }

    public List<RealmSpeakerShort> getAllShortSpeakers() {
        final Realm realm = realmProvider.getRealm();
        final List<RealmSpeakerShort> result = realm.allObjects(RealmSpeakerShort.class);
        realm.close();

        return result;
    }

    public List<RealmSpeakerShort> getAllShortSpeakersWithFilter(String query) {
        final Realm realm = realmProvider.getRealm();
        final List<RealmSpeakerShort> result = realm
                .where(RealmSpeakerShort.class)
                .contains("firstName", query, false)
                .or()
                .contains("lastName", query, false)
                .findAll();
        realm.close();

        return result;
    }
}
