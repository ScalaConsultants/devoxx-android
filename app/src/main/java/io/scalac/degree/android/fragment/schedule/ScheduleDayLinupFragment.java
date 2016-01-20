package io.scalac.degree.android.fragment.schedule;

import com.annimon.stream.Collectors;
import com.annimon.stream.Stream;
import com.annimon.stream.function.Function;

import org.androidannotations.annotations.AfterInject;
import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.FragmentArg;

import android.support.v7.widget.RecyclerView;
import android.view.View;

import java.util.Comparator;
import java.util.List;

import io.scalac.degree.android.adapter.ScheduleDayLineupAdapter;
import io.scalac.degree.android.fragment.common.BaseListFragment;
import io.scalac.degree.connection.model.SlotApiModel;
import io.scalac.degree.data.manager.SlotsDataManager;
import io.scalac.degree33.R;

@EFragment(R.layout.fragment_list)
public class ScheduleDayLinupFragment extends BaseListFragment {

    private static final long UNKNOWN_LINEUP_TIME = -1;

    @FragmentArg
    long lineupDayMs = UNKNOWN_LINEUP_TIME;

    @Bean
    SlotsDataManager slotsDataManager;

    @Bean
    ScheduleDayLineupAdapter scheduleDayLineupAdapter;

    @AfterInject
    void afterInject() {
        if (lineupDayMs == UNKNOWN_LINEUP_TIME) {
            throw new RuntimeException("Lineup day time must be provided!");
        }

        final List<SlotApiModel> slotsRaw = slotsDataManager.getSlotsForDay(lineupDayMs);

        final List<ScheduleDayLineupAdapter.Item> items = Stream.of(slotsRaw)
                .sorted(new Comparator<SlotApiModel>() {
                    @Override
                    public int compare(SlotApiModel lhs, SlotApiModel rhs) {
                        return lhs.fromTimeMillis < rhs.fromTimeMillis ? -1
                                : (lhs.fromTimeMillis == rhs.fromTimeMillis ? 0 : 1);
                    }
                })
                .map(new Function<SlotApiModel, ScheduleDayLineupAdapter.Item>() {
                    @Override
                    public ScheduleDayLineupAdapter.Item apply(SlotApiModel value) {
                        return ScheduleDayLineupAdapter.createFromSlotModel(value);
                    }
                })
                .collect(Collectors.<ScheduleDayLineupAdapter.Item>toList());

        scheduleDayLineupAdapter.setData(items);
    }

    @Override
    protected RecyclerView.Adapter getAdapter() {
        return scheduleDayLineupAdapter;
    }

    @Override
    public void onItemClick(RecyclerView parent, View view, int position, long id) {
        // TODO
    }
}
