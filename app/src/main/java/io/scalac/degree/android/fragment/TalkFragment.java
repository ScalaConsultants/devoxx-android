package io.scalac.degree.android.fragment;

import android.content.Context;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.Html;
import android.text.InputType;
import android.text.method.LinkMovementMethod;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.FragmentArg;
import org.androidannotations.annotations.ViewById;
import org.androidannotations.annotations.res.ColorRes;

import java.text.DateFormat;
import java.util.List;

import io.scalac.degree.connection.model.SlotApiModel;
import io.scalac.degree.connection.model.TalkSpeakerApiModel;
import io.scalac.degree.connection.vote.model.VoteTalkModel;
import io.scalac.degree.data.manager.NotificationsManager;
import io.scalac.degree.data.vote.interfaces.IOnGetTalkVotesListener;
import io.scalac.degree.data.vote.interfaces.IOnVoteForTalkListener;
import io.scalac.degree.data.vote.interfaces.ITalkVoter;
import io.scalac.degree.data.vote.voters.FakeVoter;
import io.scalac.degree33.R;

@EFragment(R.layout.fragment_talk)
public class TalkFragment extends BaseFragment implements IOnGetTalkVotesListener, IOnVoteForTalkListener {

    private static final int QUESTION_MIN_CHARS_LENGTH = 3;
    private static final int QUESTION_MAX_CHARS_LENGTH = 140;

    @FragmentArg
    SlotApiModel slotModel;

    @Bean
    NotificationsManager notificationsManager;

    @Bean(FakeVoter.class)
    ITalkVoter talkVoter;

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
    View scheduleViewContainer;

    @ViewById(R.id.scheduleIcon)
    ImageView scheduleIcon;

    @ViewById(R.id.scheduleText)
    TextView scheduleText;

    @ViewById(R.id.talkFragmentVoteLabel)
    TextView voteLabel;

    @ColorRes(R.color.scheduled_star_color)
    int scheduledIconColor;

    private String talkId;

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
        talkId = slotModel.talk.id;

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

        setupNotificationView();

        scheduleViewContainer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (notificationsManager.isNotificationScheduled(slotModel.slotId)) {
                    notificationsManager.unscheduleNotification(slotModel.slotId);
                    setupNotActiveScheduleView();
                } else {
                    final NotificationsManager.ScheduleNotificationModel model =
                            NotificationsManager.ScheduleNotificationModel.create(slotModel, true);
                    notificationsManager.scheduleNotification(model);
                    setupActiveScheduleView();
                }
            }
        });

        getMainActivity().getSupportActionBarHelper().setDisplayHomeAsUpEnabled(true);

        talkVoter.getVotesCountForTalk(talkId, this);
    }

    private void setupNotificationView() {
        if (notificationsManager.isNotificationScheduled(slotModel.slotId)) {
            setupActiveScheduleView();
        } else {
            setupNotActiveScheduleView();
        }
    }

    private void setupNotActiveScheduleView() {
        scheduleIcon.clearColorFilter();
        scheduleText.setText(R.string.add_to_my_schedule);
    }

    private void setupActiveScheduleView() {
        scheduleIcon.setColorFilter(scheduledIconColor, PorterDuff.Mode.MULTIPLY);
        scheduleText.setText(R.string.remove_to_my_schedule);
    }

    @Click({R.id.buttonSpeaker, R.id.buttonSpeaker2, R.id.voteButton, R.id.questionButton})
    void onSpeakersClick(View view) {
        switch (view.getId()) {
            case R.id.buttonSpeaker:
                getMainActivity().replaceFragment(SpeakerFragment_.builder()
                        .speakerTalkModel(slotModel.talk.speakers.get(0)).build(), true);
                break;
            case R.id.buttonSpeaker2:
                getMainActivity().replaceFragment(SpeakerFragment_.builder()
                        .speakerTalkModel(slotModel.talk.speakers.get(1)).build(), true);
                break;
            case R.id.voteButton:
                onVoteButtonClick();
                break;
            case R.id.questionButton:
                onQuestionButtonClick();
                break;
        }
    }

    private void onVoteButtonClick() {
        if (talkVoter.isVotingEnabled()) {
            talkVoter.voteForTalk(talkId, this);
        } else {
            // TODO Should I open RegisterActivity?
            Toast.makeText(getContext(), R.string.vote_not_legged_message,
                    Toast.LENGTH_SHORT).show();
        }
    }

    private void onQuestionButtonClick() {
        // TODO Labels will be changed!
        new MaterialDialog.Builder(getContext())
                .title("Ask about talk")
                .inputType(InputType.TYPE_CLASS_TEXT |
                        InputType.TYPE_TEXT_VARIATION_PERSON_NAME |
                        InputType.TYPE_TEXT_FLAG_CAP_WORDS |
                        InputType.TYPE_TEXT_FLAG_MULTI_LINE)
                .inputRange(QUESTION_MIN_CHARS_LENGTH, QUESTION_MAX_CHARS_LENGTH)
                .positiveText("Submit")
                .input("Type question here...", "", false, new MaterialDialog.InputCallback() {
                    @Override
                    public void onInput(@NonNull MaterialDialog dialog, CharSequence input) {
                        Toast.makeText(getContext(), "Question sent...",
                                Toast.LENGTH_SHORT).show();
                    }
                }).show();
    }

    @Override
    public void onTalkVotesAvailable(VoteTalkModel voteTalkModel) {
        // TODO Dev code, do not comment.
        final int count = Integer.parseInt(voteTalkModel.count);
        final String template = count == 0 ? "VOTES" : "%s VOTES";
        voteLabel.setText(String.format(template, voteTalkModel.count));
    }

    @Override
    public void onTalkVotesError() {
        // TODO Handle it.
    }

    @Override
    public void onVoteForTalkSucceed() {
        // TODO Dev code, do not comment.
        Toast.makeText(getContext(), "Zagłosowałeś!", Toast.LENGTH_SHORT).show();
        talkVoter.getVotesCountForTalk(talkId, this);
    }

    @Override
    public void onVoteForTalkFailed() {
        // TODO Dev code, do not comment.
        Toast.makeText(getContext(), "Problem podczas głosowania.", Toast.LENGTH_SHORT).show();
    }
}
