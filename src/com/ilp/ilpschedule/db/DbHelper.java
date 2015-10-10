package com.ilp.ilpschedule.db;

import java.sql.Date;
import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.ilp.ilpschedule.model.Contact;
import com.ilp.ilpschedule.model.Feedback;
import com.ilp.ilpschedule.model.Location;
import com.ilp.ilpschedule.model.Slot;

public class DbHelper extends SQLiteOpenHelper {
	private static int DB_VERSION = 1;
	private static String DB_NAME = "myilpschedule.db";

	public DbHelper(Context context) {
		super(context, DB_NAME, null, DB_VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL(DbStructure.ContactTable.COMMAND_CREATE);
		db.execSQL(DbStructure.LocationTable.COMMAND_CREATE);
		db.execSQL(DbStructure.NotificationTable.COMMAND_CREATE);
		db.execSQL(DbStructure.ScheduleTable.COMMAND_CREATE);
		db.execSQL(DbStructure.FeedbackTable.COMMAND_CREATE);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		if (oldVersion < newVersion) {
			db.execSQL(DbStructure.ContactTable.COMMAND_DROP);
			db.execSQL(DbStructure.LocationTable.COMMAND_DROP);
			db.execSQL(DbStructure.NotificationTable.COMMAND_DROP);
			db.execSQL(DbStructure.ScheduleTable.COMMAND_DROP);
			db.execSQL(DbStructure.FeedbackTable.COMMAND_DROP);
			onCreate(db);
		}
	}

	public long addContact(Contact contact) {
		long id;
		ContentValues values = new ContentValues();
		values.put(DbStructure.ContactTable.COLUMN_TITLE, contact.getTitle());
		values.put(DbStructure.ContactTable.COLUMN_NUMBER, contact.getNumber());
		id = this.getWritableDatabase().insert(
				DbStructure.ContactTable.TABLE_NAME, null, values);
		return id;
	}

	public List<Contact> getContacts() {
		ArrayList<Contact> contacts = new ArrayList<>();
		Cursor cursor = this.getReadableDatabase().query(
				DbStructure.ContactTable.TABLE_NAME, null, null, null, null,
				null, null);
		Contact contact;
		if (cursor.moveToFirst()) {
			while (!cursor.isAfterLast()) {
				contact = new Contact();
				contact.setId(cursor.getLong(cursor
						.getColumnIndexOrThrow(DbStructure.ContactTable._ID)));
				contact.setNumber(cursor.getString(cursor
						.getColumnIndexOrThrow(DbStructure.ContactTable.COLUMN_NUMBER)));
				contact.setTitle(cursor.getString(cursor
						.getColumnIndexOrThrow(DbStructure.ContactTable.COLUMN_NUMBER)));
				contacts.add(contact);
			}
		}
		return contacts;
	}

	public int addLocations(List<Location> locations) {
		int added = 0;
		for (Location location : locations) {
			if (location != null && location.isValid()
					&& addLocation(location) != -1)
				added++;
		}
		return added;
	}

	public long addLocation(Location location) {
		long id;
		ContentValues values = new ContentValues();
		values.put(DbStructure.LocationTable.COLUMN_BATCH, location.getBatch());
		values.put(DbStructure.LocationTable.COLUMN_LOCATION,
				location.getLocation());
		values.put(DbStructure.LocationTable.COLUMN_NAME, location.getName());
		id = this.getWritableDatabase().insert(
				DbStructure.LocationTable.TABLE_NAME, null, values);
		return id;
	}

	public List<Location> getLocations() {
		ArrayList<Location> locations = new ArrayList<>();
		Cursor cursor = this.getReadableDatabase().query(
				DbStructure.LocationTable.TABLE_NAME, null, null, null, null,
				null, null);
		Location location;
		if (cursor.moveToFirst()) {
			while (!cursor.isAfterLast()) {
				location = new Location();
				location.setId(cursor.getLong(cursor
						.getColumnIndexOrThrow(DbStructure.LocationTable._ID)));
				location.setName(cursor.getString(cursor
						.getColumnIndexOrThrow(DbStructure.LocationTable.COLUMN_NAME)));
				location.setBatch(cursor.getString(cursor
						.getColumnIndexOrThrow(DbStructure.LocationTable.COLUMN_BATCH)));
				location.setLocation(cursor.getString(cursor
						.getColumnIndexOrThrow(DbStructure.LocationTable.COLUMN_LOCATION)));
				locations.add(location);
			}
		}
		return locations;
	}

	public int addSlots(List<Slot> slots) {
		int added = 0;
		for (Slot slot : slots) {
			if (slot != null && slot.isValid() && addSlot(slot) != -1)
				added++;
		}
		return added;
	}

	public long addSlot(Slot slot) {
		long id = -1;
		ContentValues values = new ContentValues();
		values.put(DbStructure.ScheduleTable.COLUMN_BATCH, slot.getBatch());
		values.put(DbStructure.ScheduleTable.COLUMN_DATE, slot.getDate()
				.getTime());
		values.put(DbStructure.ScheduleTable.COLUMN_SLOT, slot.getSlot());
		values.put(DbStructure.ScheduleTable.COLUMN_COURSE, slot.getCourse());
		values.put(DbStructure.ScheduleTable.COLUMN_FACULTY, slot.getFaculty());
		values.put(DbStructure.ScheduleTable.COLUMN_ROOM, slot.getRoom());

		id = this.getWritableDatabase().insert(
				DbStructure.ScheduleTable.TABLE_NAME, null, values);
		return id;
	}

	public List<Slot> getSlot(Date date, String batch) {
		ArrayList<Slot> slots = new ArrayList<>();
		Cursor cursor = this.getReadableDatabase().query(
				DbStructure.ScheduleTable.TABLE_NAME,
				null,
				new StringBuilder(DbStructure.ScheduleTable.COLUMN_DATE)
						.append(DbConstants.EQUALS)
						.append(DbConstants.QUESTION_MARK)
						.append(DbConstants.AND)
						.append(DbStructure.ScheduleTable.COLUMN_BATCH)
						.append(DbConstants.EQUALS)
						.append(DbConstants.QUESTION_MARK).toString(),
				new String[] { String.valueOf(date.getTime()), batch }, null,
				null, null);
		Slot slot;
		if (cursor.moveToFirst()) {
			while (!cursor.isAfterLast()) {
				slot = new Slot();
				slot.setId(cursor.getLong(cursor
						.getColumnIndexOrThrow(DbStructure.ScheduleTable._ID)));
				slot.setBatch(cursor.getString(cursor
						.getColumnIndexOrThrow(DbStructure.ScheduleTable.COLUMN_BATCH)));
				slot.setCourse(cursor.getString(cursor
						.getColumnIndexOrThrow(DbStructure.ScheduleTable.COLUMN_COURSE)));
				slot.setDate(new Date(
						cursor.getLong(cursor
								.getColumnIndexOrThrow(DbStructure.ScheduleTable.COLUMN_DATE))));
				slot.setFaculty(cursor.getString(cursor
						.getColumnIndexOrThrow(DbStructure.ScheduleTable.COLUMN_FACULTY)));
				slot.setSlot(cursor.getString(cursor
						.getColumnIndexOrThrow(DbStructure.ScheduleTable.COLUMN_SLOT)));
				slot.setRoom(cursor.getString(cursor
						.getColumnIndexOrThrow(DbStructure.ScheduleTable.COLUMN_ROOM)));
				slots.add(slot);
			}
		}
		return slots;
	}

	public long addFeedback(Feedback feedback) {
		long id = -1;
		ContentValues values = new ContentValues();
		values.put(DbStructure.FeedbackTable.COLUMN_COMMENT,
				feedback.getComment());
		values.put(DbStructure.FeedbackTable.COLUMN_RATING,
				feedback.getRating());
		values.put(DbStructure.FeedbackTable.COLUMN_SLOT_REF,
				feedback.getSlot_id());

		id = this.getWritableDatabase().insert(
				DbStructure.FeedbackTable.TABLE_NAME, null, values);
		return id;
	}

	public int deleteFeedback(int id) {
		return this.getWritableDatabase().delete(
				DbStructure.FeedbackTable.TABLE_NAME,
				new StringBuilder(DbStructure.FeedbackTable._ID)
						.append(DbConstants.EQUALS)
						.append(DbConstants.QUESTION_MARK).toString(),
				new String[] { String.valueOf(id) });
	}
}
