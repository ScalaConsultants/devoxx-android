package io.scalac.degree.android.fragment;

import com.annimon.stream.Collectors;
import com.annimon.stream.Stream;
import com.annimon.stream.function.Function;
import com.annimon.stream.function.Predicate;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.FragmentArg;
import org.androidannotations.annotations.ViewById;
import org.lucasr.twowayview.ItemClickSupport;

import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import java.util.List;
import java.util.Map;

import io.scalac.degree.android.adapter.TracksAdapter;
import io.scalac.degree.connection.model.SlotApiModel;
import io.scalac.degree.data.manager.SlotsDataManager;
import io.scalac.degree33.R;

@EFragment(R.layout.fragment_tracks_list)
public class TracksListFragment extends BaseFragment implements ItemClickSupport.OnItemClickListener {

    @Bean
    SlotsDataManager slotsDataManager;

    @ViewById(R.id.tracksList)
    RecyclerView recyclerView;

    @Bean
    TracksAdapter tracksAdapter;

    @FragmentArg
    String trackName;

    @AfterViews
    void afterViews() {
        final List<SlotApiModel> slots = slotsDataManager.getLastTalks();
        final List<SlotApiModel> tracks =
                Stream.of(slots).filter(new Predicate<SlotApiModel>() {
                    @Override
                    public boolean test(SlotApiModel slot) {
                        return slot.talk != null && slot.talk.track.equalsIgnoreCase(trackName);
                    }
                }).collect(Collectors.<SlotApiModel>toList());
        tracksAdapter.setData(tracks);

        setupList();
    }

    @Override
    public void onItemClick(RecyclerView parent, View view, int position, long id) {
        getMainActivity().replaceFragment(TalkFragment_.builder()
                .slotModel(tracksAdapter.getClickedItem(position)).build(), true);
    }

    private void setupList() {
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLongClickable(false);
        ItemClickSupport clickSupport = ItemClickSupport.addTo(recyclerView);
        recyclerView.setAdapter(tracksAdapter);
        clickSupport.setOnItemClickListener(this);
    }
}
