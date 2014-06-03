package io.scalac.degree.items;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class TimeslotItem {
	
	private int		id;
	private Date	startTime;
	private Date	endTime;
	
	public static void fillList(ArrayList<TimeslotItem> timeslotItemsList, JSONArray jsonArray) {
		for (int i = 0; i < jsonArray.length(); i++) {
			try {
				if (jsonArray.get(i) instanceof JSONObject) {
					JSONObject jsonObject = jsonArray.getJSONObject(i);
					TimeslotItem timeslotItem = new TimeslotItem(jsonObject);
					timeslotItemsList.add(timeslotItem);
				}
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
	}
	
	public static TimeslotItem getByID(int id, ArrayList<TimeslotItem> timeslotItemsList) {
		for (TimeslotItem timeslotItem : timeslotItemsList) {
			if (timeslotItem.getId() == id)
				return timeslotItem;
		}
		return null;
	}
	
	public TimeslotItem(JSONObject jsonObject) {
		this.id = jsonObject.optInt("id");
		DateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.US);
		sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
		try {
			this.startTime = sdf.parse(jsonObject.optString("startTime"));
			this.endTime = sdf.parse(jsonObject.optString("endTime"));
		} catch (ParseException e) {
			e.printStackTrace();
		}
	}
	
	public int getId() {
		return id;
	}
	
	public Date getStartTime() {
		return startTime;
	}
	
	public Date getEndTime() {
		return endTime;
	}
	
	public static class TimeslotComparator implements Comparator<TimeslotItem> {
		
		@Override
		public final int compare(TimeslotItem lhs, TimeslotItem rhs) {
			return Long.valueOf(lhs.getStartTime().getTime()).compareTo(Long.valueOf(rhs.getStartTime().getTime()));
		}
		
	}
}
