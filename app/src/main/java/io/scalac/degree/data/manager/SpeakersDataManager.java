package io.scalac.degree.data.manager;

import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.EBean;

import java.io.IOException;

import io.scalac.degree.data.RealmProvider;
import io.scalac.degree.data.downloader.SpeakersDownloader;
import io.scalac.degree.data.model.RealmSpeaker;
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

    public Observable<Void> fetchSpeakers(final String confCode) {
        return Observable.create(new Observable.OnSubscribe<Void>() {
            @Override
            public void call(Subscriber<? super Void> observer) {
                if (!observer.isUnsubscribed()) {
                    try {
                        observer.onStart();
                        speakersDownloader.downloadSpeakersSync(confCode);
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
        return realmProvider.getRealm().where(RealmSpeaker.class).
                equalTo(RealmSpeaker.Contract.UUID, uuid).findFirst();
    }
}
