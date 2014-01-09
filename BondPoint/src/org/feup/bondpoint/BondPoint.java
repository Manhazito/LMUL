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
	private String eventID = "";
	private String[] invitedPeople = null;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getEventId() {
		return eventID;
	}

	public void setEventId(String eventID) {
		this.eventID = eventID;
	}

	public String[] getInvitedPeople() {
		return invitedPeople;
	}

	public void setInvitedPeople(String[] invitedPeople) {
		this.invitedPeople = invitedPeople;
	}

	public Boolean addInvitedPerson(String personId) {
		String[] tmp = new String[invitedPeople.length + 1];

		if (indexOfInvited(personId) == -1) {
			System.arraycopy(invitedPeople, 0, tmp, 0, invitedPeople.length);
			tmp[invitedPeople.length] = personId;
			setInvitedPeople(tmp);

			return true;
		}
		return false;
	}

	public Boolean removeInvitedPerson(String personId) {
		String[] tmp = new String[invitedPeople.length - 1];

		int index = indexOfInvited(personId);
		if (index != -1) {
			int lengthSecondString = invitedPeople.length - index - 1;

			if (lengthSecondString < 0) { // Foi removido o único elemento...
				invitedPeople = null;
				return true;
			}

			System.arraycopy(invitedPeople, 0, tmp, 0, index);

			if (lengthSecondString > 0) {
				System.arraycopy(invitedPeople, index + 1, tmp, index,
						lengthSecondString);
			}

			setInvitedPeople(tmp);
			return true;
		}

		return false;
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

	int indexOfInvited(String personId) {
		for (int i = 0; i < invitedPeople.length; i++) {
			if (invitedPeople[i].equalsIgnoreCase(personId))
				return i;
		}
		return (-1);
	}
}
