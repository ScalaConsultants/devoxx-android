package io.scalac.degree.android.fragment.track;

import com.annimon.stream.Collectors;
import com.annimon.stream.Stream;
import com.annimon.stream.function.Predicate;
import com.bumptech.glide.load.model.stream.StreamModelLoader;

import org.androidannotations.annotations.AfterInject;
import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.FragmentArg;
import org.joda.time.DateTime;

import android.support.v7.widget.RecyclerView;
import android.view.View;

import java.util.List;

import io.scalac.degree.android.activity.TalkDetailsHostActivity;
import io.scalac.degree.android.activity.TalkDetailsHostActivity_;
import io.scalac.degree.android.adapter.track.TracksAdapter;
import io.scalac.degree.android.fragment.common.BaseListFragment;
import io.scalac.degree.connection.model.SlotApiModel;
import io.scalac.degree.data.manager.SlotsDataManager;
import io.scalac.degree.data.schedule.filter.ScheduleFilterManager;
import io.scalac.degree.data.schedule.filter.model.RealmScheduleDayItemFilter;
import io.scalac.degree.data.schedule.filter.model.RealmScheduleTrackItemFilter;
import io.scalac.degree.utils.DateUtils;
import io.scalac.degree33.R;

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
        TalkDetailsHostActivity_.intent(getContext())
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
