package io.scalac.degree.fragments;

import io.scalac.degree.MainActivity;
import io.scalac.degree.R;
import io.scalac.degree.items.RoomItem;
import io.scalac.degree.items.SpeakerItem;
import io.scalac.degree.items.TalkItem;
import io.scalac.degree.utils.ItemNotFoundException;
import io.scalac.degree.utils.Utils;

import java.text.DateFormat;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.method.LinkMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.Switch;
import android.widget.TextView;

/**
 * A placeholder fragment containing a simple view.
 */
public class TalkFragment extends Fragment {
	/**
	 * The fragment argument representing the section number for this fragment.
	 */
	private static final String	ARG_TALK_ID	= "talk_id";
	private int							talkID;
	private TalkItem					talkItem;
	private SpeakerItem				speakerItem;
	private SpeakerItem				speaker2Item;
	boolean								isCreated;
	
	/**
	 * Returns a new instance of this fragment for the given section number.
	 */
	public static TalkFragment newInstance(int talkID) {
		TalkFragment fragment = new TalkFragment();
		Bundle args = new Bundle();
		args.putInt(ARG_TALK_ID, talkID);
		fragment.setArguments(args);
		return fragment;
	}
	
	public TalkFragment() {}
	
	private MainActivity getMainActivity() {
		return (MainActivity) getActivity();
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setRetainInstance(true);
		if (getActivity() != null) {
			init();
			isCreated = true;
		} else
			isCreated = false;
	}
	
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		if (!isCreated) {
			init();
			isCreated = true;
		}
	}
	
	private void init() {
		talkID = getArguments().getInt(ARG_TALK_ID);
		talkItem = TalkItem.getByID(talkID, getMainActivity().getTalkItemsList());
		speakerItem = SpeakerItem.getByID(talkItem.getSpeakerID(), getMainActivity().getSpeakerItemsList());
		if (talkItem.hasSpeaker2())
			speaker2Item = SpeakerItem.getByID(talkItem.getSpeaker2ID(), getMainActivity().getSpeakerItemsList());
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		getMainActivity().setDrawerIndicatorEnabled(false);
		
		View rootView = inflater.inflate(R.layout.fragment_talk, container, false);
		
		TextView textView;
		
		textView = (TextView) rootView.findViewById(R.id.textTopic);
		textView.setText(talkItem.getTopicHtml());
		
		textView = (TextView) rootView.findViewById(R.id.textDesc);
		textView.setText(talkItem.getDescriptionHtml());
		textView.setMovementMethod(LinkMovementMethod.getInstance());
		
		DateFormat dateFormat = android.text.format.DateFormat.getLongDateFormat(getActivity().getApplicationContext());
		DateFormat timeFormat = android.text.format.DateFormat.getTimeFormat(getActivity().getApplicationContext());
		
		textView = (TextView) rootView.findViewById(R.id.textDate);
		textView.setText(dateFormat.format(talkItem.getStartTime()));
		textView = (TextView) rootView.findViewById(R.id.textTimeStart);
		textView.setText(timeFormat.format(talkItem.getStartTime()));
		textView = (TextView) rootView.findViewById(R.id.textTimeEnd);
		textView.setText(timeFormat.format(talkItem.getEndTime()));
		
		textView = (TextView) rootView.findViewById(R.id.textRoom);
		try {
			RoomItem roomItem = RoomItem.getByID(talkItem.getRoomID(), getMainActivity().getRoomItemsList());
			textView.setText(roomItem.getName());
		} catch (ItemNotFoundException e) {
			textView.setText("");
			e.printStackTrace();
		}
		
		OnClickListener onClickListener = new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				switch (v.getId()) {
					case R.id.buttonSpeaker:
						getMainActivity().replaceFragment(SpeakerFragment.newInstance(speakerItem.getId()), true);
						break;
					case R.id.buttonSpeaker2:
						getMainActivity().replaceFragment(SpeakerFragment.newInstance(speaker2Item.getId()), true);
						break;
				}
			}
		};
		Button buttonSpeaker = (Button) rootView.findViewById(R.id.buttonSpeaker);
		buttonSpeaker.setText(speakerItem.getName());
		buttonSpeaker.setOnClickListener(onClickListener);
		
		Button buttonSpeaker2 = (Button) rootView.findViewById(R.id.buttonSpeaker2);
		if (speaker2Item != null) {
			buttonSpeaker2.setText(speaker2Item.getName());
			buttonSpeaker2.setOnClickListener(onClickListener);
		} else
			buttonSpeaker2.setVisibility(View.GONE);
		
		Switch switchNotify = (Switch) rootView.findViewById(R.id.switchNotify);
		switchNotify.setChecked(Utils.isNotifySet(getActivity().getApplicationContext(), talkID));
		switchNotify.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				if (isChecked)
					Utils.setNotify(getActivity().getApplicationContext(), talkID, talkItem.getStartTime().getTime(), true);
				else
					Utils.unsetNotify(getActivity().getApplicationContext(), talkID);
				getMainActivity().setNotifyMap(Utils.getAlarms(getActivity().getApplicationContext()));
			}
		});
		
		return rootView;
	}
}
