package io.scalac.degree.android.fragment;

import com.annimon.stream.Collectors;
import com.annimon.stream.Stream;
import com.annimon.stream.function.Function;
import com.annimon.stream.function.Predicate;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.ViewById;

import android.widget.TextView;

import java.util.List;
import java.util.Map;

import io.scalac.degree.connection.model.SlotApiModel;
import io.scalac.degree.data.manager.SlotsDataManager;
import io.scalac.degree33.R;

@EFragment(R.layout.fragment_tracks)
public class TracksFragment extends BaseFragment {

    @Bean
    SlotsDataManager slotsDataManager;

    @ViewById(R.id.tracksSize)
    TextView size;

    @AfterViews
    void afterViews() {
        final List<SlotApiModel> slots = slotsDataManager.getLastTalks();
        final Map<String, List<SlotApiModel>> tracksMap =
                Stream.of(slots).filter(new Predicate<SlotApiModel>() {
                    @Override
                    public boolean test(SlotApiModel slot) {
                        return slot.talk != null;
                    }
                }).collect(Collectors.groupingBy(new Function<SlotApiModel, String>() {
                    @Override
                    public String apply(SlotApiModel value) {
                        return value.talk.track;
                    }
                }));

        // TODO Continue when designs will be in final version.
        size.setText(String.format("Calculated tracks group: %d", tracksMap.size()));
    }
}
