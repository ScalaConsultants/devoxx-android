package io.scalac.degree.data.manager;

import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.EBean;

import java.io.IOException;
import java.util.List;

import io.realm.Realm;
import io.scalac.degree.data.RealmProvider;
import io.scalac.degree.data.downloader.SpeakersDownloader;
import io.scalac.degree.data.model.RealmSpeaker;
import io.scalac.degree.data.model.RealmSpeakerShort;
import io.scalac.degree.utils.Logger;
import rx.Observable;
import rx.Subscriber;
import rx.schedulers.Schedulers;

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
