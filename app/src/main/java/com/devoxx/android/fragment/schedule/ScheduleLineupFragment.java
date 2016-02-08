package com.devoxx.android.fragment.schedule;

import android.content.Intent;
import android.support.v7.widget.LinearLayoutManager;
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
import com.devoxx.data.DataInformation_;
import com.devoxx.data.schedule.filter.ScheduleFilterManager;
import com.devoxx.data.schedule.search.SearchManager;
import com.devoxx.navigation.Navigator;
import com.devoxx.utils.InfoUtil;

import org.androidannotations.annotations.AfterInject;
import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.FragmentArg;
import org.androidannotations.annotations.Receiver;
import org.androidannotations.annotations.sharedpreferences.Pref;

import java.util.List;
import java.util.concurrent.TimeUnit;

@EFragment(R.layout.fragment_list)
public class ScheduleLineupFragment extends BaseListFragment {

    public static final String REFRESH_ACTION = "com.devoxx.android.intent.REFRESH_ACTION";

    private static final long UNKNOWN_LINEUP_TIME = -1;
    private static final long CHECK_RUNNING_SESSIONS_INTERVAL_MS = TimeUnit.SECONDS.toMillis(10);

    @FragmentArg
    long lineupDayMs = UNKNOWN_LINEUP_TIME;

    @Bean
    Navigator navigator;

    @Bean
    ScheduleDayLineupAdapter scheduleDayLineupAdapter;

    @Bean
    InfoUtil infoUtil;

    @Bean
    ScheduleLineupDataCreator scheduleLineupDataCreator;

    @Pref
    DataInformation_ dataInformation;

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

    @Override
    public void onResume() {
        super.onResume();
        triggerRunningSessionCheck();
    }

    public void triggerRunningSessionCheck() {
        checkNowRunningSessions();
    }

    private void checkNowRunningSessions() {
        final int runningIndex = scheduleDayLineupAdapter.getRunningFirstPosition();
        final boolean hasRunningSession = runningIndex != ScheduleDayLineupAdapter.INVALID_RUNNING_SLOT_INDEX;
        if (!hasRunningSession) {
            return;
        }

        final long lastCheck = dataInformation.lastRunningSessionCheckTime().get();
        final long now = System.currentTimeMillis();
        final long diffMs = now - lastCheck;
        final boolean shouldForceCheck = Math.signum(diffMs) == -1; // User changed time in system.

        if (shouldForceCheck || diffMs > CHECK_RUNNING_SESSIONS_INTERVAL_MS) {
            dataInformation.edit().lastRunningSessionCheckTime().put(now).apply();
            initAdapterWithLastQuery();
        }
    }

    @Receiver(actions = {SearchManager.SEARCH_INTENT_ACTION,
            ScheduleFilterManager.FILTERS_CHANGED_ACTION})
    void onRefreshData() {
        final String lastQuery = searchManager.getLastQuery();
        List<ScheduleItem> items = searchManager.handleSearchQuery(lineupDayMs, lastQuery);
        items = filterManager.applyTracksFilter(items);
        scheduleDayLineupAdapter.setData(items);
        scheduleDayLineupAdapter.notifyDataSetChanged();

        scrollToCurrentRunningSlot();
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
        final String lastQuery = searchManager.getLastQuery();
        List<ScheduleItem> items;
        if (!TextUtils.isEmpty(lastQuery)) {
            items = searchManager.handleSearchQuery(lineupDayMs, lastQuery);
        } else {
            items = scheduleLineupDataCreator.prepareInitialData(lineupDayMs);
        }
        items = filterManager.applyTracksFilter(items);
        scheduleDayLineupAdapter.setData(items);
        scheduleDayLineupAdapter.notifyDataSetChanged();

        scrollToCurrentRunningSlot();
    }

    private void scrollToCurrentRunningSlot() {
        final int runningIndex = scheduleDayLineupAdapter.getRunningFirstPosition();
        if (runningIndex != ScheduleDayLineupAdapter.INVALID_RUNNING_SLOT_INDEX) {
            final LinearLayoutManager lm = (LinearLayoutManager) recyclerView.getLayoutManager();

            final int lastVisiblePosition = lm.findLastCompletelyVisibleItemPosition();
            if (lastVisiblePosition != -1 && lastVisiblePosition < runningIndex) {
                lm.scrollToPosition(runningIndex + 3); // Correct scroll position.
            } else {
                lm.scrollToPosition(runningIndex);
            }
        }
    }
}
