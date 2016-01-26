package com.devoxx.data.downloader;

import com.devoxx.connection.DevoxxApi;
import com.devoxx.connection.model.SpeakerShortApiModel;
import com.devoxx.data.RealmProvider;
import com.devoxx.data.cache.SpeakersCache;
import com.devoxx.data.model.RealmSpeaker;
import com.devoxx.data.model.RealmSpeakerShort;
import com.devoxx.utils.Logger;
import com.squareup.okhttp.ResponseBody;

import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.EBean;
import org.androidannotations.annotations.sharedpreferences.Pref;

import java.io.IOException;
import java.util.List;

import io.realm.Realm;
import com.devoxx.data.DataInformation_;

import com.devoxx.data.cache.SpeakerCache;

import retrofit.Call;

@EBean
public class SpeakersDownloader extends AbstractDownloader<SpeakerShortApiModel> {

    @Bean
    RealmProvider realmProvider;

    @Bean
    SpeakerCache speakerCache;

    @Bean
    SpeakersCache speakersCache;

    @Pref
    DataInformation_ dataInformation;

    public void downloadSpeakerSync(final String confCode, final String uuid) throws IOException {
        if (!speakerCache.isValid(uuid)) {
            final DevoxxApi devoxxApi = connection.getDevoxxApi();
            final Realm realm = realmProvider.getRealm();

            final Call<ResponseBody> callSpeaker = devoxxApi.speaker(confCode, uuid);
            final String rawModel = callSpeaker.execute().body().string();
            speakerCache.upsert(rawModel, uuid);

            realm.beginTransaction();
            realm.createOrUpdateObjectFromJson(
                    RealmSpeaker.class, rawModel);
            realm.commitTransaction();
            realm.close();
        }
    }

    public void downloadSpeakersShortInfoList(
            final String confCode) throws IOException {

        final List<SpeakerShortApiModel> speakers;
        if (speakersCache.isValid()) {
            speakers = speakersCache.getData();
        } else {
            try {
                final DevoxxApi devoxxApi = connection.getDevoxxApi();
                final Call<List<SpeakerShortApiModel>> call = devoxxApi.speakers(confCode);
                speakers = call.execute().body();
                speakersCache.upsert(speakers);
            } catch (IOException e) {
                Logger.exc(e);
                throw e;
            }
        }

        final Realm realm = realmProvider.getRealm();
        realm.beginTransaction();
        for (SpeakerShortApiModel apiModel : speakers) {
            final RealmSpeakerShort speakerShort = RealmSpeakerShort.fromApi(apiModel);
            realm.copyToRealmOrUpdate(speakerShort);
        }
        realm.commitTransaction();
        realm.close();
    }
}