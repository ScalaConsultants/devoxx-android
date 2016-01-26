package io.scalac.degree.android.activity;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.Extra;
import org.androidannotations.annotations.FragmentById;

import io.scalac.degree.android.fragment.talk.NEW_TalkFragment;
import io.scalac.degree.connection.model.SlotApiModel;
import io.scalac.degree33.R;

@EActivity(R.layout.activity_talk_details_host)
public class TalkDetailsHostActivity extends BaseActivity {

    @Extra
    SlotApiModel slotApiModel;

    @FragmentById(R.id.talkDetailsFragment)
    NEW_TalkFragment new_talkFragment;

    @AfterViews void afterViews() {
        new_talkFragment.setupFragment(slotApiModel);
    }
}
