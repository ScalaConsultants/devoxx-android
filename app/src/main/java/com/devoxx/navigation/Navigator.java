package com.devoxx.navigation;

import android.support.v4.app.Fragment;

import com.devoxx.R;
import com.devoxx.android.activity.MainActivity;
import com.devoxx.android.activity.SpeakerDetailsHostActivity_;
import com.devoxx.android.activity.TalkDetailsHostActivity;
import com.devoxx.android.activity.TalkDetailsHostActivity_;
import com.devoxx.android.fragment.map.MapMainFragment_;
import com.devoxx.android.fragment.map.MapMenuLandscapeFragment;
import com.devoxx.android.fragment.map.MapMenuLandscapeFragment_;
import com.devoxx.android.fragment.speaker.SpeakerFragment_;
import com.devoxx.android.fragment.speaker.SpeakersFragment_;
import com.devoxx.android.fragment.talk.TalkFragment_;
import com.devoxx.connection.model.SlotApiModel;
import com.devoxx.utils.DeviceUtil;

import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.EBean;

@EBean
public class Navigator {

    @Bean
    DeviceUtil deviceUtil;

    public void openSchedule(MainActivity mainActivity) {
        if (deviceUtil.isLandscapeTablet()) {

        } else {

        }
    }

    public void openTracks(MainActivity mainActivity) {
        if (deviceUtil.isLandscapeTablet()) {

        } else {

        }
    }

    public void openSpeakers(MainActivity mainActivity) {
        if (deviceUtil.isLandscapeTablet()) {

        } else {

        }
    }

    public void openTalkDetails(MainActivity mainActivity) {

    }

    public void openTalkDetails(MainActivity mainActivity, SlotApiModel slotApiModel, Fragment fragment, boolean notifyAboutChange) {
        if (deviceUtil.isLandscapeTablet()) {
            mainActivity.replaceFragmentInGivenContainer(
                    TalkFragment_.builder().slotApiModel(slotApiModel)
                            .notifyAboutChange(notifyAboutChange).build(),
                    false, R.id.content_frame_second);
        } else {
            TalkDetailsHostActivity_.intent(fragment).
                    slotApiModel(slotApiModel)
                    .startForResult(TalkDetailsHostActivity.REQUEST_CODE);
        }
    }

    public void openTalkDetails(MainActivity mainActivity, SlotApiModel slotApiModel, boolean notifyAboutChange) {
        if (deviceUtil.isLandscapeTablet()) {
            mainActivity.replaceFragmentInGivenContainer(
                    TalkFragment_.builder().slotApiModel(slotApiModel)
                            .notifyAboutChange(notifyAboutChange).build(),
                    false, R.id.content_frame_second);
        } else {
            TalkDetailsHostActivity_.intent(mainActivity).
                    slotApiModel(slotApiModel)
                    .startForResult(TalkDetailsHostActivity.REQUEST_CODE);
        }
    }

    public void openSpeakerDetails(MainActivity mainActivity, String speakerUuid) {
        if (deviceUtil.isLandscapeTablet()) {
            mainActivity.replaceFragmentInGivenContainer(
                    SpeakerFragment_.builder().speakerUuid(speakerUuid).build(),
                    false, R.id.content_frame_second);
        } else {
            SpeakerDetailsHostActivity_.intent(mainActivity).speakerUuid(speakerUuid).start();
        }
    }

    public void openMaps(MainActivity mainActivity) {
        if (deviceUtil.isLandscapeTablet()) {
            mainActivity.replaceFragmentInGivenContainer(
                    MapMenuLandscapeFragment_.builder().build(), false, R.id.content_frame);
        } else {
            mainActivity.replaceFragmentInGivenContainer(
                    MapMainFragment_.builder().build(), false, R.id.content_frame);
        }
    }
}
