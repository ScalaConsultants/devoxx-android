package com.devoxx.android.activity;

import android.content.Intent;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.Extra;
import org.androidannotations.annotations.FragmentById;
import org.androidannotations.annotations.OptionsItem;

import com.annimon.stream.Optional;
import com.devoxx.android.fragment.talk.TalkFragment;
import com.devoxx.connection.model.SlotApiModel;
import com.devoxx.R;
import com.devoxx.data.manager.NotificationsManager;
import com.devoxx.data.manager.SlotsDataManager;
import com.devoxx.utils.InfoUtil;

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

    @Bean
    InfoUtil infoUtil;

    @Bean
    SlotsDataManager slotsDataManager;

    @AfterViews
    void afterViewsInner() {
        talkFragment.setupFragment(slotApiModel, notifyAboutChange);
    }

    @OptionsItem(android.R.id.home)
    void onBackClick() {
        onBackPressed();
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);

        if (intent.hasExtra(NotificationsManager.EXTRA_TALK_ID)) {
            final String incoming = intent.getStringExtra(
                    NotificationsManager.EXTRA_TALK_ID);
            final Optional<SlotApiModel> opt = slotsDataManager.getSlotByTalkId(incoming);
            if (opt.isPresent()) {
                talkFragment.setupFragment(opt.get(), true);
            } else {
                infoUtil.showToast(R.string.no_talk_foud);
                onBackClick();
            }
        }
    }
}
