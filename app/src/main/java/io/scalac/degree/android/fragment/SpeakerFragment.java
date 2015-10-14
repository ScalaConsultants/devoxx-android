package io.scalac.degree.android.fragment;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.FragmentArg;
import org.androidannotations.annotations.ViewById;

import android.support.annotation.Nullable;
import android.text.method.LinkMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import io.scalac.degree.items.SpeakerItem;
import io.scalac.degree.items.TalkItem;
import io.scalac.degree.utils.AnimateFirstDisplayListener;
import io.scalac.degree33.R;

@EFragment(R.layout.fragment_speaker)
public class SpeakerFragment extends BaseFragment {

	@FragmentArg int speakerID;

	@ViewById(R.id.imageSpeaker) ImageView imageView;
	@ViewById(R.id.textName) TextView textName;
	@ViewById(R.id.textBio) TextView textBio;
	@ViewById(R.id.linearLayoutTalks) LinearLayout linearLayoutTalks;
	@ViewById(R.id.textViewTalks) View textViewTalks;

	private SpeakerItem speakerItem;

	protected ImageLoader imageLoader = ImageLoader.getInstance();
	private ImageLoadingListener animateFirstListener = new AnimateFirstDisplayListener();
	DisplayImageOptions imageLoaderOptions;

	@AfterViews void afterViews() {
		logFlurryEvent("Speaker_profile_watched");

		init();

		setupView();
	}

	@Override public boolean needsToolbarSpinner() {
		return false;
	}

	@Nullable @Override public String getTitleAsString() {
		return speakerItem.getName();
	}

	private void setupView() {
		imageLoader.displayImage(speakerItem.getPhotoLink(), imageView, imageLoaderOptions, animateFirstListener);

		textName.setText(speakerItem.getName());

		textBio.setText(speakerItem.getBioHtml());
		textBio.setMovementMethod(LinkMovementMethod.getInstance());

		int[] talksIDs = speakerItem.getTalksIDs();
		if (talksIDs != null && talksIDs.length > 0) {
			OnClickListener buttonItemClickListener = new OnClickListener() {

				@Override
				public void onClick(View v) {
					getMainActivity().replaceFragment(TalkFragment_.builder()
							.talkID((Integer) v.getTag()).build(), true);
				}
			};
			for (int talkID : talksIDs) {
				Button buttonItem = (Button) LayoutInflater.from(getActivity()).inflate(R.layout.button_item, null);
				buttonItem.setText(TalkItem.getByID(talkID, dataSource.getTalkItemsList()).getTopicHtml());
				buttonItem.setTag(talkID);
				buttonItem.setOnClickListener(buttonItemClickListener);
				linearLayoutTalks.addView(buttonItem);
			}
		} else {
			textViewTalks.setVisibility(View.GONE);
			linearLayoutTalks.setVisibility(View.GONE);
		}
	}

	private void init() {
		imageLoaderOptions = new DisplayImageOptions.Builder().showImageOnLoading(R.drawable.th_background)
				.showImageForEmptyUri(R.drawable.no_photo)
				.showImageOnFail(R.drawable.no_photo)
				.cacheInMemory(true)
				.cacheOnDisk(true)
				.build();
		speakerItem = SpeakerItem.getByID(speakerID, dataSource.getSpeakerItemsList());
	}
}
