package io.scalac.degree.fragments;

import io.scalac.degree.MainActivity;
import io.scalac.degree.R;
import io.scalac.degree.items.SpeakerItem;
import io.scalac.degree.items.TalkItem;

import java.text.DateFormat;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.method.LinkMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
	private String						speakers;
	
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
		talkID = getArguments().getInt(ARG_TALK_ID);
		talkItem = TalkItem.getByID(talkID, getMainActivity().getTalkItemsList());
		SpeakerItem speakerItem = SpeakerItem.getByID(talkItem.getSpeakerID(), getMainActivity().getSpeakerItemsList());
		speakers = (speakerItem != null) ? speakerItem.getName() : "";
		if (talkItem.hasSpeaker2()) {
			SpeakerItem speaker2Item = SpeakerItem.getByID(talkItem.getSpeaker2ID(),
					getMainActivity().getSpeakerItemsList());
			if (speaker2Item != null)
				speakers += "\n" + speaker2Item.getName();
		}
	}
	
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.fragment_talk, container, false);
		
		TextView textView;
		
		textView = (TextView) rootView.findViewById(R.id.textTopic);
		textView.setText(talkItem.getTopic());
		
		textView = (TextView) rootView.findViewById(R.id.textSpeakers);
		
		textView.setText(speakers);
		
		textView = (TextView) rootView.findViewById(R.id.textDesc);
		textView.setText(talkItem.getDescription());
		textView.setMovementMethod(LinkMovementMethod.getInstance());
		
		DateFormat timeFormat = android.text.format.DateFormat.getTimeFormat(getActivity().getApplicationContext());
		
		textView = (TextView) rootView.findViewById(R.id.textTimeStart);
		textView.setText(timeFormat.format(talkItem.getStartTime()));
		
		textView = (TextView) rootView.findViewById(R.id.textTimeEnd);
		textView.setText(timeFormat.format(talkItem.getEndTime()));
		return rootView;
	}
}
