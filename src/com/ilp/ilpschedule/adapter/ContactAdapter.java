package com.ilp.ilpschedule.adapter;

import com.ilp.ilpschedule.model.Contact;
import com.tcs.myilp.R;

import android.content.Context;
import android.widget.ArrayAdapter;

public class ContactAdapter extends ArrayAdapter<Contact> {

	public ContactAdapter(Context context, Contact[] objects) {
		super(context, R.layout.contact_list_item, objects);
	}

}
