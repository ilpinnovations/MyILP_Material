package com.ilp.ilpschedule.model;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class Notification {
	private Date date;
	public static SimpleDateFormat inputDateFormat = new SimpleDateFormat(
			"yyyy-MM-dd HH:mm:ss", Locale.US);
	public static SimpleDateFormat outputDateFormat = new SimpleDateFormat(
			"E, dd MMM yyyy", Locale.US);

	@Override
	public String toString() {
		return "Notification [date=" + date + ", msg=" + msg + ", id=" + id
				+ "]";
	}

	public Notification(Date date, String msg, int id) {
		super();
		this.date = date;
		this.msg = msg;
		this.id = id;
	}

	private String msg;
	private int id;

	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	public String getMsg() {
		return msg;
	}

	public void setMsg(String msg) {
		this.msg = msg;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}
}
