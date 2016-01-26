package io.scalac.degree.android.fragment.talk;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.FragmentArg;
import org.androidannotations.annotations.ViewById;

import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v7.widget.Toolbar;
import android.view.View;

import io.scalac.degree.android.activity.BaseActivity;
import io.scalac.degree.android.fragment.common.BaseFragment;
import io.scalac.degree.android.view.TalkDetailsHeader;
import io.scalac.degree.connection.model.SlotApiModel;
import io.scalac.degree33.R;

@EFragment(R.layout.fragment_talk_new)
public class NEW_TalkFragment extends BaseFragment implements AppBarLayout.OnOffsetChangedListener {

    @ViewById(R.id.talkDetailsScheduleBtn)
    View scheduleButton;

    @ViewById(R.id.main_toolbar)
    Toolbar toolbar;

    @ViewById(R.id.main_appbar)
    AppBarLayout appBarLayout;

    @ViewById(R.id.main_collapsing)
    CollapsingToolbarLayout collapsingToolbarLayout;

    @ViewById(R.id.toolbar_header_view)
    TalkDetailsHeader toolbarHeaderView;

    @ViewById(R.id.float_header_view)
    TalkDetailsHeader floatHeaderView;

    private boolean isHideToolbarView = false;

    @AfterViews
    void afterViews() {
        collapsingToolbarLayout.setTitle(" ");
        final BaseActivity baseActivity = ((BaseActivity) getActivity());
        baseActivity.setSupportActionBar(toolbar);
        baseActivity.getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        appBarLayout.addOnOffsetChangedListener(this);
    }

    @Click(R.id.talkDetailsScheduleBtn)
    void onScheduleButtonClick() {
        // TODO Schedule unschedule talk!
    }

    @Override
    public void onOffsetChanged(AppBarLayout appBarLayout, int offset) {
        int maxScroll = appBarLayout.getTotalScrollRange();
        float percentage = (float) Math.abs(offset) / (float) maxScroll;

        if (percentage == 1f && isHideToolbarView) {
            toolbarHeaderView.setVisibility(View.VISIBLE);
            isHideToolbarView = !isHideToolbarView;

        } else if (percentage < 1f && !isHideToolbarView) {
            toolbarHeaderView.setVisibility(View.GONE);
            isHideToolbarView = !isHideToolbarView;
        }
    }

    public void setupFragment(SlotApiModel slotModel) {
        toolbarHeaderView.setupHeader(slotModel.talk.title, slotModel.talk.track);
        floatHeaderView.setupHeader(slotModel.talk.title, slotModel.talk.track);
    }
}