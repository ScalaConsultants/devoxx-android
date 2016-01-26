package com.devoxx.android.dialog;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;

import java.util.List;

import com.devoxx.data.schedule.filter.model.RealmScheduleDayItemFilter;
import com.devoxx.data.schedule.filter.model.RealmScheduleTrackItemFilter;
import com.devoxx.R;

public class FiltersDialog {

    private static final long INDICATOR_ANIM_TIME_MS = 200;

    public interface IFiltersChangedListener {
        void onDayFiltersChanged(RealmScheduleDayItemFilter itemFilter, boolean isActive);

        void onTrackFiltersChanged(RealmScheduleTrackItemFilter itemFilter, boolean isActive);

        void onFiltersCleared();

        void onFiltersDismissed();

        void onFiltersDefault();
    }

    public static void showFiltersDialog(
            final Context context,
            final List<RealmScheduleDayItemFilter> daysFilters,
            final List<RealmScheduleTrackItemFilter> tracksFilters,
            final IFiltersChangedListener globalListener) {

        final MaterialDialog md = new MaterialDialog.Builder(context)
                .customView(R.layout.dialog_filters, true)
                .title(R.string.filters)
                .positiveText(R.string.apply)
                .negativeText(R.string.clear)
                .neutralText(R.string.default_filters)
                .onNegative((dialog, which) -> globalListener.onFiltersCleared())
                .onNeutral((dialog, which) -> globalListener.onFiltersDefault())
                .dismissListener(dialog -> globalListener.onFiltersDismissed())
                .build();

        final View customView = md.getCustomView();
        final ViewGroup daysContainer = (ViewGroup) customView.findViewById(R.id.dialogFitlersDays);
        final ViewGroup tracksContainer = (ViewGroup) customView.findViewById(R.id.dialogFitlersTracks);

        setupListeners(customView, daysContainer, tracksContainer);
        setupCheckBoxes(context, daysFilters, tracksFilters, globalListener, daysContainer, tracksContainer);

        md.show();
    }

    private static void setupCheckBoxes(
            Context context,
            List<RealmScheduleDayItemFilter> daysFilters,
            List<RealmScheduleTrackItemFilter> tracksFilters,
            IFiltersChangedListener globalListener,
            ViewGroup daysContainer, ViewGroup tracksContainer) {
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
    }

    private static void setupListeners(View customView, ViewGroup daysContainer, ViewGroup tracksContainer) {
        customView.findViewById(R.id.dialogFiltersDaysMore)
                .setOnClickListener(createOpenCloseAction(daysContainer));
        customView.findViewById(R.id.dialogFiltersTracksMore)
                .setOnClickListener(createOpenCloseAction(tracksContainer));
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
            indicatorIcon.animate().scaleY(indicatorIcon.getScaleY() * -1)
                    .setDuration(INDICATOR_ANIM_TIME_MS).start();
        };
    }
}
