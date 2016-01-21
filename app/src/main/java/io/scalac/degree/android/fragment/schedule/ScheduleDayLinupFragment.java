package io.scalac.degree.android.fragment.schedule;

import org.androidannotations.annotations.AfterInject;
import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.FragmentArg;

import android.support.v7.widget.RecyclerView;
import android.view.View;

import java.util.List;

import io.scalac.degree.android.adapter.schedule.ScheduleDayLineupAdapter;
import io.scalac.degree.android.adapter.schedule.model.ScheduleItem;
import io.scalac.degree.android.adapter.schedule.model.creator.ScheduleLineupDataCreator;
import io.scalac.degree.android.fragment.talk.TalkFragment_;
import io.scalac.degree.connection.model.SlotApiModel;
import io.scalac.degree.utils.InfoUtil;
import io.scalac.degree.utils.Logger;
import io.scalac.degree33.R;

@EFragment(R.layout.fragment_list)
public class ScheduleDayLinupFragment extends BaseScheduleLineupListFragment {

    private static final long UNKNOWN_LINEUP_TIME = -1;

    @FragmentArg
    long lineupDayMs = UNKNOWN_LINEUP_TIME;

    @Bean
    ScheduleDayLineupAdapter scheduleDayLineupAdapter;

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
        final List<ScheduleItem> items = scheduleLineupDataCreator
                .prepareInitialData(lineupDayMs);
        scheduleDayLineupAdapter.setData(items);
    }

    @Override
    protected RecyclerView.Adapter getAdapter() {
        return scheduleDayLineupAdapter;
    }

    @Override
    public void onItemClick(RecyclerView parent, View view, int position, long id) {
        final SlotApiModel slotApiModel = scheduleDayLineupAdapter.getClickedSlot(position);
        Logger.l("Clicked slot: " + slotApiModel);

        if (slotApiModel.isTalk()) {
            getMainActivity().replaceFragment(TalkFragment_.builder()
                    .slotModel(slotApiModel).build(), true);
        } else {
            infoUtil.showToast("Nothing here...");
        }
    }
}
