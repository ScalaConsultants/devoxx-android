package io.scalac.degree.android.fragment;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EFragment;

import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Collections;

import io.scalac.degree.items.SpeakerItem;
import io.scalac.degree.items.SpeakerItem.NameComparator;
import io.scalac.degree.items.TalkItem;
import io.scalac.degree.utils.AnimateFirstDisplayListener;
import io.scalac.degree.utils.Utils;
import io.scalac.degree33.R;

@EFragment(R.layout.items_list_view)
public class SpeakersFragment extends BaseFragment {

	private ItemAdapter listAdapter;
	ArrayList<TalkItem> talkItemsList;
	ArrayList<SpeakerItem> speakerItemsList;
	String[] bios;

	protected ImageLoader imageLoader = ImageLoader.getInstance();
	private ImageLoadingListener animateFirstListener = new AnimateFirstDisplayListener();
	DisplayImageOptions imageLoaderOptions;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		logFlurryEvent("Speakers_watched");

		init();
	}

	@Override
	public void onResume() {
		super.onResume();
		listAdapter.notifyDataSetChanged();
	}

	@Override public int getTitle() {
		return R.string.drawer_menu_speakers_label;
	}

	@Override public boolean needsToolbarSpinner() {
		return false;
	}

	private void init() {
		imageLoaderOptions = new DisplayImageOptions.Builder().showImageOnLoading(R.drawable.th_background)
				.showImageForEmptyUri(R.drawable.no_photo)
				.showImageOnFail(R.drawable.no_photo)
				.delayBeforeLoading(200)
				.cacheInMemory(true)
				.cacheOnDisk(true)
				.build();
		talkItemsList = dataSource.getTalkItemsList();
		speakerItemsList = new ArrayList<>(dataSource.getSpeakerItemsList());
		Collections.sort(speakerItemsList, new NameComparator());
		bios = new String[speakerItemsList.size()];
		for (int i = 0; i < speakerItemsList.size(); i++) {
			bios[i] = speakerItemsList.get(i).getBioShort();
		}
		listAdapter = new ItemAdapter();
	}

	@AfterViews void afterViews() {
		final ListView listViewTalks = (ListView) getView();
		listViewTalks.addFooterView(Utils.getFooterView(getActivity()));
		listViewTalks.setFooterDividersEnabled(false);
		listViewTalks.setAdapter(listAdapter);
		listViewTalks.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				getMainActivity().replaceFragment(SpeakerFragment_.builder().
						speakerID(speakerItemsList.get(position).getId()).build(), true);

			}
		});
	}

	class ItemAdapter extends BaseAdapter {

		private class ViewHolder {
			public TextView textSpeaker;
			public TextView textBio;
			public ImageView imageSpeaker;
		}

		@Override
		public int getCount() {
			return speakerItemsList.size();
		}

		@Override
		public Object getItem(int position) {
			return position;
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

			SpeakerItem speakerItem = speakerItemsList.get(position);
			holder.textSpeaker.setText(speakerItem.getName());
			holder.textBio.setText(bios[position]);

			imageLoader.displayImage(speakerItem.getPhotoLink(),
					holder.imageSpeaker,
					imageLoaderOptions,
					animateFirstListener);

			return viewItem;
		}
	}
}
