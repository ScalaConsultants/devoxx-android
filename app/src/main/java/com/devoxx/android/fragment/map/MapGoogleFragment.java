package com.devoxx.android.fragment.map;

import com.devoxx.R;
import com.devoxx.android.fragment.common.BaseFragment;
import com.devoxx.data.conference.ConferenceManager;
import com.devoxx.data.model.RealmConference;
import com.devoxx.utils.InfoUtil;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.EFragment;

@EFragment(R.layout.fragment_map_google)
public class MapGoogleFragment extends BaseFragment {

    @Bean
    InfoUtil infoUtil;

    @Bean
    ConferenceManager conferenceManager;

    @AfterViews
    void afterViews() {
        initMap(conferenceManager.getActiveConference());
    }

    private void initMap(RealmConference conference) {
        final LatLng conferenceLocation = RealmConference.getLocation(conference);
        final SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager()
                .findFragmentById(R.id.mapGoogle);

        mapFragment.getMapAsync(map -> {
            setupConcrete(conference, conferenceLocation, map);
        });
    }

    private void setupConcrete(RealmConference conference, LatLng conferenceLocation, GoogleMap googleMap) {
        try {
            googleMap.setMyLocationEnabled(true);
            googleMap.addMarker(new MarkerOptions()
                    .position(conferenceLocation)
                    .title(conference.getVenue())
                    .snippet(conference.getAddress()));
            googleMap.animateCamera(CameraUpdateFactory
                    .newLatLngZoom(conferenceLocation, 12.0f));
        } catch (SecurityException s) {
            infoUtil.showToast(R.string.map_permissions_failure);
        }
    }
}
