package com.ilp.ilpschedule.model;

import com.ilp.ilpschedule.util.Util;

public class Location {
	private long id;
	private String name;
	private String location;
	private String batch;

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getBatch() {
		return batch;
	}

	public void setBatch(String batch) {
		this.batch = batch;
	}

	public String getLocation() {
		return location;
	}

	public void setLocation(String location) {
		this.location = location;
	}

	/**
	 * check validation before inserting into the database
	 * 
	 * @return
	 */
	public boolean isValid() {
		return Util.checkString(name) && Util.checkString(batch)
				&& Util.checkString(location);
	}

}
