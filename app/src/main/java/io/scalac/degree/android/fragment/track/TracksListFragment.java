package io.scalac.degree.android.fragment.track;

import com.annimon.stream.Collectors;
import com.annimon.stream.Stream;
import com.annimon.stream.function.Predicate;

import org.androidannotations.annotations.AfterInject;
import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.FragmentArg;
import org.androidannotations.annotations.ViewById;

import android.support.v7.widget.RecyclerView;
import android.view.View;

import java.util.List;

import io.scalac.degree.android.adapter.TracksAdapter;
import io.scalac.degree.android.fragment.common.BaseListFragment;
import io.scalac.degree.android.fragment.talk.TalkFragment_;
import io.scalac.degree.connection.model.SlotApiModel;
import io.scalac.degree.data.manager.SlotsDataManager;
import io.scalac.degree33.R;

@EFragment(R.layout.fragment_list)
public class TracksListFragment extends BaseListFragment {

    @Bean
    SlotsDataManager slotsDataManager;

    @Bean
    TracksAdapter tracksAdapter;

    @FragmentArg
    String trackName;

    @AfterInject
    void afterInject() {
        final List<SlotApiModel> tracks =
                Stream.of(slotsDataManager.getLastTalks())
                        .filter(new Predicate<SlotApiModel>() {
                            @Override
                            public boolean test(SlotApiModel slot) {
                                return slot.talk != null &&
                                        slot.talk.track.equalsIgnoreCase(trackName);
                            }
                        })
                        .collect(Collectors.<SlotApiModel>toList());
        tracksAdapter.setData(tracks);
    }

    @Override
    public void onItemClick(RecyclerView parent, View view, int position, long id) {
        getMainActivity().replaceFragment(TalkFragment_.builder()
                .slotModel(tracksAdapter.getClickedItem(position)).build(), true);
    }

    @Override
    protected RecyclerView.Adapter getAdapter() {
        return tracksAdapter;
    }
}
