package io.scalac.degree.android.fragment;

import com.annimon.stream.Collectors;
import com.annimon.stream.Stream;

import org.androidannotations.annotations.AfterInject;
import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.FragmentArg;
import org.androidannotations.annotations.res.StringRes;

import android.content.Context;
import android.text.TextUtils;
import android.text.format.DateUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import java.text.Collator;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;

import io.scalac.degree.connection.model.SlotApiModel;
import io.scalac.degree.data.manager.NotificationsManager;
import io.scalac.degree.data.manager.SlotsDataManager;
import io.scalac.degree.utils.Utils;
import io.scalac.degree33.R;

@EFragment(R.layout.items_list_view)
public class TalksFragment extends BaseFragment implements OnItemClickListener {

	@Bean SlotsDataManager slotsDataManager;
	@Bean NotificationsManager notificationsManager;

	@StringRes(R.string.devoxx_conference) String conferenceCode;

	@FragmentArg String talksTypeEnumName;
	@FragmentArg String roomID;
	@FragmentArg SlotApiModel slotModel;
	@FragmentArg long dateMs;

	private ItemAdapter listAdapter;
	private DateFormat timeFormat;
	private boolean is12HourFormat;
	private TalksType talksType = TalksType.ALL;

	private int itemLayoutID;

	public enum TalksType {
		ALL, ROOM, TIME, NOTIFICATION;
	}

	@AfterInject void afterInject() {
		talksType = TextUtils.isEmpty(talksTypeEnumName) ? TalksType.ALL
				: TalksType.valueOf(talksTypeEnumName);

		final Context appContext = getActivity().getApplicationContext();
		timeFormat = android.text.format.DateFormat.getTimeFormat(appContext);
		is12HourFormat = !android.text.format.DateFormat.is24HourFormat(appContext);
		listAdapter = new ItemAdapter();
	}

	@Override public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		getMainActivity().replaceFragment(TalkFragment_.builder()
				.slotModel(listAdapter.getClickedItem(position)).build(), true);
	}

