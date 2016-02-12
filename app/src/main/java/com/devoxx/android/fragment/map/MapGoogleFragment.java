package com.devoxx.android.fragment.map;

import android.content.Intent;
import android.os.Build;
import android.provider.Settings;
import android.support.design.widget.Snackbar;
import android.text.TextUtils;
import android.view.View;

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
import org.androidannotations.annotations.res.ColorRes;

@EFragment(R.layout.fragment_map_google)
public class MapGoogleFragment extends BaseFragment {

    @Bean
    InfoUtil infoUtil;

    @Bean
    ConferenceManager conferenceManager;

    @ColorRes(R.color.primary)
    int color;

    private Snackbar settingsSnackbar;

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
            if (!isLocationEnabled()) {
                map.setOnMyLocationButtonClickListener(() -> {
                    showinfoAboutDisabledLocation(mapFragment.getView());
                    return false;
                });
            }
        });
    }

    private void showinfoAboutDisabledLocation(final View view) {
        settingsSnackbar = Snackbar.make(view,
                R.string.disabled_location_info, Snackbar.LENGTH_LONG)
                .setAction(R.string.open_settings, v -> {
                    if (isAdded()) {
                        startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                    }
                })
                .setActionTextColor(color);
        settingsSnackbar.show();
    }

    @Override
    public void onPause() {
        if (settingsSnackbar != null) {
            settingsSnackbar.dismiss();
        }
        super.onPause();
    }

    private void setupConcrete(RealmConference conference, LatLng conferenceLocation, GoogleMap googleMap) {
        try {
            googleMap.setMyLocationEnabled(true);
            googleMap.addMarker(new MarkerOptions()
                    .position(conferenceLocation)
                    .title(conference.getVenue())
                    .snippet(conference.getAddress()));
            googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(conferenceLocation, 12.0f));
        } catch (SecurityException s) {
            infoUtil.showToast(R.string.map_permissions_failure);
        }
    }

    public boolean isLocationEnabled() {
        int locationMode = 0;
        String locationProviders;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            try {
                locationMode = Settings.Secure.getInt(getActivity()
                        .getContentResolver(), Settings.Secure.LOCATION_MODE);
            } catch (final Settings.SettingNotFoundException ignored) {
                // Nothing.
            }
            return locationMode != Settings.Secure.LOCATION_MODE_OFF;
        } else {
            locationProviders = Settings.Secure.getString(getActivity()
                    .getContentResolver(), Settings.Secure.LOCATION_PROVIDERS_ALLOWED);
            return !TextUtils.isEmpty(locationProviders);
        }
    }
}
