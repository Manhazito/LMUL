package org.feup.bondpoint;

import com.google.android.gms.maps.model.Marker;

public class BondPoint {

	private String id;
	private String name;
	private String type;
	private String date;
	private String startTime;
	private String endTime;
	private String description;
	private Marker marker;

	public Marker getMarker() {
		return marker;
	}

	public void setMarker(Marker marker) {
		this.marker = marker;
	}

	public String getId() {
		return id;
	}

	public void setId(String bpid) {
		this.id = bpid;
	}

	public String getName() {
		return name;
	}

	public void setName(String bpname) {
		this.name = bpname;
		if (this.marker != null)
			this.marker.setTitle(bpname);
	}

	public String getType() {
		return type;
	}

	public void setType(String bptype) {
		this.type = bptype;
	}

	public String getDate() {
		return date;
	}

	public void setDate(String bpdate) {
		this.date = bpdate;
	}

	public String getStartTime() {
		return startTime;
	}

	public void setStartTime(String starttime) {
		this.startTime = starttime;
	}

	public String getEndTime() {
		return endTime;
	}

	public void setEndTime(String endtime) {
		this.endTime = endtime;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

}
