package com.devoxx.android.activity;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.Extra;
import org.androidannotations.annotations.FragmentById;
import org.androidannotations.annotations.OptionsItem;

import com.devoxx.android.fragment.talk.TalkFragment;
import com.devoxx.connection.model.SlotApiModel;
import com.devoxx.R;

@EActivity(R.layout.activity_talk_details_host)
public class TalkDetailsHostActivity extends BaseActivity {

    public static final int REQUEST_CODE = 1;
    public static final int RESULT_CODE_SUCCESS = 2;

    @Extra
    SlotApiModel slotApiModel;

    @Extra
    boolean notifyAboutChange;

    @FragmentById(R.id.talkDetailsFragment)
    TalkFragment talkFragment;

    @AfterViews
    void afterViews() {
        talkFragment.setupFragment(slotApiModel, notifyAboutChange);
    }

    @OptionsItem(android.R.id.home)
    void onBackClick() {
        finish();
    }
}
