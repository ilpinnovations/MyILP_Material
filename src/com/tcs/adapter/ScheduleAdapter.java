package com.tcs.adapter;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.tcs.model.Slot;
import com.tcs.model.SlotViewHolder;
import com.tcs.myilp.R;

public class ScheduleAdapter extends ArrayAdapter<Slot> {
	private Context context;
	private ArrayList<Slot> data;
	private SlotViewHolder svh;

	public ArrayList<Slot> getData() {
		return data;
	}

	public ScheduleAdapter(Context context, ArrayList<Slot> objects) {
		super(context, R.layout.schedule_list_item, objects);
		this.context = context;
		if (objects == null)
			data = new ArrayList<Slot>();
		else
			data = objects;
	}

	public void setData(List<Slot> data) {
		if (data != null && data.size() > 0) {
			this.data.clear();
			this.data.addAll(data);
			notifyDataSetInvalidated();
			notifyDataSetChanged();
		}
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		if (convertView == null) {
			convertView = ((LayoutInflater) context
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE))
					.inflate(R.layout.schedule_list_item, null);
			svh = new SlotViewHolder();
			svh.setSlotTitle((TextView) convertView
					.findViewById(R.id.textViewSlotTitle));
			svh.setSlotContent((TextView) convertView
					.findViewById(R.id.textViewSlotContent));
			svh.setCourseTitle((TextView) convertView
					.findViewById(R.id.textViewCourseTitle));
			svh.setCourseContent((TextView) convertView
					.findViewById(R.id.textViewCourseContent));
			svh.setFacultyTitle((TextView) convertView
					.findViewById(R.id.textViewFacultyTitle));
			svh.setFacultyContent((TextView) convertView
					.findViewById(R.id.textViewFacultyContent));
			svh.setRoomTitle((TextView) convertView
					.findViewById(R.id.textViewRoomTitle));
			svh.setRoomContent((TextView) convertView
					.findViewById(R.id.textViewRoomContent));
			convertView.setTag(svh);
		}
		Slot s = data.get(position);
		svh = (SlotViewHolder) convertView.getTag();
		svh.getSlotContent().setText(s.getSlot());
		svh.getRoomContent().setText(s.getRoom());
		svh.getCourseContent().setText(s.getCourse());
		svh.getFacultyContent().setText(s.getFaculty());
		return convertView;
	}
}
