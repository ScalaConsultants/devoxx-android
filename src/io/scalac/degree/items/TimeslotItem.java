package io.scalac.degree.items;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
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
	private Date	dateTime;
	
	public static void fillList(ArrayList<TimeslotItem> timeslotItemsList, JSONArray jsonArray) {
		timeslotItemsList.clear();
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
	
	public static ArrayList<TimeslotItem> getTimeslotItemsList(ArrayList<TimeslotItem> timeslotItemsList, Date date) {
		ArrayList<TimeslotItem> newTimeslotItemsList = new ArrayList<TimeslotItem>();
		for (TimeslotItem timeslotItem : timeslotItemsList) {
			if (timeslotItem.getDateTime().equals(date)) {
				newTimeslotItemsList.add(timeslotItem);
			}
		}
		return newTimeslotItemsList;
	}
	
	public static ArrayList<Date> getDatesList(ArrayList<TimeslotItem> timeslotItemsList) {
		ArrayList<Date> dates = new ArrayList<Date>();
		for (TimeslotItem timeslotItem : timeslotItemsList) {
			if (!dates.contains(timeslotItem.getDateTime())) {
				dates.add(timeslotItem.getDateTime());
			}
		}
		return dates;
	}
	
	public static int getInitialDatePosition(ArrayList<TimeslotItem> timeslotItemsList) {
		Calendar cal = Calendar.getInstance();
		cal.setTimeZone(TimeZone.getTimeZone("UTC"));
		cal.set(Calendar.MILLISECOND, 0);
		cal.set(Calendar.SECOND, 0);
		cal.set(Calendar.MINUTE, 0);
		cal.set(Calendar.HOUR, 0);
		cal.set(Calendar.HOUR_OF_DAY, 0);
		long currentDateMS = cal.getTimeInMillis();
		
		ArrayList<Date> dates = getDatesList(timeslotItemsList);
		for (int i = 0; i < dates.size(); i++) {
			if (dates.get(i).getTime() == currentDateMS)
				return i;
		}
		return 0;
	}
	
	public static int getInitialTimePosition(ArrayList<TimeslotItem> timeslotItemsList) {
		Calendar cal = Calendar.getInstance();
		cal.setTimeZone(TimeZone.getTimeZone("UTC"));
		long currentDateMS = cal.getTimeInMillis();
		TimeslotItem timeslotItem = timeslotItemsList.get(0);
		long prevEndTime = timeslotItem.getStartTime().getTime();
		for (int i = 0; i < timeslotItemsList.size(); i++) {
			timeslotItem = timeslotItemsList.get(i);
			if (prevEndTime <= currentDateMS && timeslotItem.getEndTime().getTime() >= currentDateMS)
				return i;
			prevEndTime = timeslotItem.getEndTime().getTime();
		}
		return 0;
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
		Calendar cal = Calendar.getInstance();
		cal.setTime(startTime);
		cal.setTimeZone(TimeZone.getTimeZone("UTC"));
		cal.set(Calendar.MILLISECOND, 0);
		cal.set(Calendar.SECOND, 0);
		cal.set(Calendar.MINUTE, 0);
		cal.set(Calendar.HOUR, 0);
		cal.set(Calendar.HOUR_OF_DAY, 0);
		this.dateTime = new Date(cal.getTimeInMillis());
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
	
	public Date getDateTime() {
		return dateTime;
	}
	
	public static class TimeslotComparator implements Comparator<TimeslotItem> {
		
		@Override
		public final int compare(TimeslotItem lhs, TimeslotItem rhs) {
			return Long.valueOf(lhs.getStartTime().getTime()).compareTo(Long.valueOf(rhs.getStartTime().getTime()));
		}
		
	}
}
