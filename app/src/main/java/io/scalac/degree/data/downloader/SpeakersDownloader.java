package io.scalac.degree.data.downloader;

import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.squareup.okhttp.ResponseBody;

import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.EBean;
import org.androidannotations.annotations.sharedpreferences.Pref;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import io.realm.Realm;
import io.scalac.degree.connection.DevoxxApi;
import io.scalac.degree.connection.model.SlotApiModel;
import io.scalac.degree.connection.model.SpeakerShortApiModel;
import io.scalac.degree.data.DataInformation_;
import io.scalac.degree.data.RealmProvider;
import io.scalac.degree.data.cache.SpeakerCache;
import io.scalac.degree.data.cache.SpeakersCache;
import io.scalac.degree.data.model.RealmSpeaker;
import io.scalac.degree.data.model.RealmSpeakerShort;
import io.scalac.degree.utils.Logger;
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
