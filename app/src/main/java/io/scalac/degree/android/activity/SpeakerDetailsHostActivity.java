package io.scalac.degree.android.activity;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.Extra;
import org.androidannotations.annotations.FragmentById;
import org.androidannotations.annotations.OptionsItem;

import io.scalac.degree.android.fragment.speaker.SpeakerFragment;
import io.scalac.degree.connection.model.TalkSpeakerApiModel;
import io.scalac.degree33.R;

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
