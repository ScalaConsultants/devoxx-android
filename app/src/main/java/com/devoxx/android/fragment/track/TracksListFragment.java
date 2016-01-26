package com.devoxx.android.fragment.track;

import com.annimon.stream.Collectors;
import com.annimon.stream.Stream;
import com.devoxx.android.activity.TalkDetailsHostActivity;
import com.devoxx.android.adapter.track.TracksAdapter;
import com.devoxx.android.fragment.common.BaseListFragment;
import com.devoxx.connection.model.SlotApiModel;
import com.devoxx.data.manager.SlotsDataManager;
import com.devoxx.data.schedule.filter.ScheduleFilterManager;
import com.devoxx.data.schedule.filter.model.RealmScheduleDayItemFilter;
import com.devoxx.utils.DateUtils;

import org.androidannotations.annotations.AfterInject;
import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.FragmentArg;
import org.joda.time.DateTime;

import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import java.util.List;

import com.devoxx.android.activity.TalkDetailsHostActivity_;
import com.devoxx.R;

@EFragment(R.layout.fragment_list)
public class TracksListFragment extends BaseListFragment {

    @Bean
    SlotsDataManager slotsDataManager;

    @Bean
    ScheduleFilterManager scheduleFilterManager;

    @Bean
    TracksAdapter tracksAdapter;

    @FragmentArg
    String trackName;

    @AfterInject
    void afterInject() {
        final List<SlotApiModel> tracks = filterSlotsByDay();
        tracksAdapter.setData(tracks);
    }

    @Override
    public void onItemClick(RecyclerView parent, View view, int position, long id) {
        TalkDetailsHostActivity_.intent(getParentFragment())
                .slotApiModel(tracksAdapter.getClickedItem(position))
                .startForResult(TalkDetailsHostActivity.REQUEST_CODE);
    }

    @Override
    protected RecyclerView.Adapter getAdapter() {
        return tracksAdapter;
    }

    @Override
    protected boolean wantBaseClickListener() {
        return true;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        /** Called from TracksMainFragment.onActivityResult() */
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == TalkDetailsHostActivity.REQUEST_CODE &&
                resultCode == TalkDetailsHostActivity.RESULT_CODE_SUCCESS) {
            onRefreshData();
        }
    }

    private void onRefreshData() {
        final List<SlotApiModel> tracks = filterSlotsByDay();
        tracksAdapter.setData(tracks);
        tracksAdapter.notifyDataSetChanged();
    }

    private List<SlotApiModel> filterSlotsByDay() {
        final List<SlotApiModel> slots =
                Stream.of(slotsDataManager.getLastTalks())
                        .filter(slot -> slot.talk != null &&
                                slot.talk.track.equalsIgnoreCase(trackName))
                        .collect(Collectors.<SlotApiModel>toList());

        final List<RealmScheduleDayItemFilter> dayFilters
                = scheduleFilterManager.getActiveDayFilters();
        final DateTime filterTime = new DateTime();
        final DateTime slotDate = new DateTime();

        return Stream.of(slots)
                .filter(value -> {
                    final DateTime rhs = slotDate.withMillis(value.fromTimeMillis);
                    for (RealmScheduleDayItemFilter dayFilter : dayFilters) {
                        final DateTime lhs = filterTime.withMillis(dayFilter.getDayMs());
                        if (DateUtils.isSameDay(lhs, rhs)) {
                            return true;
                        }
                    }
                    return false;
                })
                .collect(Collectors.toList());
    }
}
