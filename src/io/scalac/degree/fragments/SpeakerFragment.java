package io.scalac.degree.fragments;

import io.scalac.degree.MainActivity;
import io.scalac.degree.items.SpeakerItem;
import io.scalac.degree.utils.AnimateFirstDisplayListener;
import io.scalac.degree33.R;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.method.LinkMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;

/**
 * A placeholder fragment containing a simple view.
 */
public class SpeakerFragment extends Fragment {
	/**
	 * The fragment argument representing the section number for this fragment.
	 */
	private static final String	ARG_SPEAKER_ID			= "speaker_id";
	private int							speakerID;
	private SpeakerItem				speakerItem;
	boolean								isCreated;
	
	protected ImageLoader			imageLoader				= ImageLoader.getInstance();
	private ImageLoadingListener	animateFirstListener	= new AnimateFirstDisplayListener();
	DisplayImageOptions				imageLoaderOptions;
	
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
		imageLoaderOptions = new DisplayImageOptions.Builder().showImageOnLoading(R.drawable.th_background)
				.showImageForEmptyUri(R.drawable.th_photo)
				.showImageOnFail(R.drawable.th_photo)
				.cacheInMemory(true)
				.cacheOnDisk(true)
				.build();
		
		speakerID = getArguments().getInt(ARG_SPEAKER_ID);
		speakerItem = SpeakerItem.getByID(speakerID, getMainActivity().getSpeakerItemsList());
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		getMainActivity().setDrawerIndicatorEnabled(false);
		
		View rootView = inflater.inflate(R.layout.fragment_speaker, container, false);
		
		ImageView imageView = (ImageView) rootView.findViewById(R.id.imageSpeaker);
		
		imageLoader.displayImage(speakerItem.getPhotoLink(), imageView, imageLoaderOptions, animateFirstListener);
		
		TextView textView;
		
		textView = (TextView) rootView.findViewById(R.id.textName);
		textView.setText(speakerItem.getName());
		
		textView = (TextView) rootView.findViewById(R.id.textBio);
		textView.setText(speakerItem.getBioHtml());
		textView.setMovementMethod(LinkMovementMethod.getInstance());
		
		return rootView;
	}
}
