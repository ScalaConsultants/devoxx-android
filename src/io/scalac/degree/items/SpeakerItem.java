package io.scalac.degree.items;

import java.util.ArrayList;

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
		this.photoLink = jsonObject.optString("photoLink");
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
	
	public CharSequence getBio() {
		return Html.fromHtml(bio);
	}
	
	public String getPhotoLink() {
		return "http://2014.33degree.org/images/speakers/" + photoLink;
	}
	
	public int[] getTalks() {
		return talks;
	}
}
