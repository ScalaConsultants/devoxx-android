package io.scalac.degree.items;

public class RoomItem {
	
	private int				id;
	private CharSequence	name;
	
	public RoomItem(int id, CharSequence name) {
		this.id = id;
		this.name = name;
	}
	
	public int getId() {
		return id;
	}
	
	public CharSequence getName() {
		return name;
	}
}
