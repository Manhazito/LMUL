package org.feup.bondpoint;

import com.google.android.gms.maps.model.Marker;

public class BondPoint {

	private String name = "";
	private String type = "";
	private String description = "";
	private String endTime = "";
	private String startTime = "";
	private Marker marker = null;
	private String id = "";
	private String event_id = "";
	private String[] invited_people = {};

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getEvent_id() {
		return event_id;
	}

	public void setEvent_id(String event_id) {
		this.event_id = event_id;
	}

	public String[] getInvited_people() {
		return invited_people;
	}

	public void setInvited_people(String[] invited_people) {
		this.invited_people = invited_people;
	}

	public String getID() {
		return id;
	}

	public void setID(String id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
		this.marker.setTitle(name);
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getEndTime() {
		return endTime;
	}

	public void setEndTime(String endTime) {
		this.endTime = endTime;
	}

	public String getStartTime() {
		return startTime;
	}

	public void setStartTime(String startTime) {
		this.startTime = startTime;
	}

	public Marker getMarker() {
		return marker;
	}

	public void setMarker(Marker marker) {
		this.marker = marker;
		this.id = marker.getId();
	}
}
