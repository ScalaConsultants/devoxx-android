package com.devoxx.android.activity;

import com.devoxx.android.fragment.speaker.SpeakerFragment;
import com.devoxx.connection.model.TalkSpeakerApiModel;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.Extra;
import org.androidannotations.annotations.FragmentById;
import org.androidannotations.annotations.OptionsItem;

import com.devoxx.R;

@EActivity(R.layout.activity_spekaer_details_host)
public class SpeakerDetailsHostActivity extends BaseActivity {

    @Extra
    String speakerUuid;

    @Extra
    TalkSpeakerApiModel talkSpeakerModel;

    @FragmentById(R.id.speakerDetailsFragment)
    SpeakerFragment speakerFragment;

    @AfterViews
    void afterViews() {
        speakerFragment.setupFragment(speakerUuid, talkSpeakerModel);
    }

    @OptionsItem(android.R.id.home)
    void onBackClick() {
        finish();
    }
}
