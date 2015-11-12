package io.scalac.degree.android.fragment;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.text.Html;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import org.androidannotations.annotations.AfterInject;
import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.FragmentArg;
import org.androidannotations.annotations.ViewById;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import io.scalac.degree.android.activity.MainActivity;
import io.scalac.degree.android.view.SlidingTabLayout;
import io.scalac.degree.connection.model.SlotApiModel;
import io.scalac.degree.data.manager.SlotsDataManager;
import io.scalac.degree33.R;

@EFragment(R.layout.fragment_tabs)
public class TabsFragment extends BaseFragment implements AdapterView.OnItemSelectedListener {

    @Bean
    SlotsDataManager slotsDataManager;

    @FragmentArg
    String tabTypeEnumName;
    @FragmentArg
    int currentDatePosition;

    @ViewById(R.id.pager)
    ViewPager viewPager;
    @ViewById(R.id.sliding_tabs)
    SlidingTabLayout slidingTabLayout;

    List<SlotApiModel> roomTabLabels;
    List<SlotApiModel> timeTabLabels;

    List<Date> datesList;
    ArrayList<String> datesNamesList;
    SectionsPagerAdapter sectionsPagerAdapter;
    int currentTabPosition = 0;
    private TabType tabType;

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        if (currentDatePosition != position) {
            currentDatePosition = position;
            final MainActivity mainActivity = getMainActivity();
            mainActivity.replaceFragment(TabsFragment_.builder()
                    .currentDatePosition(position).tabTypeEnumName(tabType.name()).build());
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
    }

    @AfterInject
    void afterInject() {
        tabType = TabType.valueOf(tabTypeEnumName);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        init();
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        init();
    }

    @Override
    public boolean needsToolbarSpinner() {
        return true;
    }

    @AfterViews
    void afterViews() {
        sectionsPagerAdapter = new SectionsPagerAdapter(getChildFragmentManager());

        viewPager.setAdapter(sectionsPagerAdapter);

        viewPager.addOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                currentTabPosition = position;
            }
        });

        slidingTabLayout.setViewPager(viewPager);
        slidingTabLayout.setCustomTabColorizer(new SlidingTabLayout.TabColorizer() {
            @Override
            public int getIndicatorColor(int position) {
                return getResources().getColor(R.color.tab_strip);
            }
        });
        viewPager.setCurrentItem(currentTabPosition);

        slidingTabLayout.setTabTextColor(Color.WHITE);
        slidingTabLayout.setDistributeEvenly(false);
        slidingTabLayout.populateTabStrip();

        setupToolbarSpinner();
    }

    private void setupToolbarSpinner() {
        final MainActivity mainActivity = getMainActivity();
        ArrayAdapter<String> spinnerAbAdapter = new ArrayAdapter<>(
                mainActivity.getSupportActionBarHelper().getThemedContext(),
                android.R.layout.simple_spinner_item, datesNamesList);
        spinnerAbAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        final Spinner spinner = getToolbarSpinner();
        spinner.setAdapter(spinnerAbAdapter);
        spinner.setOnItemSelectedListener(this);
        spinner.setSelection(currentDatePosition);
    }

    private void init() {
        datesList = slotsDataManager.createDateList();
        final Date currentDate = datesList.get(currentDatePosition);
        roomTabLabels = slotsDataManager.extractRoomLabelsForDate(currentDate);
        timeTabLabels = slotsDataManager.extractTimeLabelsForDate(currentDate);

        switch (tabType) {
            case ROOM:
                logFlurryEvent("Schedule_by_room_watched");
                break;
            case TIME:
                // TODO Do it!
                // currentTabPosition = TimeslotItem.getInitialTimePosition(timeTabLabels);
                logFlurryEvent("Schedule_by_time_watched");
                break;
        }
        datesNamesList = new ArrayList<>();
        DateFormat dateFormat = android.text.format.DateFormat.
                getMediumDateFormat(getActivity().getApplicationContext());
        for (Date date : datesList) {
            datesNamesList.add(dateFormat.format(date));
        }
    }

    public enum TabType {
        ROOM, TIME
    }

    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            switch (tabType) {
                case TIME:
                    final SlotApiModel slotModel = timeTabLabels.get(position);
                    if (slotModel.isBreak()) {
                        return BreaksFragment_.builder().
                                timeslotID(slotModel).build();
                    } else {
                        return TalksFragment_.builder().
                                slotModel(slotModel).
                                talksTypeEnumName(TalksFragment.TalksType.TIME.name()).
                                build();
                    }
                default:
                    return TalksFragment_.builder().roomID(roomTabLabels.get(position).roomId).
                            dateMs(datesList.get(currentDatePosition).getTime()).
                            talksTypeEnumName(TalksFragment.TalksType.ROOM.name()).build();
            }
        }

        @Override
        public int getCount() {
            switch (tabType) {
                case TIME:
                    return timeTabLabels.size();
                default:
                    return roomTabLabels.size();
            }
        }

        @Override
        public CharSequence getPageTitle(int position) {
            Locale l = Locale.getDefault();
            switch (tabType) {
                case TIME:
                    final SlotApiModel timeslotItem = timeTabLabels.get(position);
                    final DateFormat timeFormat = android.text.format.DateFormat.getTimeFormat(
                            getActivity().getApplicationContext());
                    final String timeStart = timeFormat.format(new Date(timeslotItem.fromTimeMillis));
                    final String timeEnd = timeFormat.format(new Date(timeslotItem.toTimeMillis));
                    return Html.fromHtml(String.format("%s %s", timeStart, timeEnd));
                default:
                    return roomTabLabels.get(position).roomName.toUpperCase(l);
            }
        }
    }
}
