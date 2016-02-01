package com.devoxx.android.fragment.schedule;

import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;

import com.annimon.stream.Optional;
import com.devoxx.R;
import com.devoxx.android.activity.TalkDetailsHostActivity;
import com.devoxx.android.adapter.schedule.ScheduleDayLineupAdapter;
import com.devoxx.android.adapter.schedule.model.ScheduleItem;
import com.devoxx.android.adapter.schedule.model.creator.ScheduleLineupDataCreator;
import com.devoxx.android.fragment.common.BaseListFragment;
import com.devoxx.connection.model.SlotApiModel;
import com.devoxx.data.schedule.filter.ScheduleFilterManager;
import com.devoxx.data.schedule.search.ScheduleLineupSearchManager;
import com.devoxx.navigation.Navigator;
import com.devoxx.utils.InfoUtil;

import org.androidannotations.annotations.AfterInject;
import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.FragmentArg;
import org.androidannotations.annotations.Receiver;

import java.util.List;

@EFragment(R.layout.fragment_list)
public class ScheduleLineupFragment extends BaseListFragment {

    public static final String REFRESH_ACTION = "com.devoxx.android.intent.REFRESH_ACTION";

    private static final long UNKNOWN_LINEUP_TIME = -1;

    @FragmentArg
    long lineupDayMs = UNKNOWN_LINEUP_TIME;

    @Bean
    Navigator navigator;

    @Bean
    ScheduleDayLineupAdapter scheduleDayLineupAdapter;

    @Bean
    ScheduleFilterManager scheduleFilterManager;

    @Bean
    ScheduleLineupSearchManager scheduleLineupSearchManager;

    @Bean
    InfoUtil infoUtil;

    @Bean
    ScheduleLineupDataCreator scheduleLineupDataCreator;

    @AfterInject
    void afterInject() {
        if (lineupDayMs == UNKNOWN_LINEUP_TIME) {
            throw new RuntimeException("Lineup day time must be provided!");
        }
    }

    @Override
    protected void afterViews() {
        super.afterViews();
        scheduleDayLineupAdapter.setListener(this);
        initAdapterWithLastQuery();
    }

    @Receiver(actions = {ScheduleLineupSearchManager.SEARCH_INTENT_ACTION,
            ScheduleFilterManager.FILTERS_CHANGED_ACTION})
    void onRefreshData() {
        final String lastQuery = scheduleLineupSearchManager.getLastQuery();
        List<ScheduleItem> items = scheduleLineupSearchManager
                .handleSearchQuery(lineupDayMs, lastQuery);
        items = scheduleFilterManager.applyTracksFilter(items);
        scheduleDayLineupAdapter.setData(items);
        scheduleDayLineupAdapter.notifyDataSetChanged();
    }

    @Override
    protected RecyclerView.Adapter getAdapter() {
        return scheduleDayLineupAdapter;
    }

    @Override
    public void onItemClick(RecyclerView parent, View view, int position, long id) {
        final Optional<SlotApiModel> slotApiModel = scheduleDayLineupAdapter.getClickedSlot(position);
        if (slotApiModel.isPresent() && slotApiModel.get().isTalk()) {
            navigator.openTalkDetails(getMainActivity(), slotApiModel.get(), getParentFragment(), false);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        /** Called from ScheduleMainFragment.onActivityResult() */
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == TalkDetailsHostActivity.REQUEST_CODE &&
                resultCode == TalkDetailsHostActivity.RESULT_CODE_SUCCESS) {
            onRefreshData();
        }
    }

    @Receiver(actions = {REFRESH_ACTION})
    void onTalkNotification() {
        onRefreshData();
    }

    public static Intent getRefreshIntent() {
        return new Intent(REFRESH_ACTION);
    }

    private void initAdapterWithLastQuery() {
        final String lastQuery = scheduleLineupSearchManager.getLastQuery();
        List<ScheduleItem> items;
        if (!TextUtils.isEmpty(lastQuery)) {
            items = scheduleLineupSearchManager.handleSearchQuery(lineupDayMs, lastQuery);
        } else {
            items = scheduleLineupDataCreator.prepareInitialData(lineupDayMs);
        }
        items = scheduleFilterManager.applyTracksFilter(items);
        scheduleDayLineupAdapter.setData(items);

        final int runningIndex = scheduleDayLineupAdapter.getRunningFirstPosition();
        if (runningIndex != ScheduleDayLineupAdapter.INVALID_RUNNING_SLOT_INDEX) {
            recyclerView.scrollToPosition(runningIndex);
        }
    }
}
