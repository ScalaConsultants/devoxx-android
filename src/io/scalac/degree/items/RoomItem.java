package io.scalac.degree.items;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class RoomItem {
	
	private int		id;
	private String	name;
	
	public static void fillList(ArrayList<RoomItem> roomItemsList, JSONArray jsonArray) {
		for (int i = 0; i < jsonArray.length(); i++) {
			try {
				if (jsonArray.get(i) instanceof JSONObject) {
					JSONObject jsonObject = jsonArray.getJSONObject(i);
					RoomItem roomItem = new RoomItem(jsonObject);
					roomItemsList.add(roomItem);
				}
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
	}
	
	public static RoomItem getByID(int id, ArrayList<RoomItem> roomItemsList) {
		for (RoomItem roomItem : roomItemsList) {
			if (roomItem.getId() == id)
				return roomItem;
		}
		return null;
	}
	
	public RoomItem(JSONObject jsonObject) {
		this.id = jsonObject.optInt("id");
		this.name = jsonObject.optString("name");
	}
	
	public int getId() {
		return id;
	}
	
	public String getName() {
		return name;
	}
}
