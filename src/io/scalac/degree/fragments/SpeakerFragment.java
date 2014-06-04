package io.scalac.degree.fragments;

import io.scalac.degree.MainActivity;
import io.scalac.degree.R;
import io.scalac.degree.items.SpeakerItem;
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
public class SpeakerFragment extends Fragment {
	/**
	 * The fragment argument representing the section number for this fragment.
	 */
	private static final String	ARG_SPEAKER_ID	= "speaker_id";
	private int							speakerID;
	private SpeakerItem				speakerItem;
	boolean								isCreated;
	
	/**
	 * Returns a new instance of this fragment for the given section number.
	 */
	public static SpeakerFragment newInstance(int speakerID) {
		SpeakerFragment fragment = new SpeakerFragment();
		Bundle args = new Bundle();
		args.putInt(ARG_SPEAKER_ID, speakerID);
		fragment.setArguments(args);
		return fragment;
	}
	
	public SpeakerFragment() {}
	
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
		speakerID = getArguments().getInt(ARG_SPEAKER_ID);
		speakerItem = SpeakerItem.getByID(speakerID, getMainActivity().getSpeakerItemsList());
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		getMainActivity().setDrawerIndicatorEnabled(false);
		
		View rootView = inflater.inflate(R.layout.fragment_speaker, container, false);
		
		TextView textView;
		
		textView = (TextView) rootView.findViewById(R.id.textName);
		textView.setText(speakerItem.getName());
		
		textView = (TextView) rootView.findViewById(R.id.textBio);
		textView.setText(speakerItem.getBio());
		textView.setMovementMethod(LinkMovementMethod.getInstance());
		
		return rootView;
	}
}
