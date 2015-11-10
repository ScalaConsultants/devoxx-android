package io.scalac.degree.android.fragment;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;

import org.androidannotations.annotations.AfterInject;
import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.res.StringRes;

import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import io.scalac.degree.connection.model.SpeakerShortApiModel;
import io.scalac.degree.data.manager.AbstractDataManager;
import io.scalac.degree.data.manager.SpeakersDataManager;
import io.scalac.degree.utils.AnimateFirstDisplayListener;
import io.scalac.degree.utils.Utils;
import io.scalac.degree33.R;

@EFragment(R.layout.items_list_view)
public class SpeakersFragment extends BaseFragment implements
		AbstractDataManager.IDataManagerListener<SpeakerShortApiModel> {

	@Bean SpeakersDataManager speakersDataManager;
	@StringRes(R.string.devoxx_conference) String conferenceCode;

	private ImageLoader imageLoader = ImageLoader.getInstance();
	private ImageLoadingListener animateFirstListener = new AnimateFirstDisplayListener();
	DisplayImageOptions imageLoaderOptions;
	private ListView listView;
	private ItemAdapter itemAdapter;

	@AfterInject void afterInject() {
		imageLoaderOptions = new DisplayImageOptions.Builder()
				.showImageOnLoading(R.drawable.th_background)
				.showImageForEmptyUri(R.drawable.no_photo)
				.showImageOnFail(R.drawable.no_photo)
				.delayBeforeLoading(200)
				.cacheInMemory(true)
				.cacheOnDisk(true)
				.build();
	}

	@AfterViews void afterViews() {
		logFlurryEvent("Speakers_watched");

		speakersDataManager.fetchSpeakers(conferenceCode, this);

		listView = (ListView) getView();
		final View footer = Utils.getFooterView(getActivity(), listView);
		listView.addFooterView(footer);
		listView.setFooterDividersEnabled(false);
		listView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				getMainActivity().replaceFragment(SpeakerFragment_.builder()
						.speakerShortApiModel(itemAdapter.getClickedItem(position))
						.build(), true);

			}
		});
	}

	@Override public int getTitle() {
		return R.string.drawer_menu_speakers_label;
	}

	@Override public boolean needsToolbarSpinner() {
		return false;
	}

	@Override public void onDataStartFetching() {

	}

	@Override public void onDataAvailable(List<SpeakerShortApiModel> items) {
		itemAdapter = new ItemAdapter(items);
		listView.setAdapter(itemAdapter);
	}

	@Override public void onDataAvailable(SpeakerShortApiModel item) {
		// Nothing here.
	}

	@Override public void onDataError() {
		// Nothing here.
	}

	class ItemAdapter extends BaseAdapter {

		private List<SpeakerShortApiModel> speakers = new ArrayList<>(0);

		ItemAdapter(List<SpeakerShortApiModel> speakers) {
			this.speakers.addAll(speakers);
		}

		@Override
		public int getCount() {
			return speakers.size();
		}

		@Override
		public SpeakerShortApiModel getItem(int position) {
			return speakers.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(final int position, View convertView, ViewGroup parent) {

			View viewItem;
			ViewHolder holder;

			if (convertView == null) {
				viewItem = getActivity().getLayoutInflater().inflate(R.layout.speakers_all_list_item, parent, false);
				holder = new ViewHolder();
				holder.textSpeaker = (TextView) viewItem.findViewById(R.id.textSpeaker);
				holder.textBio = (TextView) viewItem.findViewById(R.id.textBio);
				holder.imageSpeaker = (ImageView) viewItem.findViewById(R.id.imageSpeaker);
				viewItem.setTag(holder);
			} else {
				viewItem = convertView;
				holder = (ViewHolder) viewItem.getTag();
			}

			SpeakerShortApiModel speakerItem = getItem(position);
			holder.textSpeaker.setText(String.format("%s %s",
					speakerItem.firstName, speakerItem.lastName));

			imageLoader.displayImage(speakerItem.avatarURL,
					holder.imageSpeaker,
					imageLoaderOptions,
					animateFirstListener);

			return viewItem;
		}

		public SpeakerShortApiModel getClickedItem(int position) {
			return speakers.get(position);
		}

		private class ViewHolder {
			public TextView textSpeaker;
			public TextView textBio;
			public ImageView imageSpeaker;
		}
	}
}
