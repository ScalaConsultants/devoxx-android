package com.devoxx.android.activity;

import android.support.v7.widget.Toolbar;

import com.devoxx.R;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.OptionsItem;
import org.androidannotations.annotations.ViewById;

@EActivity(R.layout.activity_settings)
public class SettingsActivity extends BaseActivity {

    @ViewById(R.id.toolbar)
    Toolbar toolbar;

    @AfterViews
    void afterViews() {
        super.afterViews();

        toolbar.setTitle(R.string.settings);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @OptionsItem(android.R.id.home)
    void onBackClick() {
        finish();
    }
}
