package io.scalac.degree.android.fragment;

import com.annimon.stream.Collectors;
import com.annimon.stream.Stream;
import com.bumptech.glide.Glide;

import org.androidannotations.annotations.AfterInject;
import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.FragmentArg;
import org.androidannotations.annotations.Receiver;
import org.androidannotations.annotations.res.ColorRes;
import org.androidannotations.annotations.sharedpreferences.Pref;

import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.text.TextUtils;
import android.text.format.DateUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import java.text.Collator;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;

import io.scalac.degree.android.activity.MainActivity;
import io.scalac.degree.android.view.ForegroundLinearLayout;
import io.scalac.degree.connection.model.SlotApiModel;
import io.scalac.degree.data.Settings_;
import io.scalac.degree.data.downloader.TracksDownloader;
import io.scalac.degree.data.manager.NotificationsManager;
import io.scalac.degree.data.manager.SlotsDataManager;
import io.scalac.degree33.R;

@EFragment(R.layout.items_list_view)
public class TalksFragment extends BaseFragment implements OnItemClickListener {

    private static final int IMAGE_LOADING_DELAY_MS = 200;
    @Bean
    SlotsDataManager slotsDataManager;

    @Bean
    NotificationsManager notificationsManager;

    @Pref
    Settings_ settings;

    @Bean
    TracksDownloader tracksDownloader;

    @FragmentArg
    String talksTypeEnumName;

    @FragmentArg
    SlotApiModel slotModel;

    @FragmentArg
    long dateMs;

    @ColorRes(R.color.scheduled_star_color)
    int scheduledStarColor;

    @ColorRes(R.color.scheduled_not_star_color)
    int notscheduledStarColor;

    private ItemAdapter listAdapter;
    private TalksType talksType = TalksType.ALL;

    private int itemLayoutID;

    @ColorRes(R.color.primary_text_45)
    int unscheduledItemColorForeground;

    @AfterInject
    void afterInject() {
        talksType = TextUtils.isEmpty(talksTypeEnumName) ? TalksType.ALL
                : TalksType.valueOf(talksTypeEnumName);
        listAdapter = new ItemAdapter(shouldFilter());
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        getMainActivity().replaceFragment(TalkFragment_.builder()
                .slotModel(listAdapter.getClickedItem(position)).build(), true);
    }

    private void init() {
        List<SlotApiModel> slots;

        switch (talksType) {
            case TIME:
                slots = slotsDataManager.getTalksForSpecificTime(slotModel.fromTimeMillis);
                itemLayoutID = R.layout.talks_time_list_item;
                break;
            default:
                itemLayoutID = R.layout.talks_all_list_item;

                slots = Stream.of(slotsDataManager.getLastTalks())
                        .sorted(new Comparator<SlotApiModel>() {
                            final Collator collator = Collator.getInstance(Locale.getDefault());

                            @Override
                            public int compare(SlotApiModel lhs, SlotApiModel rhs) {
                                return collator.compare(lhs.talk.title, rhs.talk.title);
                            }
                        })
                        .collect(Collectors.<SlotApiModel>toList());
                break;
        }
        listAdapter.setData(slots);
        listAdapter.notifyDataSetChanged();
    }

    @Override
    public boolean needsToolbarSpinner() {
        return setupSpinnerVisibility();
    }

    @Override
    public boolean needsFilterToolbarIcon() {
        return true;
    }

    @Override
    public int getTitle() {
        switch (talksType) {
            case ALL:
                return R.string.drawer_menu_talks_label;
            case TIME:
            default:
                return -1;
        }
    }

    @AfterViews
    void afterViews() {
        final ListView listViewTalks = (ListView) getView();
        listViewTalks.setFooterDividersEnabled(false);
        listViewTalks.setAdapter(listAdapter);
        listViewTalks.setOnItemClickListener(this);

        init();
    }

    @Receiver(actions = {MainActivity.INTENT_FILTER_TALKS_ACTION})
    void onFilterEvent() {
        listAdapter.notifyDataSetChangedCustom(shouldFilter());
    }

    private boolean shouldFilter() {
        return settings.filterTalksBySchedule().getOr(false);
    }

    private boolean setupSpinnerVisibility() {
        switch (talksType) {
            case ALL:
                return false;
            case TIME:
            default:
                return true;
        }
    }

    public enum TalksType {
        ALL, TIME;
    }

    class ItemAdapter extends BaseAdapter {

