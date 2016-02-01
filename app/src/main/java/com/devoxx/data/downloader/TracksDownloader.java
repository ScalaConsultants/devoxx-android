package com.devoxx.data.downloader;

import com.devoxx.connection.Connection;
import com.devoxx.connection.model.TracksApiModel;
import com.devoxx.data.schedule.filter.ScheduleFilterManager;

import org.androidannotations.annotations.AfterInject;
import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.EBean;

import java.io.IOException;
import java.util.List;

import io.realm.Realm;

import com.devoxx.connection.model.TrackApiModel;
import com.devoxx.data.RealmProvider;
import com.devoxx.data.model.RealmTrack;

import retrofit.Call;
import retrofit.Response;

@EBean
public class TracksDownloader {

    private static final String UNKNOWN_TRACK_ICON_URL = "";

    @Bean
    RealmProvider realmProvider;

    @Bean
    ScheduleFilterManager scheduleFilterManager;

    @Bean
    Connection connection;

    private String activeConferenceApiUrl;

    @AfterInject
    void afterInject() {
        activeConferenceApiUrl = connection.getActiveConferenceApiUrl();
    }

    public void downloadTracksDescriptions(String confCode) throws IOException {
        final Call<TracksApiModel> tracksCall = connection.getDevoxxApi().tracks(confCode);
        final Realm realm = realmProvider.getRealm();
        final Response<TracksApiModel> response = tracksCall.execute();
        final TracksApiModel tracksApiModel = response.body();
        realm.beginTransaction();
        for (TrackApiModel apiModel : tracksApiModel.tracks) {
            realm.copyToRealmOrUpdate(RealmTrack.createFromApi(apiModel));
        }
        realm.commitTransaction();

        final List<RealmTrack> tracks = realm.allObjects(RealmTrack.class);
        scheduleFilterManager.createTrackFiltersDefinition(tracks);

        realm.close();
    }

    public String getTrackIconUrl(String trackId) {
        final Realm realm = realmProvider.getRealm();
        final RealmTrack realmTrack = realm.where(RealmTrack.class).
                equalTo(RealmTrack.Contract.TITLE, trackId.toLowerCase(), false).findFirst();

        String result = UNKNOWN_TRACK_ICON_URL;
        if (realmTrack != null) {
            result = activeConferenceApiUrl + realmTrack.getImgsrc();
        }

        realm.close();

        return result;
    }

    public void clearTracksData() {
        final Realm realm = realmProvider.getRealm();
        realm.beginTransaction();
        realm.allObjects(RealmTrack.class).clear();
        realm.commitTransaction();
        realm.close();
    }
}
