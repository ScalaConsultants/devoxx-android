package io.scalac.degree.data.downloader;

import com.squareup.okhttp.ResponseBody;

import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.EBean;
import org.androidannotations.annotations.sharedpreferences.Pref;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import io.realm.Realm;
import io.scalac.degree.connection.DevoxxApi;
import io.scalac.degree.connection.model.SpeakerShortApiModel;
import io.scalac.degree.data.DataInformation_;
import io.scalac.degree.data.RealmProvider;
import io.scalac.degree.data.cache.SpeakerCache;
import io.scalac.degree.data.model.RealmSpeaker;
import io.scalac.degree.utils.Logger;
import retrofit.Call;

/**
 * www.scalac.io
 * jacek.modrakowski@scalac.io
 * 29/10/2015
 */
@EBean
public class SpeakersDownloader extends AbstractDownloader<SpeakerShortApiModel> {

    @Bean
    RealmProvider realmProvider;

    @Bean
    SpeakerCache speakerCache;

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
        }
    }

    public void downloadSpeakersSync(final String confCode) throws IOException {
        if (shouldCall()) {
            dataInformation.lastSpeakersCall().put(System.currentTimeMillis());

            final DevoxxApi devoxxApi = connection.getDevoxxApi();
            final Call<List<SpeakerShortApiModel>> call = devoxxApi.speakers(confCode);
            final List<SpeakerShortApiModel> result = call.execute().body();

            final ExecutorService es = Executors.newFixedThreadPool(4);
            final List<Callable<Object>> callables = new ArrayList<>(result.size());
            for (final SpeakerShortApiModel speakerShortApiModel : result) {
                callables.add(new Callable<Object>() {
                    @Override
                    public Object call() throws Exception {
                        try {
                            final String uuid = speakerShortApiModel.uuid;
                            final boolean isCacheObjectInValid = !speakerCache.isValid(uuid);

                            if (isCacheObjectInValid) {
                                final Realm realm = realmProvider.getRealm();
                                final Call<ResponseBody> callSpeaker = devoxxApi.
                                        speaker(confCode, speakerShortApiModel.uuid);
                                final String rawModel = callSpeaker.execute().body().string();
                                speakerCache.upsert(rawModel, uuid);

                                realm.beginTransaction();
                                realm.createOrUpdateObjectFromJson(
                                        RealmSpeaker.class, rawModel);
                                realm.commitTransaction();
                            }
                        } catch (Exception e) {
                            Logger.exc(e);
                        }

                        // We don't need to return some value.
                        return null;
                    }
                });
            }

            try {
                es.invokeAll(callables);
            } catch (InterruptedException e) {
                Logger.exc(e);
            }
        }
    }

    private boolean shouldCall() {
        return (System.currentTimeMillis() - dataInformation.lastSpeakersCall().getOr(0L)
                > SpeakerCache.CACHE_LIFE_TIME_MS);
    }
}
