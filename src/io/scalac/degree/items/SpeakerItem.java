package io.scalac.degree.items;

import java.text.Collator;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Locale;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.text.Html;

public class SpeakerItem {
	
	private int		id;
	private String	firstName;
	private String	lastName;
	private String	bio;
	private String	photoLink;
	private int[]	talks;
	
	public static void fillList(ArrayList<SpeakerItem> speakerItemsList, JSONArray jsonArray) {
		for (int i = 0; i < jsonArray.length(); i++) {
			try {
				if (jsonArray.get(i) instanceof JSONObject) {
					JSONObject jsonObject = jsonArray.getJSONObject(i);
					SpeakerItem speakerItem = new SpeakerItem(jsonObject);
					speakerItemsList.add(speakerItem);
				}
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
	}
	
	public SpeakerItem(JSONObject jsonObject) {
		this.id = jsonObject.optInt("id");
		this.firstName = jsonObject.optString("firstName");
		this.lastName = jsonObject.optString("lastName");
		this.bio = jsonObject.optString("bio");
		try {
			this.photoLink = jsonObject.getString("photoLink");
		} catch (JSONException e) {
			this.photoLink = null;
		}
	}
	
	public static SpeakerItem getByID(int id, ArrayList<SpeakerItem> speakerItemsList) {
		for (SpeakerItem speakerItem : speakerItemsList) {
			if (speakerItem.getId() == id)
				return speakerItem;
		}
		return null;
	}
	
	public int getId() {
		return id;
	}
	
	public String getFirstName() {
		return firstName;
	}
	
	public String getLastName() {
		return lastName;
	}
	
	public String getName() {
		return firstName + " " + lastName;
	}
	
	public String getBio() {
		return bio;
	}
	
	public String getBioShort() {
		String shortBio = Html.fromHtml(bio).toString();
		return ((shortBio.length() > 128) ? shortBio.substring(0, 128) : shortBio) + "…";
	}
	
	public CharSequence getBioHtml() {
		return Html.fromHtml(bio);
	}
	
	public String getPhotoLink() {
		if (photoLink == null)
			return null;
		return "http://2014.33degree.org/images/speakers/" + photoLink;
	}
	
	public int[] getTalks() {
		return talks;
	}
	
	public static class NameComparator implements Comparator<SpeakerItem> {
		
		private Collator	c	= Collator.getInstance(Locale.getDefault());
		
		@Override
		public final int compare(SpeakerItem lhs, SpeakerItem rhs) {
			return c.compare(lhs.getName(), rhs.getName());
		}
		
	}
}
