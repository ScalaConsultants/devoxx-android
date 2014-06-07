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
import io.scalac.degree33.R;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;

import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources.NotFoundException;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.DrawerLayout;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.flurry.android.FlurryAgent;

public class MainActivity extends FragmentActivity {
	private String[]					mDrawerActions;
	private DrawerLayout				mDrawerLayout;
	private ListView					mDrawerList;
	private ActionBarDrawerToggle	mDrawerToggle;
	private int							currentNavPosition		= 0;
	private int							initialDatePosition		= 0;
	private boolean					drawerIndicatorEnabled	= true;
	
	ArrayList<TalkItem>				talkItemsList				= new ArrayList<TalkItem>();
	ArrayList<SpeakerItem>			speakerItemsList			= new ArrayList<SpeakerItem>();
	ArrayList<TimeslotItem>			timeslotItemsList			= new ArrayList<TimeslotItem>();
	ArrayList<RoomItem>				roomItemsList				= new ArrayList<RoomItem>();
	Map<String, ?>						notifyMap;
	
	ArrayAdapter<String>				arrayAdapter;
	
	public void setDrawerIndicatorEnabled(boolean enable) {
		drawerIndicatorEnabled = enable;
		mDrawerToggle.setDrawerIndicatorEnabled(enable);
	}
	
	public ArrayList<TalkItem> getTalkItemsList() {
		if (talkItemsList.isEmpty())
			initData();
		return talkItemsList;
	}
	
	public ArrayList<SpeakerItem> getSpeakerItemsList() {
		if (speakerItemsList.isEmpty())
			initData();
		return speakerItemsList;
	}
	
	public ArrayList<TimeslotItem> getTimeslotItemsList() {
		if (timeslotItemsList.isEmpty())
			initData();
		return timeslotItemsList;
	}
	
	public ArrayList<RoomItem> getRoomItemsList() {
		if (roomItemsList.isEmpty())
			initData();
		return roomItemsList;
	}
	
	public Map<String, ?> getNotifyMap() {
		if (notifyMap == null)
			initData();
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
				arrayAdapter.notifyDataSetChanged();
			}
		}
	}
	
	private void initData() {
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
			
			initialDatePosition = TimeslotItem.getInitialDatePosition(timeslotItemsList);
		} catch (NotFoundException e) {
			e.printStackTrace();
		} catch (JSONException e) {
			e.printStackTrace();
		}
		setNotifyMap(Utils.getAlarms(getApplicationContext()));
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.drawer_layout);
		
		initData();
		
		mDrawerActions = getResources().getStringArray(R.array.drawer_actions_array);
		mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
		mDrawerList = (ListView) findViewById(R.id.left_drawer);
		
		arrayAdapter = new ArrayAdapter<String>(this, R.layout.drawer_list_item, mDrawerActions) {
			
			@Override
			public View getView(int position, View convertView, ViewGroup parent) {
				convertView = super.getView(position, convertView, parent);
				((TextView) convertView).setTypeface(null, position == currentNavPosition ? Typeface.BOLD : Typeface.NORMAL);
				return convertView;
			}
		};
		// Set the adapter for the list view
		mDrawerList.setAdapter(arrayAdapter);
		// Set the list's click listener
		mDrawerList.setOnItemClickListener(new DrawerItemClickListener());
		mDrawerList.setOnItemSelectedListener(new OnItemSelectedListener() {
			
			@Override
			public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
				currentNavPosition = position;
				arrayAdapter.notifyDataSetChanged();
			}
			
			@Override
			public void onNothingSelected(AdapterView<?> parent) {}
		});
		
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
	protected void onStart() {
		super.onStart();
		FlurryAgent.onStartSession(this, Utils.FLURRY_API_KEY);
	}
	
	@Override
	protected void onStop() {
		super.onStop();
		FlurryAgent.onEndSession(this);
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
				return true;
		}
		return super.onOptionsItemSelected(item);
	}
	
	public void buttonScalacOnClick(View v) {
		FlurryAgent.logEvent("Scalac_clicked");
		Intent i = new Intent(Intent.ACTION_VIEW);
		i.setData(Uri.parse("http://scalac.io/"));
		startActivity(i);
	}
	
	public void replaceFragment(Fragment fragment) {
		replaceFragment(fragment, false, FragmentTransaction.TRANSIT_NONE);
	}
	
	public void replaceFragment(Fragment fragment, boolean addToBackStack) {
		replaceFragment(fragment, addToBackStack, FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
	}
	
	public void replaceFragment(Fragment fragment, boolean addToBackStack, int fragmentTransition) {
		FragmentManager fragmentManager = getSupportFragmentManager();
		
		// Fragment oldFragment = fragmentManager.findFragmentById(R.id.content_frame);
		// if (oldFragment != null && oldFragment.getTag() == null) {
		// fragmentManager.beginTransaction().detach(oldFragment).remove(oldFragment).commit();
		// // fragmentManager.executePendingTransactions();
		// }
		
		FragmentTransaction ft = fragmentManager.beginTransaction();
		ft.setTransition(fragmentTransition);
		ft.replace(R.id.content_frame, fragment, TAG_CONTENT_FRAGMENT);
		ft.attach(fragment);
		if (addToBackStack)
			ft.addToBackStack(null);
		ft.commit();
	}
	
	private void selectItem(final int position) {
		removeFragments();
		switch (position) {
			case 0:
				replaceFragment(TabsFragment.newInstance(TabType.TIME, initialDatePosition));
				break;
			case 1:
				replaceFragment(TabsFragment.newInstance(TabType.ROOM, initialDatePosition));
				break;
			case 2:
				replaceFragment(TalksFragment.newInstance());
				break;
			case 3:
				replaceFragment(SpeakersFragment.newInstance());
				break;
		}
	}
	
	private static final String	TAG_CONTENT_FRAGMENT	= "content_fragment";
	
	private void removeFragments() {
		FragmentManager fragmentManager = getSupportFragmentManager();
		fragmentManager.popBackStackImmediate();
		List<Fragment> fragments = fragmentManager.getFragments();
		if (fragments != null) {
			FragmentTransaction ft = fragmentManager.beginTransaction();
			for (Fragment fragment : fragments) {
				if (fragment != null)
					ft.detach(fragment).remove(fragment);
			}
			ft.commitAllowingStateLoss();
			fragmentManager.executePendingTransactions();
		}
	}
}
