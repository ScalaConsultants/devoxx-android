package com.devoxx.data.manager;

import com.devoxx.data.RealmProvider;
import com.devoxx.data.model.RealmSpeaker;
import com.devoxx.data.model.RealmSpeakerShort;
import com.devoxx.utils.Logger;

import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.EBean;

import java.io.IOException;
import java.util.List;

import io.realm.Realm;

import com.devoxx.data.downloader.SpeakersDownloader;

import rx.Observable;
import rx.Subscriber;

@EBean
public class SpeakersDataManager extends AbstractDataManager<RealmSpeaker> {

    @Bean
    SpeakersDownloader speakersDownloader;

    @Bean
    RealmProvider realmProvider;

    public Observable<Void> fetchSpeakersShortInfo(final String confCode) {
        return Observable.create(new Observable.OnSubscribe<Void>() {
            @Override
            public void call(Subscriber<? super Void> observer) {
                if (!observer.isUnsubscribed()) {
                    try {
                        observer.onStart();
                        speakersDownloader.downloadSpeakersShortInfoList(confCode);
                        observer.onCompleted();
                    } catch (IOException e) {
                        Logger.exc(e);
                        observer.onError(e);
                    }
                }
            }
        });
    }

    public Observable<RealmSpeaker> fetchSpeaker(final String confCode, final String uuid) {
        return Observable.create(new Observable.OnSubscribe<RealmSpeaker>() {
            @Override
            public void call(Subscriber<? super RealmSpeaker> observer) {
                if (!observer.isUnsubscribed()) {
                    try {
                        observer.onStart();
                        speakersDownloader.downloadSpeakerSync(confCode, uuid);
                        observer.onCompleted();
                    } catch (IOException e) {
                        Logger.exc(e);
                        observer.onError(e);
                    }
                }
            }
        });
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
