package io.scalac.degree;

import io.scalac.degree.fragments.RoomsFragment;
import io.scalac.degree.items.RoomItem;
import io.scalac.degree.items.SpeakerItem;
import io.scalac.degree.items.TalkItem;
import io.scalac.degree.items.TalkItem.TimeComparator;
import io.scalac.degree.items.TimeslotItem;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;

import android.content.Intent;
import android.content.res.Resources.NotFoundException;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.view.Menu;
import android.view.MenuItem;

public class MainActivity extends FragmentActivity {
	
	ArrayList<TalkItem>		talkItemsList		= new ArrayList<TalkItem>();
	ArrayList<SpeakerItem>	speakerItemsList	= new ArrayList<SpeakerItem>();
	ArrayList<TimeslotItem>	timeslotItemsList	= new ArrayList<TimeslotItem>();
	ArrayList<RoomItem>		roomItemsList		= new ArrayList<RoomItem>();
	Map<String, ?>				notifyMap;
	
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
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// setContentView(R.layout.activity_main);
		
		try {
			JSONArray jsonArray = new JSONArray(Utils.getRawResource(this, R.raw.timeslots));
			TimeslotItem.fillList(timeslotItemsList, jsonArray);
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
		// if (savedInstanceState == null)
		replaceFragment(RoomsFragment.newInstance());
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
		switch (item.getItemId()) {
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
		ft.replace(android.R.id.content, fragment);
		ft.attach(fragment);
		if (backStackName != null)
			ft.addToBackStack(backStackName);
		ft.commit();
	}
	
}
