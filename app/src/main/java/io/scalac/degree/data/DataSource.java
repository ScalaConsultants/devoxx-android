package io.scalac.degree.data;

import org.androidannotations.annotations.AfterInject;
import org.androidannotations.annotations.EBean;
import org.androidannotations.annotations.RootContext;
import org.json.JSONArray;
import org.json.JSONException;

import android.content.Context;
import android.content.res.Resources;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Map;

import io.scalac.degree.items.BreakItem;
import io.scalac.degree.items.RoomItem;
import io.scalac.degree.items.SpeakerItem;
import io.scalac.degree.items.TalkItem;
import io.scalac.degree.items.TimeslotItem;
import io.scalac.degree.utils.Utils;
import io.scalac.degree33.R;

@EBean(scope = EBean.Scope.Singleton)
public class DataSource {

	@RootContext Context context;

	private int initialDatePosition = 0;

	@AfterInject void afterInject() {
		initData();
	}

	ArrayList<TalkItem> talkItemsList = new ArrayList<>();

	ArrayList<SpeakerItem> speakerItemsList = new ArrayList<>();
	ArrayList<TimeslotItem> timeslotItemsList = new ArrayList<>();
	ArrayList<RoomItem> roomItemsList = new ArrayList<>();
	ArrayList<BreakItem> breakItemsList = new ArrayList<>();
	Map<String, ?> notifyMap;

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

	private void initData() {
		try {
			JSONArray jsonArray = new JSONArray(Utils.getRawResource(context, R.raw.timeslots));
			JSONArray jsonArray2 = new JSONArray(Utils.getRawResource(context, R.raw.breaks_timeslots));

			TimeslotItem.fillList(timeslotItemsList, Utils.concatArray(jsonArray, jsonArray2));
			Collections.sort(timeslotItemsList, new TimeslotItem.TimeslotComparator());
			TalkItem.fillList(talkItemsList, new JSONArray(Utils.getRawResource(context, R.raw.talks)), timeslotItemsList);
			Collections.sort(talkItemsList, new TalkItem.TimeComparator());
			SpeakerItem.fillList(speakerItemsList, new JSONArray(Utils.getRawResource(context, R.raw.speakers)));
			RoomItem.fillList(roomItemsList, new JSONArray(Utils.getRawResource(context, R.raw.rooms)));
			BreakItem.fillList(breakItemsList, new JSONArray(Utils.getRawResource(context, R.raw.breaks)));

			initialDatePosition = TimeslotItem.getInitialDatePosition(timeslotItemsList);
		} catch (Resources.NotFoundException | JSONException e) {
			e.printStackTrace();
		}

		setNotifyMap(Utils.getAlarms(context));
	}

	public int getInitialDatePosition() {
		return initialDatePosition;
	}
}
