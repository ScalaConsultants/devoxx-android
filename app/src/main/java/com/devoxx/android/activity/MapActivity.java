package com.devoxx.android.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.ViewFlipper;

import com.google.android.gms.maps.MapView;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.ViewById;

import com.devoxx.R;

@EActivity
public class MapActivity extends BaseActivity {

    @ViewById(R.id.toolbar)
    Toolbar toolbar;

    @ViewById(R.id.mapGetThere)
    View mapButton;

    @ViewById(R.id.mapIndoor)
    View indoorButton;

    @ViewById(R.id.mapFlipper)
    ViewFlipper viewFlipper;

    private MapView mapView;

    @AfterViews
    protected void afterViews() {
        setupToolbar();
        setupInitialState();
    }

    @Click({R.id.mapGetThere, R.id.mapIndoor})
    protected void onButtonsClick(final View view) {
        switch (view.getId()) {
            case R.id.mapGetThere:
                enableMapButton(true);
                break;
            case R.id.mapIndoor:
                enableMapButton(false);
                break;
        }
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);
        mapView = (MapView) findViewById(R.id.mapView);

        mapView.onCreate(savedInstanceState);
    }

    @Override
    protected void onResume() {
        super.onResume();
        mapView.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mapView.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mapView.onSaveInstanceState(outState);
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mapView.onLowMemory();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem) {
        if (menuItem.getItemId() == android.R.id.home) {
            finish();
        }
        return super.onOptionsItemSelected(menuItem);
    }

    private void setupToolbar() {
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Maps");
    }

    private void setupInitialState() {
        mapButton.setSelected(true);
        indoorButton.setSelected(false);
    }

    private void enableMapButton(boolean enableMap) {
        mapButton.setSelected(enableMap);
        indoorButton.setSelected(!enableMap);

        if (enableMap) {
            viewFlipper.showPrevious();
        } else {
            viewFlipper.showNext();
        }
    }
}
