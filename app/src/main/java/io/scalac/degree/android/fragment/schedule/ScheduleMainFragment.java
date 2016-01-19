package io.scalac.degree.android.fragment.schedule;

import org.androidannotations.annotations.AfterInject;
import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.ViewById;
import org.androidannotations.annotations.res.ColorRes;

import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;

import io.scalac.degree.android.adapter.SchedulePagerAdapter;
import io.scalac.degree.android.fragment.common.BaseFragment;
import io.scalac.degree.data.manager.SlotsDataManager;
import io.scalac.degree33.R;

@EFragment(R.layout.fragment_schedules)
public class ScheduleMainFragment extends BaseFragment {

    @Bean
    SlotsDataManager slotsDataManager;

    @ViewById(R.id.tab_layout)
    TabLayout tabLayout;

    @ViewById(R.id.pager)
    ViewPager viewPager;

    @ColorRes(R.color.primary_text)
    int selectedTablColor;

    @ColorRes(R.color.tab_text_unselected)
    int unselectedTablColor;

    @ColorRes(R.color.primary_text)
    int tabStripColor;


    @AfterInject
    void afterInject() {

    }

    @AfterViews
    void afterViews() {
        final SchedulePagerAdapter adapter = new SchedulePagerAdapter(
                getChildFragmentManager(), slotsDataManager.getLastTalks());

        viewPager.setAdapter(adapter);
        tabLayout.setupWithViewPager(viewPager);
        tabLayout.setTabTextColors(unselectedTablColor, selectedTablColor);
        tabLayout.setTabMode(TabLayout.MODE_SCROLLABLE);
        tabLayout.setSelectedTabIndicatorColor(tabStripColor);
    }


    /*
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
    }*/
}
