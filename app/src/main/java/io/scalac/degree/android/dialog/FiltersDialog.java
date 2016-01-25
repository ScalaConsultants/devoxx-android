package io.scalac.degree.android.dialog;

import com.afollestad.materialdialogs.MaterialDialog;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;

import java.util.List;

import io.scalac.degree.data.schedule.filter.model.RealmScheduleDayItemFilter;
import io.scalac.degree.data.schedule.filter.model.RealmScheduleTrackItemFilter;
import io.scalac.degree33.R;

public class FiltersDialog extends MaterialDialog {

    public interface IFiltersChangedListener {
        void onDayFiltersChanged(RealmScheduleDayItemFilter itemFilter, boolean isActive);

        void onTrackFiltersChanged(RealmScheduleTrackItemFilter itemFilter, boolean isActive);

        void onFiltersCleared();

        void onFiltersDismissed();
    }

    public static void showFiltersDialog(
            final Context context,
            final List<RealmScheduleDayItemFilter> daysFilters,
            final List<RealmScheduleTrackItemFilter> tracksFilters,
            final IFiltersChangedListener globalListener) {

        final MaterialDialog md = new Builder(context)
                .customView(R.layout.dialog_filters, true)
                .title(R.string.filters)
                .positiveText(R.string.apply)
                .negativeText(R.string.clear)
                .onNegative((dialog, which) -> globalListener.onFiltersCleared())
                .dismissListener(dialog -> globalListener.onFiltersDismissed())
                .build();

        final View customView = md.getCustomView();
        final ViewGroup daysContainer = (ViewGroup) customView.findViewById(R.id.dialogFitlersDays);
        customView.findViewById(R.id.dialogFiltersDaysMore)
                .setOnClickListener(createOpenCloseAction(daysContainer));

        final ViewGroup tracksContainer = (ViewGroup) customView.findViewById(R.id.dialogFitlersTracks);
        customView.findViewById(R.id.dialogFiltersTracksMore)
                .setOnClickListener(createOpenCloseAction(tracksContainer));

        final LayoutInflater li = LayoutInflater.from(context);
        for (RealmScheduleDayItemFilter dayFilter : daysFilters) {
            daysContainer.addView(createFilterItemView(li, daysContainer, (buttonView, isChecked) ->
                            globalListener.onDayFiltersChanged(dayFilter, isChecked),
                    dayFilter.isActive(), dayFilter.getLabel()));
        }

        for (RealmScheduleTrackItemFilter trackFilter : tracksFilters) {
            tracksContainer.addView(createFilterItemView(li, tracksContainer, (buttonView, isChecked) ->
                            globalListener.onTrackFiltersChanged(trackFilter, isChecked),
                    trackFilter.isActive(), trackFilter.getLabel()));
        }

        md.show();
    }

    private static View createFilterItemView(
            final LayoutInflater li,
            final ViewGroup parent,
            final CompoundButton.OnCheckedChangeListener listener,
            final boolean isActive, final String label) {
        final View result = li.inflate(R.layout.dialog_filters_item, parent, false);
        final CheckBox checkBox = (CheckBox) result.findViewById(R.id.dialogFiltersItemCheckBox);
        checkBox.setChecked(isActive);
        checkBox.setText(label);
        checkBox.setOnCheckedChangeListener(listener);
        return result;
    }

    private static View.OnClickListener createOpenCloseAction(final View container) {
        return v -> {
            if (container.getVisibility() == View.GONE) {
                container.setVisibility(View.VISIBLE);
            } else {
                container.setVisibility(View.GONE);
            }

            final View indicatorIcon = v.findViewById(R.id.dialogFiltersMoreIcon);
            indicatorIcon.clearAnimation();
            indicatorIcon.clearAnimation();
            indicatorIcon.animate().scaleY(indicatorIcon.getScaleY() * -1)
                    .setDuration(200).start();
        };
    }

    protected FiltersDialog(Builder builder) {
        super(builder);
    }
}
