package org.feup.bondpoint;

import java.io.Serializable;

import com.google.android.gms.maps.model.Marker;

public class Bondpoint implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String bpid;
	private String bpname;
	private String bptype;
	private String bpdate;
	private String starttime;
	private String endtime;
	private String description;
	private Marker marker;

	public Marker getMarker() {
		return marker;
	}

	public void setMarker(Marker marker) {
		this.marker = marker;
	}

	public String getBpid() {
		return bpid;
	}

	public void setBpid(String bpid) {
		this.bpid = bpid;
	}

	public String getBpname() {
		return bpname;
	}

	public void setBpname(String bpname) {
		this.bpname = bpname;
	}

	public String getBptype() {
		return bptype;
	}

	public void setBptype(String bptype) {
		this.bptype = bptype;
	}

	public String getBpdate() {
		return bpdate;
	}

	public void setBpdate(String bpdate) {
		this.bpdate = bpdate;
	}

	public String getStarttime() {
		return starttime;
	}

	public void setStarttime(String starttime) {
		this.starttime = starttime;
	}

	public String getEndtime() {
		return endtime;
	}

	public void setEndtime(String endtime) {
		this.endtime = endtime;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

}
