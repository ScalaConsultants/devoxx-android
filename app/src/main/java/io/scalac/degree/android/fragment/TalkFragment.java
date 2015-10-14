package io.scalac.degree.android.fragment;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.FragmentArg;
import org.androidannotations.annotations.ViewById;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.method.LinkMovementMethod;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.Switch;
import android.widget.TextView;

import java.text.DateFormat;

import io.scalac.degree.items.RoomItem;
import io.scalac.degree.items.SpeakerItem;
import io.scalac.degree.items.TalkItem;
import io.scalac.degree.utils.ItemNotFoundException;
import io.scalac.degree.utils.Utils;
import io.scalac.degree33.R;

@EFragment(R.layout.fragment_talk)
public class TalkFragment extends BaseFragment {

	@FragmentArg int talkID;

	@ViewById(R.id.textTopic) TextView topic;
	@ViewById(R.id.textDesc) TextView desc;
	@ViewById(R.id.textDate) TextView date;
	@ViewById(R.id.textTimeStart) TextView start;
	@ViewById(R.id.textTimeEnd) TextView end;
	@ViewById(R.id.textRoom) TextView room;
	@ViewById(R.id.buttonSpeaker) Button speaker;
	@ViewById(R.id.buttonSpeaker2) Button speaker2;
	@ViewById(R.id.switchNotify) Switch notifySwitch;

	private TalkItem talkItem;
	private SpeakerItem speakerItem;
	private SpeakerItem speaker2Item;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		logFlurryEvent("Talk_info_watched");

		init();
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		init();
	}

	private void init() {
		talkItem = TalkItem.getByID(talkID, dataSource.getTalkItemsList());
		speakerItem = SpeakerItem.getByID(talkItem.getSpeakerID(),
				dataSource.getSpeakerItemsList());
		if (talkItem.hasSpeaker2()) {
			speaker2Item = SpeakerItem.getByID(talkItem.getSpeaker2ID(),
					dataSource.getSpeakerItemsList());
		}
	}

	@Nullable @Override public String getTitleAsString() {
		return talkItem.getTopic();
	}

	@Override public boolean needsToolbarSpinner() {
		return false;
	}

	@AfterViews void afterViews() {
		setHasOptionsMenu(true);

		topic.setText(talkItem.getTopicHtml());

		desc.setText(talkItem.getDescriptionHtml());
		desc.setMovementMethod(LinkMovementMethod.getInstance());

		DateFormat dateFormat = android.text.format.DateFormat
				.getLongDateFormat(getActivity().getApplicationContext());
		DateFormat timeFormat = android.text.format.DateFormat
				.getTimeFormat(getActivity().getApplicationContext());

		date.setText(dateFormat.format(talkItem.getStartTime()));
		start.setText(timeFormat.format(talkItem.getStartTime()));
		end.setText(timeFormat.format(talkItem.getEndTime()));

		try {
			RoomItem roomItem = RoomItem.getByID(talkItem.getRoomID(), dataSource.getRoomItemsList());
			room.setText(roomItem.getName());
		} catch (ItemNotFoundException e) {
			room.setText("");
			e.printStackTrace();
		}
		speaker.setText(speakerItem.getName());

		if (speaker2Item != null) {
			speaker2.setText(speaker2Item.getName());
		} else
			speaker2.setVisibility(View.GONE);

		notifySwitch.setChecked(Utils.isNotifySet(getActivity().getApplicationContext(), talkID));
		notifySwitch.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				if (isChecked) {
					buttonView.setChecked(Utils.setNotify(getActivity().getApplicationContext(),
							talkID,
							talkItem.getStartTime().getTime(),
							true));
				} else
					Utils.unsetNotify(getActivity().getApplicationContext(), talkID);
				dataSource.setNotifyMap(Utils.getAlarms(getActivity().getApplicationContext()));
			}
		});

		getMainActivity().getSupportActionBarHelper().setDisplayHomeAsUpEnabled(true);
	}

	@Click({R.id.buttonSpeaker, R.id.buttonSpeaker2}) void onSpeakersClick(View view) {
		switch (view.getId()) {
			case R.id.buttonSpeaker:
				getMainActivity().replaceFragment(SpeakerFragment_.builder()
						.speakerID(speakerItem.getId()).build(), true);
				break;
			case R.id.buttonSpeaker2:
				getMainActivity().replaceFragment(SpeakerFragment_.builder()
						.speakerID(speaker2Item.getId()).build(), true);
				break;
		}
	}
}
