package io.scalac.degree.android.fragment;

import org.androidannotations.annotations.AfterInject;
import org.androidannotations.annotations.AfterViews;
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
import java.util.Date;
import java.util.Locale;

import io.scalac.degree.android.activity.MainActivity;
import io.scalac.degree.android.view.SlidingTabLayout;
import io.scalac.degree.items.RoomItem;
import io.scalac.degree.items.TimeslotItem;
import io.scalac.degree33.R;

@EFragment(R.layout.fragment_tabs)
public class TabsFragment extends BaseFragment implements AdapterView.OnItemSelectedListener {

	SectionsPagerAdapter sectionsPagerAdapter;

	@ViewById(R.id.pager) ViewPager viewPager;
	@ViewById(R.id.sliding_tabs) SlidingTabLayout slidingTabLayout;

	@FragmentArg String tabTypeEnumName;
	@FragmentArg int currentDatePosition;

	ArrayList<RoomItem> roomItemsList;
	ArrayList<TimeslotItem> timeslotItemsList;
	ArrayList<Date> datesList;
	ArrayList<String> datesNamesList;
	private TabType tabType;

	int currentTabPosition = 0;

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

	public enum TabType {
		ROOM, TIME
	}

	@AfterInject void afterInject() {
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

	@Override public boolean needsToolbarSpinner() {
		return true;
	}

	@AfterViews void afterViews() {
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
			@Override public int getIndicatorColor(int position) {
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
		roomItemsList = dataSource.getRoomItemsList();
		datesList = TimeslotItem.getDatesList(dataSource.getTimeslotItemsList());
		timeslotItemsList = TimeslotItem.getTimeslotItemsList(dataSource.getTimeslotItemsList(),
				datesList.get(currentDatePosition));
		switch (tabType) {
			case ROOM:
				logFlurryEvent("Schedule_by_room_watched");
				break;
			case TIME:
				currentTabPosition = TimeslotItem.getInitialTimePosition(timeslotItemsList);
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

	public class SectionsPagerAdapter extends FragmentPagerAdapter {

		public SectionsPagerAdapter(FragmentManager fm) {
			super(fm);
		}

		@Override
		public Fragment getItem(int position) {
			switch (tabType) {
				case TIME:
					TimeslotItem timeslotItem = timeslotItemsList.get(position);
					switch (timeslotItem.getTimeslotType()) {
						case BREAK:
							return BreaksFragment_.builder().
									timeslotID(timeslotItem.getId()).build();
						default:
							return TalksFragment_.builder().
									timeslotID(timeslotItem.getId()).
									talksTypeEnumName(TalksFragment.TalksType.TIME.name()).
									build();
					}
				default:
					return TalksFragment_.builder().roomID(roomItemsList.get(position).getId()).
							dateMs(datesList.get(currentDatePosition).getTime()).
							talksTypeEnumName(TalksFragment.TalksType.ROOM.name()).build();
			}
		}

		@Override
		public int getCount() {
			// Show total pages.
			switch (tabType) {
				case TIME:
					return timeslotItemsList.size();
				default:
					return roomItemsList.size();
			}
		}

		@Override
		public CharSequence getPageTitle(int position) {
			Locale l = Locale.getDefault();
			switch (tabType) {
				case TIME:
					TimeslotItem timeslotItem = timeslotItemsList.get(position);
					DateFormat timeFormat = android.text.format.DateFormat.getTimeFormat(getActivity().getApplicationContext());
					String timeStart = timeFormat.format(timeslotItem.getStartTime());
					String timeEnd = timeFormat.format(timeslotItem.getEndTime());
					if (timeStart.contains(" AM")) {
						if (timeEnd.contains(" AM"))
							timeStart = timeStart.replaceAll(" AM", "");
					} else if (timeStart.contains(" PM") && timeEnd.contains(" PM"))
						timeStart = timeStart.replaceAll(" PM", "");
					String time = timeStart + "  " + timeEnd;
					return Html.fromHtml(time);
				default:
					return roomItemsList.get(position).getName().toUpperCase(l);
			}
		}
	}
}