//	TODO Do it!
//	public void onResume() {
//		super.onResume();
//		if (talksType == TalksType.NOTIFICATION && slots != null) {
//			slots.clear();
//			slots.addAll(TalkItem.getNotificationTalkList(dataSource.getTalkItemsList(),
//					dataSource.getNotifyMap()));
//		}
//		listAdapter.notifyDataSetChanged();
//	}

	private void init() {
		List<SlotApiModel> slots = new ArrayList<>();

		switch (talksType) {
			case ROOM:
				slots = slotsDataManager.getTalksForSpecificTimeAndRoom(roomID, dateMs);
				itemLayoutID = R.layout.talks_room_list_item;
				break;
			case TIME:
				slots = slotsDataManager.getTalksForSpecificTime(slotModel.fromTimeMillis);
				itemLayoutID = R.layout.talks_time_list_item;
				break;
			case NOTIFICATION:
				logFlurryEvent("Notifications_watched");

				itemLayoutID = R.layout.talks_notify_list_item;
				break;
			default:
				logFlurryEvent("Talks_watched");
				itemLayoutID = R.layout.talks_all_list_item;

				slots = Stream.of(slotsDataManager.getLastTalks())
						.sorted(new Comparator<SlotApiModel>() {
							final Collator collator = Collator.getInstance(Locale.getDefault());

							@Override public int compare(SlotApiModel lhs, SlotApiModel rhs) {
								return collator.compare(lhs.talk.title, rhs.talk.title);
							}
						})
						.collect(Collectors.<SlotApiModel>toList());
				break;
		}
		listAdapter.setData(slots);
		listAdapter.notifyDataSetChanged();
	}

	@Override public boolean needsToolbarSpinner() {
		return setupSpinnerVisibility();
	}

	@Override public int getTitle() {
		switch (talksType) {
			case ALL:
				return R.string.drawer_menu_talks_label;
			case NOTIFICATION:
				return R.string.drawer_menu_my_schedule_label;
			case ROOM:
			case TIME:
			default:
				return -1;
		}
	}

	@AfterViews void afterViews() {
		final ListView listViewTalks = (ListView) getView();
		final View footer = Utils.getFooterView(getActivity(), listViewTalks);
		listViewTalks.addFooterView(footer);
		listViewTalks.setFooterDividersEnabled(false);
		listViewTalks.setAdapter(listAdapter);
		listViewTalks.setOnItemClickListener(this);

		init();
	}

	private boolean setupSpinnerVisibility() {
		switch (talksType) {
			case ALL:
			case NOTIFICATION:
				return false;
			case ROOM:
			case TIME:
			default:
				return true;
		}
	}

	class ItemAdapter extends BaseAdapter {

		static final int FORMAT_DATE_FLAGS = DateUtils.FORMAT_SHOW_TIME
				| DateUtils.FORMAT_SHOW_DATE | DateUtils.FORMAT_NO_YEAR;

		private List<SlotApiModel> data;

		ItemAdapter() {
			this.data = new ArrayList<>();
		}

		public void setData(List<SlotApiModel> data) {
			this.data.clear();
			this.data = data;
		}

		@Override
		public int getCount() {
			return data.size();
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
				viewItem = getActivity().getLayoutInflater().inflate(itemLayoutID, parent, false);
				holder = new ViewHolder();
				holder.textTopic = (TextView) viewItem.findViewById(R.id.textTopic);
				holder.textSpeaker = (TextView) viewItem.findViewById(R.id.textSpeakers);
				switch (talksType) {
					case ALL:
					case NOTIFICATION:
						holder.text3 = (TextView) viewItem.findViewById(R.id.textTime);
						break;
					case ROOM:
						holder.textTimeStart = (TextView) viewItem.findViewById(R.id.textTimeStart);
						holder.textTimeEnd = (TextView) viewItem.findViewById(R.id.textTimeEnd);
						if (is12HourFormat) {
							LinearLayout llTime = (LinearLayout) viewItem.findViewById(R.id.linearLayoutTime);
							llTime.getLayoutParams().width = getResources().getDimensionPixelSize(R.dimen.width_12h);
						}
						break;
					case TIME:
						holder.text3 = (TextView) viewItem.findViewById(R.id.textRoom);
						break;
					default:
						break;
				}
				holder.imageButtonNotify = (ImageButton) viewItem.findViewById(R.id.imageButtonNotify);
				holder.imageButtonNotify.setOnClickListener(alarmOnClick);
				viewItem.setTag(holder);
			} else {
				viewItem = convertView;
				holder = (ViewHolder) viewItem.getTag();
			}

			fillHolder(holder, position);

			return viewItem;
		}

		private void fillHolder(ViewHolder holder, int position) {
			final SlotApiModel slotModel = data.get(position);
			holder.textSpeaker.setText(slotModel.talk.getReadableSpeakers());
			holder.textTopic.setText(slotModel.talk.title);

			switch (talksType) {
				case ALL:
					final Context appContext = getActivity().getApplicationContext();
					final String text = String.format("%s %s",
							DateUtils.formatDateTime(appContext, slotModel.fromTimeMillis,
									FORMAT_DATE_FLAGS), slotModel.roomName);
					holder.text3.setText(text);
					break;
				case NOTIFICATION:
					final long alarmTime = notificationsManager
							.calculateAlarmTime(slotModel.fromTimeMillis);
					holder.text3.setText(DateUtils.formatDateTime(getActivity()
							.getApplicationContext(), alarmTime, FORMAT_DATE_FLAGS));
					break;
				case ROOM:
					holder.textTimeStart.setText(timeFormat.format(slotModel.fromTimeMillis));
					holder.textTimeEnd.setText(timeFormat.format(slotModel.toTimeMillis));
					break;
				case TIME:
					holder.text3.setText(slotModel.roomName);
					break;
				default:
					break;
			}
			boolean isAlarmSet = notificationsManager
					.isNotificationScheduled(slotModel.slotId);
			holder.imageButtonNotify.setImageResource(isAlarmSet ? R.drawable.ic_action_alarm
					: R.drawable.ic_action_alarm_add);
			holder.imageButtonNotify.setTag(position);
		}

		private final AlarmOnClick alarmOnClick = new AlarmOnClick();

		public SlotApiModel getClickedItem(int position) {
			return data.get(position);
		}

		private class AlarmOnClick implements OnClickListener {

			@Override
			public void onClick(View v) {
				final int position = (Integer) v.getTag();
				final SlotApiModel talkItem = data.get(position);
				boolean isAlarmSet = notificationsManager
						.isNotificationScheduled(talkItem.slotId);
				final NotificationsManager.ScheduleNotificationModel scheduleNotificationModel =
						NotificationsManager.ScheduleNotificationModel.create(talkItem, true);
				if (isAlarmSet) {
					notificationsManager.unscheduleNotification(talkItem.slotId);
				} else {
					notificationsManager.scheduleNotification(scheduleNotificationModel);
				}
				listAdapter.notifyDataSetChanged();
			}
		}

		private class ViewHolder {
			public TextView textTopic;
			public TextView text3;
			public TextView textSpeaker;
			public TextView textTimeStart;
			public TextView textTimeEnd;
			public ImageButton imageButtonNotify;
		}
	}
}
