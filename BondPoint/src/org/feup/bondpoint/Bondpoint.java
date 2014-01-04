package org.feup.bondpoint;

import com.google.android.gms.maps.model.Marker;

public class Bondpoint {

	private String Bpname;
	private String Bpdate;
	private String Bptype;
	private String Description;
	private String Endtime;
	private String Starttime;
	private Marker marker;
	private String Bpid;

	public String getBpid() {
		return Bpid;
	}

	public void setBpid(String bpid) {
		Bpid = bpid;
	}

	public String getBpname() {
		return Bpname;
	}

	public void setBpname(String bpname) {
		Bpname = bpname;
	}

	public String getBpdate() {
		return Bpdate;
	}

	public void setBpdate(String bpdate) {
		Bpdate = bpdate;
	}

	public String getBptype() {
		return Bptype;
	}

	public void setBptype(String bptype) {
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

	// bondP.setBpname(textName.getText().toString());
	// bondP.setBpdate(textDate.getText().toString());
	// bondP.setBptype(textType.getText().toString());
	// bondP.setDescription(textDescr.getText().toString());
	// bondP.setEndtime(textEnd.getText().toString());
	// bondP.setStarttime(textStart.getText().toString());

}
