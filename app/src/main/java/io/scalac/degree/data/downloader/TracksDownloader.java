package io.scalac.degree.data.downloader;

import org.androidannotations.annotations.Background;
import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.EBean;

import java.io.IOException;

import io.realm.Realm;
import io.scalac.degree.Configuration;
import io.scalac.degree.connection.model.TrackApiModel;
import io.scalac.degree.connection.model.TracksApiModel;
import io.scalac.degree.data.RealmProvider;
import io.scalac.degree.data.model.RealmTrack;
import io.scalac.degree.utils.Logger;
import retrofit.Call;
import retrofit.Response;

@EBean
public class TracksDownloader extends AbstractDownloader<TracksApiModel> {

    private static final String UNKNOWN_TRACK_ICON_URL = "";

    @Bean
    RealmProvider realmProvider;

    @Background
    public void downloadTracksDescriptions(String confCode) {
        final Call<TracksApiModel> tracksCall = connection.getDevoxxApi().tracks(confCode);
        final Realm realm = realmProvider.getRealm();
        try {
            final Response<TracksApiModel> response = tracksCall.execute();
            final TracksApiModel tracksApiModel = response.body();
            realm.beginTransaction();
            for (TrackApiModel apiModel : tracksApiModel.tracks) {
                realm.copyToRealmOrUpdate(RealmTrack.createFromApi(apiModel));
            }
            realm.commitTransaction();
        } catch (IOException e) {
            Logger.exc(e);
            realm.cancelTransaction();
        }

        realm.close();
    }

    public String getTrackIconUrl(String trackId) {
        final Realm realm = realmProvider.getRealm();
        final RealmTrack realmTrack = realm.where(RealmTrack.class).
                equalTo(RealmTrack.Contract.TITLE, trackId.toLowerCase(), false).findFirst();

        String result = UNKNOWN_TRACK_ICON_URL;
        if (realmTrack != null) {
            result = Configuration.API_URL + realmTrack.getImgsrc();
        }

        realm.close();

        return result;
    }
}
