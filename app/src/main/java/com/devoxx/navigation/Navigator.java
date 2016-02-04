package com.devoxx.navigation;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;

import com.devoxx.R;
import com.devoxx.android.activity.MainActivity;
import com.devoxx.android.activity.SpeakerDetailsHostActivity_;
import com.devoxx.android.activity.TalkDetailsHostActivity;
import com.devoxx.android.activity.TalkDetailsHostActivity_;
import com.devoxx.android.fragment.map.MapMainFragment_;
import com.devoxx.android.fragment.map.MapMenuLandscapeFragment_;
import com.devoxx.android.fragment.speaker.SpeakerFragment_;
import com.devoxx.android.fragment.talk.TalkFragment_;
import com.devoxx.connection.model.SlotApiModel;
import com.devoxx.utils.DeviceUtil;

import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.EBean;

@EBean
public class Navigator {

    @Bean
    DeviceUtil deviceUtil;

    public void openTalkDetails(
            MainActivity mainActivity, SlotApiModel slotApiModel, Fragment fragment, boolean notifyAboutChange) {
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

    public void openTalkDetails(Activity mainActivity, SlotApiModel slotApiModel, boolean notifyAboutChange) {
        if (deviceUtil.isLandscapeTablet()) {
            ((MainActivity) mainActivity).replaceFragmentInGivenContainer(
                    TalkFragment_.builder().slotApiModel(slotApiModel)
                            .notifyAboutChange(notifyAboutChange).build(),
                    false, R.id.content_frame_second);
        } else {
            TalkDetailsHostActivity_.intent(mainActivity).
                    slotApiModel(slotApiModel)
                    .startForResult(TalkDetailsHostActivity.REQUEST_CODE);
        }
    }

    public void openSpeakerDetails(Activity mainActivity, String speakerUuid) {
        if (deviceUtil.isLandscapeTablet()) {
            ((MainActivity) mainActivity).replaceFragmentInGivenContainer(
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

    public void openWwwLink(Activity activity, String www) {
        final String finalUrl =
                (!www.startsWith("http://") && !www.startsWith("https://")) ? "http://" + www : www;
        activity.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(finalUrl)));
    }

    public void openTwitterUser(FragmentActivity activity, String twitterName) {
        String formattedTwitterAddress = "http://twitter.com/" + twitterName.replace("@", "");
        Intent browseTwitter = new Intent(Intent.ACTION_VIEW, Uri.parse(formattedTwitterAddress));
        activity.startActivity(browseTwitter);
    }

    public void tweetMessage(FragmentActivity activity, String twitterMessage) {
        final String tweetUrl = "https://twitter.com/intent/tweet?text=" + Uri.encode(twitterMessage);
        final Uri uri = Uri.parse(tweetUrl);
        activity.startActivity(new Intent(Intent.ACTION_VIEW, uri));
    }

    public void openRegister(Activity activity, String regURL) {
        final Uri uri = Uri.parse(regURL);
        activity.startActivity(new Intent(Intent.ACTION_VIEW, uri));
    }
}
