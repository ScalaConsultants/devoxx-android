package io.scalac.degree.android.activity;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.Extra;
import org.androidannotations.annotations.FragmentById;
import org.androidannotations.annotations.OptionsItem;

import io.scalac.degree.android.fragment.talk.TalkFragment;
import io.scalac.degree.connection.model.SlotApiModel;
import io.scalac.degree33.R;

@EActivity(R.layout.activity_talk_details_host)
public class TalkDetailsHostActivity extends BaseActivity {

    @Extra
    SlotApiModel slotApiModel;

    @FragmentById(R.id.talkDetailsFragment)
    TalkFragment talkFragment;

    @AfterViews
    void afterViews() {
        talkFragment.setupFragment(slotApiModel);
    }

    @OptionsItem(android.R.id.home)
    void onBackClick() {
        finish();
    }
}
