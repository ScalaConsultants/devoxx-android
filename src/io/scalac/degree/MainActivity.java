package io.scalac.degree;

import io.scalac.degree.fragments.SpeakersFragment;
import io.scalac.degree.fragments.TabsFragment;
import io.scalac.degree.fragments.TabsFragment.TabType;
import io.scalac.degree.fragments.TalkFragment;
import io.scalac.degree.fragments.TalksFragment;
import io.scalac.degree.fragments.TalksFragment.TalksType;
import io.scalac.degree.items.BreakItem;
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

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources.NotFoundException;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.DrawerLayout;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

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
	ArrayList<BreakItem>				breakItemsList				= new ArrayList<BreakItem>();
	Map<String, ?>						notifyMap;
	
	ArrayAdapter<String>				arrayAdapter;
	
	boolean								isPaused						= false;
	
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
	
	public ArrayList<BreakItem> getBreakItemsList() {
		if (breakItemsList.isEmpty())
			initData();
		return breakItemsList;
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
	
	private void nonSelectableItemClick(int position) {
		switch (position) {
			case 5:
				Intent intent = new Intent(Intent.ACTION_SENDTO, Uri.parse(String.format(getString(R.string.feedback_link),
						getString(R.string.app_name),
						Utils.getVersionName(this),
						Utils.getVersionCode(this),
						Build.VERSION.RELEASE,
						Build.VERSION.SDK_INT,
						Build.MANUFACTURER,
						Build.MODEL)));
				intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				try {
					startActivity(intent);
				} catch (ActivityNotFoundException e) {
					Toast.makeText(getApplicationContext(), R.string.toast_feedback_activity_not_found, Toast.LENGTH_SHORT)
							.show();
				}
				break;
			default:
				break;
		}
	}
	
	private class DrawerItemClickListener implements ListView.OnItemClickListener {
		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
			if (isPaused) {
				if (currentNavPosition != -1)
					mDrawerList.setItemChecked(currentNavPosition, true);
				return;
			}
			switch (getDrawerItemViewType(position)) {
				case SECONDARY:
					if (currentNavPosition != -1)
						mDrawerList.setItemChecked(currentNavPosition, true);
					mDrawerLayout.closeDrawers();
					nonSelectableItemClick(position);
					break;
				default:
					if (position != currentNavPosition) {
						currentNavPosition = position;
						mDrawerLayout.closeDrawers();
						selectItem(currentNavPosition);
						arrayAdapter.notifyDataSetChanged();
					}
					break;
			}
		}
	}
	
	private void initData() {
		try {
			JSONArray jsonArray = new JSONArray(Utils.getRawResource(this, R.raw.timeslots));
			JSONArray jsonArray2 = new JSONArray(Utils.getRawResource(this, R.raw.breaks_timeslots));
			
			TimeslotItem.fillList(timeslotItemsList, Utils.concatArray(jsonArray, jsonArray2));
			Collections.sort(timeslotItemsList, new TimeslotComparator());
			TalkItem.fillList(talkItemsList, new JSONArray(Utils.getRawResource(this, R.raw.talks)), timeslotItemsList);
			Collections.sort(talkItemsList, new TimeComparator());
			SpeakerItem.fillList(speakerItemsList, new JSONArray(Utils.getRawResource(this, R.raw.speakers)));
			RoomItem.fillList(roomItemsList, new JSONArray(Utils.getRawResource(this, R.raw.rooms)));
			BreakItem.fillList(breakItemsList, new JSONArray(Utils.getRawResource(this, R.raw.breaks)));
			
			initialDatePosition = TimeslotItem.getInitialDatePosition(timeslotItemsList);
		} catch (NotFoundException e) {
			e.printStackTrace();
		} catch (JSONException e) {
			e.printStackTrace();
		}
		setNotifyMap(Utils.getAlarms(getApplicationContext()));
	}
	
	private enum DrawerItemViewType {
		PRIMARY, SECONDARY
	}
	
	private DrawerItemViewType getDrawerItemViewType(int position) {
		switch (position) {
			case 5:
				return DrawerItemViewType.SECONDARY;
			default:
				return DrawerItemViewType.PRIMARY;
		}
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.drawer_layout);
		getActionBar().setDisplayShowTitleEnabled(false);
		
		initData();
		
		mDrawerActions = getResources().getStringArray(R.array.drawer_actions_array);
		mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
		mDrawerList = (ListView) findViewById(R.id.left_drawer);
		
		arrayAdapter = new ArrayAdapter<String>(this, R.layout.drawer_list_item, mDrawerActions) {
			
			class ViewHolder {
				public TextView	text1;
			}
			
			@Override
			public int getItemViewType(int position) {
				return getDrawerItemViewType(position).ordinal();
			}
			
			@Override
			public int getViewTypeCount() {
				return DrawerItemViewType.values().length;
			}
			
			@Override
			public View getView(int position, View convertView, ViewGroup parent) {
				View viewItem;
				ViewHolder holder;
				
				if (convertView == null) {
					int itemLayoutID = (getDrawerItemViewType(position) == DrawerItemViewType.SECONDARY) ? R.layout.drawer_list_secondary_item
							: R.layout.drawer_list_item;
					viewItem = getLayoutInflater().inflate(itemLayoutID, parent, false);
					holder = new ViewHolder();
					holder.text1 = (TextView) viewItem.findViewById(android.R.id.text1);
					viewItem.setTag(holder);
				} else {
					viewItem = convertView;
					holder = (ViewHolder) viewItem.getTag();
				}
				holder.text1.setText(mDrawerActions[position]);
				holder.text1.setTypeface(null, position == currentNavPosition ? Typeface.BOLD : Typeface.NORMAL);
				
				return viewItem;
				
				// convertView = super.getView(position, convertView, parent);
				// ((TextView) convertView).setTypeface(null, position == currentNavPosition ? Typeface.BOLD :
				// Typeface.NORMAL);
				// return convertView;
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
	protected void onPause() {
		super.onPause();
		isPaused = true;
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		isPaused = false;
	}
	
	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
		mDrawerToggle.onConfigurationChanged(newConfig);
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
		if (isPaused)
			return;
		FragmentManager fragmentManager = getSupportFragmentManager();
		
		FragmentTransaction ft = fragmentManager.beginTransaction();
		ft.setTransition(fragmentTransition);
		ft.replace(R.id.content_frame, fragment, TAG_CONTENT_FRAGMENT);
		ft.attach(fragment);
		if (addToBackStack)
			ft.addToBackStack(null);
		ft.commit();
	}
	
	private void selectItem(final int position) {
		if (isPaused)
			return;
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
			case 4:
				replaceFragment(TalksFragment.newInstance(TalksType.NOTIFICATION));
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
			ft.commit();
			fragmentManager.executePendingTransactions();
		}
	}
}
