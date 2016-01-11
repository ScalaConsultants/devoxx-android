package io.scalac.degree.android.fragment;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.FragmentArg;
import org.androidannotations.annotations.ViewById;

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

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

import io.scalac.degree.android.activity.MainActivity;
import io.scalac.degree.android.view.SlidingTabLayout;
import io.scalac.degree.connection.model.SlotApiModel;
import io.scalac.degree.data.manager.SlotsDataManager;
import io.scalac.degree33.R;

@EFragment(R.layout.fragment_tabs)
public class TabsFragment extends BaseFragment implements AdapterView.OnItemSelectedListener {

    private static final long TAB_POSTITION_SLOT_CHECK_WINDOW_MS = TimeUnit.MINUTES.toMillis(10);

    @Bean
    SlotsDataManager slotsDataManager;

    @FragmentArg
    int currentDatePosition;

    @ViewById(R.id.pager)
    ViewPager viewPager;

    @ViewById(R.id.sliding_tabs)
    SlidingTabLayout slidingTabLayout;

    List<SlotApiModel> timeTabLabels;

    List<Date> datesList;
    ArrayList<String> datesNamesList;
    SectionsPagerAdapter sectionsPagerAdapter;
    int currentTabPosition = 0;

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        if (currentDatePosition != position) {
            currentDatePosition = position;
            final MainActivity mainActivity = getMainActivity();
            mainActivity.replaceFragment(TabsFragment_.builder()
                    .currentDatePosition(position).build());
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        init();

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
    }

    private void init() {
        timeTabLabels = new ArrayList<>();
        datesNamesList = new ArrayList<>();

        datesList = slotsDataManager.createDateList();
        if (!datesList.isEmpty()) {
            final Date currentDate = datesList.get(currentDatePosition);
            timeTabLabels = slotsDataManager.extractTimeLabelsForDate(currentDate);

            final long now = Calendar.getInstance().getTimeInMillis();
            final int size = timeTabLabels.size();
            for (int i = 0; i < size; i++) {
                final SlotApiModel slotApiModel = timeTabLabels.get(i);
                if (checkSlot(slotApiModel, now, TAB_POSTITION_SLOT_CHECK_WINDOW_MS)) {
                    currentTabPosition = i;
                    break;
                }
            }

            DateFormat dateFormat = android.text.format.DateFormat.
                    getMediumDateFormat(getActivity().getApplicationContext());
            for (Date date : datesList) {
                datesNamesList.add(dateFormat.format(date));
            }
        }
    }

    private boolean checkSlot(SlotApiModel slotApiModel, long nowMillis, long windowMillis) {
        final long start = slotApiModel.fromTimeMillis - windowMillis;
        final long stop = slotApiModel.toTimeMillis + windowMillis;
        return nowMillis >= start && nowMillis <= stop;
    }

    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
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
        }

        @Override
        public int getCount() {
            return timeTabLabels.size();
        }

        @Override
        public CharSequence getPageTitle(int position) {
            final SlotApiModel timeslotItem = timeTabLabels.get(position);
            final DateFormat timeFormat = android.text.format.DateFormat.getTimeFormat(
                    getActivity().getApplicationContext());
            final String timeStart = timeFormat.format(new Date(timeslotItem.fromTimeMillis));
            final String timeEnd = timeFormat.format(new Date(timeslotItem.toTimeMillis));
            return Html.fromHtml(String.format("%s %s", timeStart, timeEnd));
        }
    }
}