        static final int FORMAT_DATE_FLAGS = DateUtils.FORMAT_SHOW_TIME
                | DateUtils.FORMAT_SHOW_DATE | DateUtils.FORMAT_NO_YEAR;
        private final AlarmOnClick alarmOnClick = new AlarmOnClick();
        private List<SlotApiModel> data;
        private boolean shouldFilterScheduledItems;

        ItemAdapter(boolean shouldFilter) {
            shouldFilterScheduledItems = shouldFilter;
            this.data = new ArrayList<>();
        }

        public void setData(List<SlotApiModel> data) {
            this.data.clear();
            this.data = data;
        }

        public void notifyDataSetChangedCustom(boolean shouldFilter) {
            shouldFilterScheduledItems = shouldFilter;
            notifyDataSetChanged();
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
                holder.container = viewItem;
                holder.textTopic = (TextView) viewItem.findViewById(R.id.textTopic);
                holder.textSpeaker = (TextView) viewItem.findViewById(R.id.textSpeakers);
                switch (talksType) {
                    case ALL:
                        holder.textDateRoom = (TextView) viewItem.findViewById(R.id.textTime);
                        break;
                    case TIME:
                        holder.textDateRoom = (TextView) viewItem.findViewById(R.id.textRoom);
                        break;
                    default:
                        break;
                }
                holder.imageButtonNotify = (ImageView) viewItem.findViewById(R.id.imageButtonNotify);
                holder.imageButtonNotify.setOnClickListener(alarmOnClick);
                holder.trackIcon = (ImageView) viewItem.findViewById(R.id.talkTrackIcon);
                viewItem.setTag(holder);
            } else {
                viewItem = convertView;
                holder = (ViewHolder) viewItem.getTag();
            }

            fillHolder(holder, position);

            return viewItem;
        }

        private void fillHolder(final ViewHolder holder, int position) {
            final SlotApiModel slotModel = data.get(position);
            fillTitleAndTopic(holder, slotModel);
            fillDateAndRoom(holder, slotModel);
            setupNotificationIcon(holder, position, slotModel);
            fillTrackImage(holder, slotModel);
        }

        private void fillTrackImage(ViewHolder holder, SlotApiModel slotModel) {
            final String trackIconUrl = tracksDownloader.getTrackIconUrl(slotModel.talk.track);

            Glide.with(getMainActivity()).load(trackIconUrl)
                    .placeholder(R.drawable.th_background)
                    .error(R.drawable.no_photo)
                    .fallback(R.drawable.no_photo)
                    .into(holder.trackIcon);
        }

        private void setupNotificationIcon(ViewHolder holder, int position, SlotApiModel slotModel) {
            boolean isAlarmSet = notificationsManager
                    .isNotificationScheduled(slotModel.slotId);
            if (isAlarmSet) {
                holder.imageButtonNotify.setColorFilter(scheduledStarColor);
            } else {
                holder.imageButtonNotify.setColorFilter(notscheduledStarColor);
            }
            holder.imageButtonNotify.setTag(position);

            final ForegroundLinearLayout fl = (ForegroundLinearLayout) holder.container;
            if (shouldFilterScheduledItems && !isAlarmSet) {
                fl.setForeground(new ColorDrawable(unscheduledItemColorForeground));
            } else {
                fl.setForeground(null);
            }
        }

        private void fillDateAndRoom(ViewHolder holder, SlotApiModel slotModel) {
            switch (talksType) {
                case ALL:
                    final Context appContext = getActivity().getApplicationContext();
                    final String text = String.format("%s %s",
                            DateUtils.formatDateTime(appContext, slotModel.fromTimeMillis,
                                    FORMAT_DATE_FLAGS), slotModel.roomName);
                    holder.textDateRoom.setText(text);
                    break;
                case TIME:
                    holder.textDateRoom.setText(slotModel.roomName);
                    break;
                default:
                    // Nothing.
                    break;
            }
        }

        private void fillTitleAndTopic(ViewHolder holder, SlotApiModel slotModel) {
            holder.textSpeaker.setText(slotModel.talk.getReadableSpeakers());
            holder.textTopic.setText(slotModel.talk.title);
        }

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
                    notificationsManager.unscheduleNotification(talkItem.slotId, true);
                } else {
                    notificationsManager.scheduleNotification(scheduleNotificationModel);
                }
                listAdapter.notifyDataSetChanged();
            }
        }

        private class ViewHolder {
            public View container;
            public TextView textTopic;
            public TextView textDateRoom;
            public TextView textSpeaker;
            public ImageView imageButtonNotify;
            public ImageView trackIcon;
        }
    }
}
