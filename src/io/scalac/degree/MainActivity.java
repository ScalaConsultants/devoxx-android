package io.scalac.degree;

import io.scalac.degree.fragments.SpeakersFragment;
import io.scalac.degree.fragments.TabsFragment;
import io.scalac.degree.fragments.TabsFragment.TabType;
import io.scalac.degree.fragments.TalkFragment;
import io.scalac.degree.fragments.TalksFragment;
import io.scalac.degree.items.RoomItem;
import io.scalac.degree.items.SpeakerItem;
import io.scalac.degree.items.TalkItem;
import io.scalac.degree.items.TalkItem.TimeComparator;
import io.scalac.degree.items.TimeslotItem;
import io.scalac.degree.items.TimeslotItem.TimeslotComparator;
import io.scalac.degree.utils.Utils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;

import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources.NotFoundException;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.DrawerLayout;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

public class MainActivity extends FragmentActivity {
	private String[]					mPlanetTitles;
	private DrawerLayout				mDrawerLayout;
	private ListView					mDrawerList;
	private ActionBarDrawerToggle	mDrawerToggle;
	private int							currentNavPosition		= 0;
	private boolean					drawerIndicatorEnabled	= true;
	
	ArrayList<TalkItem>				talkItemsList				= new ArrayList<TalkItem>();
	ArrayList<SpeakerItem>			speakerItemsList			= new ArrayList<SpeakerItem>();
	ArrayList<TimeslotItem>			timeslotItemsList			= new ArrayList<TimeslotItem>();
	ArrayList<RoomItem>				roomItemsList				= new ArrayList<RoomItem>();
	Map<String, ?>						notifyMap;
	
	public void setDrawerIndicatorEnabled(boolean enable) {
		drawerIndicatorEnabled = enable;
		mDrawerToggle.setDrawerIndicatorEnabled(enable);
	}
	
	public ArrayList<TalkItem> getTalkItemsList() {
		return talkItemsList;
	}
	
	public ArrayList<SpeakerItem> getSpeakerItemsList() {
		return speakerItemsList;
	}
	
	public ArrayList<TimeslotItem> getTimeslotItemsList() {
		return timeslotItemsList;
	}
	
	public ArrayList<RoomItem> getRoomItemsList() {
		return roomItemsList;
	}
	
	public Map<String, ?> getNotifyMap() {
		return notifyMap;
	}
	
	public void setNotifyMap(Map<String, ?> notifyMap) {
		this.notifyMap = notifyMap;
	}
	
	@Override
	protected void onSaveInstanceState(Bundle outState) {
		outState.putInt("currentNavPosition", currentNavPosition);
		super.onSaveInstanceState(outState);
	}
	
