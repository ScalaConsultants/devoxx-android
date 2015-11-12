package io.scalac.degree.android.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.FragmentArg;
import org.androidannotations.annotations.ViewById;

import java.text.DateFormat;
import java.util.List;

import io.scalac.degree.connection.model.SlotApiModel;
import io.scalac.degree.connection.model.TalkSpeakerApiModel;
import io.scalac.degree.data.manager.NotificationsManager;
import io.scalac.degree33.R;

@EFragment(R.layout.fragment_talk)
public class TalkFragment extends BaseFragment {

    @FragmentArg
    SlotApiModel slotModel;

    @Bean
    NotificationsManager notificationsManager;

    @ViewById(R.id.textTopic)
    TextView topic;
    @ViewById(R.id.textDesc)
    TextView desc;
    @ViewById(R.id.textDate)
    TextView date;
    @ViewById(R.id.textTimeStart)
    TextView start;
    @ViewById(R.id.textTimeEnd)
    TextView end;
    @ViewById(R.id.textRoom)
    TextView room;
    @ViewById(R.id.buttonSpeaker)
    Button speaker;
    @ViewById(R.id.buttonSpeaker2)
    Button speaker2;
    @ViewById(R.id.switchNotify)
    Switch notifySwitch;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        logFlurryEvent("Talk_info_watched");
    }

    @Nullable
    @Override
    public String getTitleAsString() {
        return slotModel.talk.title;
    }

    @Override
    public boolean needsToolbarSpinner() {
        return false;
    }

    @AfterViews
    void afterViews() {
        setHasOptionsMenu(true);
        setupViews(slotModel);
    }

    public void setupViews(final SlotApiModel slotModel) {
        topic.setText(Html.fromHtml(slotModel.talk.title));

        desc.setText(Html.fromHtml(slotModel.talk.summaryAsHtml));
        desc.setMovementMethod(LinkMovementMethod.getInstance());

        final Context appContext = getActivity().getApplicationContext();
        final DateFormat dateFormat = android.text.format.DateFormat
                .getLongDateFormat(appContext);
        final DateFormat timeFormat = android.text.format.DateFormat
                .getTimeFormat(appContext);

        date.setText(dateFormat.format(slotModel.fromTimeMillis));
        start.setText(timeFormat.format(slotModel.fromTimeMillis));
        end.setText(timeFormat.format(slotModel.toTimeMillis));

        room.setText(slotModel.roomName);

        final List<TalkSpeakerApiModel> speakers = slotModel.talk.speakers;
        final TalkSpeakerApiModel firstSpeaker = speakers.get(0);
        speaker.setText(firstSpeaker.name);

        final TalkSpeakerApiModel secondSpeaker = speakers.size() > 1 ? speakers.get(1) : null;

        if (secondSpeaker != null) {
            speaker2.setText(secondSpeaker.name);
        } else {
            speaker2.setVisibility(View.GONE);
        }

        notifySwitch.setChecked(notificationsManager.isNotificationScheduled(slotModel.slotId));
        notifySwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                final NotificationsManager.ScheduleNotificationModel model =
                        NotificationsManager.ScheduleNotificationModel.create(slotModel, true);
                if (isChecked) {
                    final boolean checkResult = notificationsManager.scheduleNotification(model);
                    buttonView.setChecked(checkResult);
                } else {
                    notificationsManager.unscheduleNotification(slotModel.slotId);
                }
            }
        });

        getMainActivity().getSupportActionBarHelper().setDisplayHomeAsUpEnabled(true);
    }

    @Click({R.id.buttonSpeaker, R.id.buttonSpeaker2})
    void onSpeakersClick(View view) {
        switch (view.getId()) {
            case R.id.buttonSpeaker:
                getMainActivity().replaceFragment(SpeakerFragment_.builder()
                        .speaker(slotModel.talk.speakers.get(0)).build(), true);
                break;
            case R.id.buttonSpeaker2:
                getMainActivity().replaceFragment(SpeakerFragment_.builder()
                        .speaker(slotModel.talk.speakers.get(1)).build(), true);
                break;
        }
    }
}
