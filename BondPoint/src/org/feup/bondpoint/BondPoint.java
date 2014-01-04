package org.feup.bondpoint;

import com.google.android.gms.maps.model.Marker;

public class BondPoint {

	private String id = "";
	private String name = "";
	private String type = "";
	private String initDateTime = "";
	private String endDimeTime = "";
	private String description = "";
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

	public String getInitDateTime() {
		return initDateTime;
	}

	public void setInitDateTime(String bpdate) {
		this.initDateTime = bpdate;
	}

	public String getEndDateTime() {
		return endDimeTime;
	}

	public void setEndDateTime(String endtime) {
		this.endDimeTime = endtime;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

}
