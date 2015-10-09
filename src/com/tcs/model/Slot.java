package com.tcs.model;

import android.os.Parcel;
import android.os.Parcelable;

public class Slot implements Parcelable {
	private String slot, course, faculty, room;

	public String getSlot() {
		return slot;
	}

	public void setSlot(String slot) {
		this.slot = slot;
	}

	public String getCourse() {
		return course;
	}

	public void setCourse(String course) {
		this.course = course;
	}

	public String getFaculty() {
		return faculty;
	}

	public void setFaculty(String faculty) {
		this.faculty = faculty;
	}

	public String getRoom() {
		return room;
	}

	public void setRoom(String room) {
		this.room = room;
	}

	@Override
	public String toString() {
		return "Slot [slot=" + slot + ", course=" + course + ", faculty="
				+ faculty + ", room=" + room + "]";
	}

	public Slot(String slot, String course, String faculty, String room) {
		super();
		this.slot = slot;
		this.course = course;
		this.faculty = faculty;
		this.room = room;
	}

	public Slot() {
		this.slot = "Not specified";
		this.course = "Not specified";
		this.faculty = "Not specified";
		this.room = "Not specified";
	}

	private Slot(Parcel in) {
		course = in.readString();
		slot = in.readString();
		faculty = in.readString();
		room = in.readString();
	}

	public static final Parcelable.Creator<Slot> CREATOR = new Parcelable.Creator<Slot>() {
		public Slot createFromParcel(Parcel in) {
			return new Slot(in);
		}

		public Slot[] newArray(int size) {
			return new Slot[size];
		}
	};

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeString(course);
		dest.writeString(slot);
		dest.writeString(faculty);
		dest.writeString(room);
	}

}
