package org.feup.bondpoint;

import com.google.android.gms.maps.model.Marker;

public class Bondpoint {

	private String Bpname = "";
	private String Bptype = "";
	private String Description = "";
	private String Endtime = "";
	private String Starttime = "";
	private Marker marker = null;
	private String Bpid = "";

	public String getID() {
		return Bpid;
	}

	public void setID(String bpid) {
		Bpid = bpid;
	}

	public String getName() {
		return Bpname;
	}

	public void setName(String bpname) {
		Bpname = bpname;
		marker.setTitle(bpname);
	}

	public String getType() {
		return Bptype;
	}

	public void setType(String bptype) {
		Bptype = bptype;
	}

	public String getDescription() {
		return Description;
	}

	public void setDescription(String description) {
		Description = description;
	}

	public String getEndtime() {
		return Endtime;
	}

	public void setEndtime(String endtime) {
		Endtime = endtime;
	}

	public String getStarttime() {
		return Starttime;
	}

	public void setStarttime(String starttime) {
		Starttime = starttime;
	}

	public Marker getMarker() {
		return marker;
	}

	public void setMarker(Marker marker) {
		this.marker = marker;
	}
}
