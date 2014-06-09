package io.scalac.degree.items;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.text.Html;

public class BreakItem {
	
	private int		id;
	private String	title;
	private String	description;
	private int		timeslotID;
	
	public static void fillList(ArrayList<BreakItem> breakItemsList, JSONArray jsonArray) {
		breakItemsList.clear();
		for (int i = 0; i < jsonArray.length(); i++) {
			try {
				if (jsonArray.get(i) instanceof JSONObject) {
					JSONObject jsonObject = jsonArray.getJSONObject(i);
					BreakItem breakItem = new BreakItem(jsonObject);
					breakItemsList.add(breakItem);
				}
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
	}
	
	public static BreakItem getByID(int id, ArrayList<BreakItem> talkItemsList) {
		for (BreakItem breakItem : talkItemsList) {
			if (breakItem.getId() == id)
				return breakItem;
		}
		return null;
	}
	
	public static ArrayList<BreakItem> getTimeslotBreakList(ArrayList<BreakItem> breakItemsList, int timeslotID) {
		ArrayList<BreakItem> roomBreakItemsList = new ArrayList<BreakItem>();
		for (BreakItem breakItem : breakItemsList) {
			if (breakItem.getTimeslotID() == timeslotID)
				roomBreakItemsList.add(breakItem);
		}
		return roomBreakItemsList;
	}
	
	public BreakItem(JSONObject jsonObject) {
		this.id = jsonObject.optInt("id");
		try {
			this.title = jsonObject.getString("title");
		} catch (JSONException e1) {
			this.title = null;
		}
		try {
			this.description = jsonObject.getString("description");
		} catch (JSONException e1) {
			this.description = null;
		}
		try {
			this.timeslotID = jsonObject.getJSONObject("timeslot").optInt("id");
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}
	
	public int getId() {
		return id;
	}
	
	public String getTitle() {
		return title;
	}
	
	public boolean hasTitle() {
		return title != null;
	}
	
	public String getDescription() {
		return description;
	}
	
	public boolean hasDescription() {
		return description != null;
	}
	
	public CharSequence getTitleHtml() {
		return Html.fromHtml(getTitle());
	}
	
	public CharSequence getDescriptionHtml() {
		return Html.fromHtml(getDescription());
	}
	
	public int getTimeslotID() {
		return timeslotID;
	}
}
