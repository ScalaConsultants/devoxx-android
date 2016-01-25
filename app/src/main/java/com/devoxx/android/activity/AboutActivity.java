package com.devoxx.android.activity;


import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.Toolbar;

import com.devoxx.R;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.OptionsItem;
import org.androidannotations.annotations.ViewById;

@EActivity(R.layout.activity_about)
public class AboutActivity extends BaseActivity {

    @ViewById(R.id.main_toolbar)
    Toolbar toolbar;

    @ViewById(R.id.main_appbar)
    AppBarLayout appBarLayout;

    @ViewById(R.id.main_collapsing)
    CollapsingToolbarLayout collapsingToolbarLayout;

    @ViewById(R.id.aboutFirstButton)
    FloatingActionButton firstButton;

    @ViewById(R.id.aboutSecondButton)
    FloatingActionButton secondButton;

    @AfterViews
    void afterViews() {
        collapsingToolbarLayout.setTitle(getString(R.string.about));
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @OptionsItem(android.R.id.home)
    void onBackClick() {
        finish();
    }

    @Click(R.id.aboutFirstButton)
    void onFirstButtonClick() {

    }

    @Click(R.id.aboutSecondButton)
    void onSecondButtonClick() {

    }
}
