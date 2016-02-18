package com.devoxx.android.activity;

import android.content.Intent;

import com.annimon.stream.Optional;
import com.devoxx.R;
import com.devoxx.android.fragment.speaker.SpeakerFragment;
import com.devoxx.connection.model.SlotApiModel;
import com.devoxx.data.manager.NotificationsManager;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.Extra;
import org.androidannotations.annotations.FragmentById;
import org.androidannotations.annotations.OptionsItem;

@EActivity(R.layout.activity_spekaer_details_host)
public class SpeakerDetailsHostActivity extends BaseActivity {

    @Extra
    String speakerUuid;

    @FragmentById(R.id.speakerDetailsFragment)
    SpeakerFragment speakerFragment;

    @AfterViews
    void afterViewsInner() {
        speakerFragment.setupFragment(speakerUuid);
    }

    @OptionsItem(android.R.id.home)
    void onBackClick() {
        finish();
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);

        if (intent.hasExtra(SpeakerDetailsHostActivity_.SPEAKER_UUID_EXTRA)) {
            final String newSpeakerUuid = intent.getStringExtra(
                    SpeakerDetailsHostActivity_.SPEAKER_UUID_EXTRA);
            speakerFragment.setupFragment(newSpeakerUuid);
        }
    }
}
