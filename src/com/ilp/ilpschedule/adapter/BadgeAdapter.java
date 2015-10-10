package com.ilp.ilpschedule.adapter;

import android.content.Context;
import android.widget.ArrayAdapter;

import com.ilp.ilpschedule.model.Badge;
import com.tcs.myilp.R;

public class BadgeAdapter extends ArrayAdapter<Badge> {

	public BadgeAdapter(Context context,Badge[] objects) {
		super(context, R.layout.badge_list_item, objects);
	}

}
