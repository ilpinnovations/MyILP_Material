package com.tcs.model;

import com.tcs.util.Constants;

public class Employee {
	private long empId;
	private String location, batch, email, name, lg;

	public Employee() {

	}

	public long getEmpId() {
		return empId;
	}

	public void setEmpId(long empId) {
		this.empId = empId;
	}

	public String getLocation() {
		return location;
	}

	public void setLocation(String location) {
		this.location = location;
	}

	public String getBatch() {
		return batch;
	}

	public void setBatch(String batch) {
		this.batch = batch;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Employee(int empId, String name, String email, String batch,
			String location) {
		this.empId = empId;
		this.location = location;
		this.batch = batch;
		this.email = email;
		this.name = name;
	}

	public int isValid() {
		if (getEmpId() < 1) {
			return Constants.EMP_ERRORS.EMP_ID.BLANK;
		}
		if (getName() == null || getName().trim().length() == 0) {
			return Constants.EMP_ERRORS.NAME.BLANK;
		}
		if (getEmail() == null || getEmail().trim().length() == 0) {
			return Constants.EMP_ERRORS.EMAIL.BLANK;
		}
		if (!android.util.Patterns.EMAIL_ADDRESS.matcher(getEmail()).matches()) {
			return Constants.EMP_ERRORS.EMAIL.INVALID;
		}
		if (getBatch() == null || getBatch().trim().length() == 0) {
			return Constants.EMP_ERRORS.BATCH.BLANK;
		}
		if (getLg() == null || getLg().trim().length() == 0) {
			return Constants.EMP_ERRORS.EMP_LG.BLANK;
		}
		if (getLocation() == null || getLocation().trim().length() == 0) {
			return Constants.EMP_ERRORS.LOCATION.BLANK;
		}
		return Constants.EMP_ERRORS.NO_ERROR;
	}

	public String getLg() {
		return lg;
	}

	public void setLg(String lg) {
		this.lg = lg;
	}
}