	private class DrawerItemClickListener implements ListView.OnItemClickListener {
		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
			if (position != currentNavPosition) {
				currentNavPosition = position;
				mDrawerLayout.closeDrawers();
				selectItem(currentNavPosition);
			}
		}
	}
	
	private void selectItem(int position) {
		getSupportFragmentManager().popBackStack();
		switch (position) {
			case 0:
				replaceFragment(TabsFragment.newInstance(TabType.TIME));
				break;
			case 1:
				replaceFragment(TabsFragment.newInstance(TabType.ROOM));
				break;
			case 2:
				replaceFragment(TalksFragment.newInstance());
				break;
			case 3:
				replaceFragment(SpeakersFragment.newInstance());
				break;
		}
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// setContentView(R.layout.activity_main);
		setContentView(R.layout.drawer_layout);
		
		mPlanetTitles = getResources().getStringArray(R.array.drawer_actions_array);
		mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
		mDrawerList = (ListView) findViewById(R.id.left_drawer);
		
		// Set the adapter for the list view
		mDrawerList.setAdapter(new ArrayAdapter<String>(this, R.layout.drawer_list_item, mPlanetTitles));
		// Set the list's click listener
		mDrawerList.setOnItemClickListener(new DrawerItemClickListener());
		
		mDrawerToggle = new ActionBarDrawerToggle(this,
				mDrawerLayout,
				R.drawable.ic_navigation_drawer,
				R.string.drawer_open,
				R.string.drawer_close) {
			
			/** Called when a drawer has settled in a completely closed state. */
			public void onDrawerClosed(View view) {
				super.onDrawerClosed(view);
				// getActionBar().setTitle(mTitle);
				mDrawerToggle.setDrawerIndicatorEnabled(drawerIndicatorEnabled);
				invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
			}
			
			/** Called when a drawer has settled in a completely open state. */
			public void onDrawerOpened(View drawerView) {
				super.onDrawerOpened(drawerView);
				// getActionBar().setTitle(mDrawerTitle);
				mDrawerToggle.setDrawerIndicatorEnabled(true);
				invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
			}
		};
		
		// Set the drawer toggle as the DrawerListener
		mDrawerLayout.setDrawerListener(mDrawerToggle);
		
		getActionBar().setDisplayHomeAsUpEnabled(true);
		getActionBar().setHomeButtonEnabled(true);
		
		try {
			JSONArray jsonArray = new JSONArray(Utils.getRawResource(this, R.raw.timeslots));
			TimeslotItem.fillList(timeslotItemsList, jsonArray);
			Collections.sort(timeslotItemsList, new TimeslotComparator());
			jsonArray = new JSONArray(Utils.getRawResource(this, R.raw.talks));
			TalkItem.fillList(talkItemsList, jsonArray, timeslotItemsList);
			Collections.sort(talkItemsList, new TimeComparator());
			jsonArray = new JSONArray(Utils.getRawResource(this, R.raw.speakers));
			SpeakerItem.fillList(speakerItemsList, jsonArray);
			jsonArray = new JSONArray(Utils.getRawResource(this, R.raw.rooms));
			RoomItem.fillList(roomItemsList, jsonArray);
		} catch (NotFoundException e) {
			e.printStackTrace();
		} catch (JSONException e) {
			e.printStackTrace();
		}
		setNotifyMap(Utils.getAlarms(getApplicationContext()));
		if (savedInstanceState == null) {
			if (getIntent().hasExtra(Utils.EXTRA_TALK_ID)) {
				currentNavPosition = -1;
				replaceFragment(TalkFragment.newInstance(getIntent().getIntExtra(Utils.EXTRA_TALK_ID, 0)));
			} else {
				mDrawerList.setItemChecked(currentNavPosition, true);
				selectItem(currentNavPosition);
			}
		} else {
			currentNavPosition = savedInstanceState.getInt("currentNavPosition");
		}
	}
	
	@Override
	protected void onPostCreate(Bundle savedInstanceState) {
		super.onPostCreate(savedInstanceState);
		// Sync the toggle state after onRestoreInstanceState has occurred.
		mDrawerToggle.syncState();
	}
	
	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
		mDrawerToggle.onConfigurationChanged(newConfig);
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		
		if (mDrawerToggle.onOptionsItemSelected(item)) {
			return true;
		}
		
		switch (item.getItemId()) {
			case android.R.id.home:
				onBackPressed();
				break;
			case R.id.action_by_scalac:
				Intent i = new Intent(Intent.ACTION_VIEW);
				i.setData(Uri.parse("http://scalac.io/"));
				startActivity(i);
				return true;
			case R.id.action_settings:
				return true;
		}
		return super.onOptionsItemSelected(item);
	}
	
	public void replaceFragment(Fragment fragment) {
		replaceFragment(fragment, null, FragmentTransaction.TRANSIT_NONE);
	}
	
	public void replaceFragment(Fragment fragment, boolean addToBackStack) {
		replaceFragment(fragment, addToBackStack ? "backStack" : null, FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
	}
	
	public void replaceFragment(Fragment fragment, String backStackName, int fragmentTransition) {
		FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
		
		ft.setTransition(fragmentTransition);
		ft.replace(R.id.content_frame, fragment);
		// ft.attach(fragment);
		if (backStackName != null)
			ft.addToBackStack(backStackName);
		ft.commit();
	}
	
}
