package com.tcs.myilp;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.DatePickerDialog.OnDateSetListener;
import android.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.tcs.adapter.ScheduleAdapter;
import com.tcs.model.Slot;
import com.tcs.util.Constants;
import com.tcs.util.Util;

public class ScheduleFragment extends Fragment {
	private TextView textViewdate;
	private EditText editTextLgName;
	private Date date;
	private ListView listViewSchedule;
	private ScheduleAdapter scheduleAdapter;
	private ImageButton changeDate, getSchedule;
	public static final String TAG = "com.tcs.myilp.ScheduleFragment";
	private SimpleDateFormat dateFormat = new SimpleDateFormat(
			"E, dd MMM yyyy", Locale.US);

	RequestQueue reqQueue;
	private OnDateSetListener dateSetLitner = new OnDateSetListener() {
		@Override
		public void onDateSet(DatePicker view, int year, int monthOfYear,
				int dayOfMonth) {
			Calendar cal = Calendar.getInstance();
			cal.set(year, monthOfYear, dayOfMonth);
			date = cal.getTime();
			textViewdate.setText(dateFormat.format(date));
		}
	};
	private OnClickListener dateChangeClickListner = new OnClickListener() {
		@Override
		public void onClick(View v) {
			new DatePickFragment(dateSetLitner).show(getFragmentManager(),
					DatePickFragment.TAG);
		}
	};
	private Response.Listener<String> schedulerTaskSuccessListner = new Response.Listener<String>() {

		@Override
		public void onResponse(String response) {
			try {
				JSONObject jobj = new JSONObject(response);
				if (jobj.has("Android")) {
					JSONArray jarr = jobj.getJSONArray("Android");
					if (jarr.length() > 0) {
						JSONObject obj;
						ArrayList<Slot> data = new ArrayList<>();
						Slot s;
						for (int i = 0; i < jarr.length(); i++) {
							s = new Slot();
							obj = jarr.getJSONObject(i);
							if (obj.has("course"))
								s.setCourse(obj.getString("course"));
							if (obj.has("faculty"))
								s.setFaculty(obj.getString("faculty"));
							if (obj.has("room"))
								s.setRoom(obj.getString("room"));
							if (obj.has("slot"))
								s.setSlot(obj.getString("slot"));
							data.add(s);
							System.out.println(s);
						}
						((ScheduleAdapter) listViewSchedule.getAdapter())
								.setData(data);
					} else {
						// no schdule
						Util.toast(getActivity().getApplicationContext(),
								"No Schedule available");
					}
				}

			} catch (JSONException ex) {
				Log.d(TAG, ex.getLocalizedMessage());
			} finally {
				Util.hideProgressDialog(getActivity());
			}
			Log.d(TAG, response);
		}

	};
	private Response.ErrorListener schedulerTaskErrorListner = new Response.ErrorListener() {

		@Override
		public void onErrorResponse(VolleyError error) {
			Util.toast(getActivity().getApplicationContext(),
					"Error connecting server. Try again!");
			Log.d(TAG, "error" + error + error.getLocalizedMessage());
			Util.hideProgressDialog(getActivity());
		}

	};
	private OnClickListener getScheduleClickListner = new OnClickListener() {
		@Override
		public void onClick(View v) {
			Util.hideKeyboard(getActivity());
			Util.showProgressDialog(getActivity());
			if (Util.hasInternetAccess(getActivity().getApplicationContext())) {
				if (isDataValid()) {
					String url = new StringBuilder(Constants.NETWORK_PARAMS.SCHEDULE.URL)
							.append(Constants.NETWORK_PARAMS.SCHEDULE.BATCH)
							.append(Constants.EQUALS)
							.append(editTextLgName.getText().toString().trim()
									.toUpperCase(Locale.US))
							.append(Constants.AND)
							.append(Constants.NETWORK_PARAMS.SCHEDULE.DATE)
							.append(Constants.EQUALS)
							.append(Constants.paramsDateFormat.format(date))
							.toString();
					if (reqQueue == null)
						reqQueue = Volley.newRequestQueue(getActivity());
					StringRequest request = new StringRequest(url,
							schedulerTaskSuccessListner,
							schedulerTaskErrorListner);
					request.setTag(1);
					reqQueue.cancelAll(1);
					reqQueue.add(request);
				} else {
					Util.toast(getActivity().getApplicationContext(),
							getString(R.string.toast_blank_lg));
					Util.hideProgressDialog(getActivity());
				}
			} else {
				Util.toast(getActivity().getApplicationContext(),
						getString(R.string.toast_no_internet));
				Util.hideProgressDialog(getActivity());
			}
		}
	};

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.fragment_schedule, container,
				false);
		if (date == null)
			date = Calendar.getInstance().getTime();
		if (textViewdate == null) {
			textViewdate = (TextView) rootView
					.findViewById(R.id.textViewScheduleDate);
			textViewdate.setText(dateFormat.format(date));
		}
		if (editTextLgName == null) {
			editTextLgName = (EditText) rootView
					.findViewById(R.id.editTextLgName);
			editTextLgName.setText(Util.getEmployee(getActivity()).getLg());
		}
		if (changeDate == null) {
			changeDate = (ImageButton) rootView
					.findViewById(R.id.imageButtonChangeDate);
			changeDate.setOnClickListener(dateChangeClickListner);
		}
		if (getSchedule == null) {
			getSchedule = (ImageButton) rootView
					.findViewById(R.id.imageButtonGetSchedule);
			getSchedule.setOnClickListener(getScheduleClickListner);
		}
		if (scheduleAdapter == null) {
			scheduleAdapter = new ScheduleAdapter(getActivity(),
					new ArrayList<Slot>());
		}
		if (listViewSchedule == null) {
			listViewSchedule = (ListView) rootView
					.findViewById(R.id.listViewSchedule);
			listViewSchedule.setEmptyView(rootView
					.findViewById(R.id.textViewScheduleEmptyView));
			listViewSchedule.setAdapter(scheduleAdapter);
			Log.d(TAG, "list view set");
		}

		if (savedInstanceState != null) {
			ArrayList<Slot> data = savedInstanceState
					.getParcelableArrayList("schedules");
			scheduleAdapter.setData(data);
		}
		return rootView;
	}

	private boolean isDataValid() {
		Log.d(TAG, "cheking data");
		if (date != null
				&& editTextLgName.getText().toString().trim().length() > 0) {
			return true;
		} else {
			Util.toast(getActivity().getApplicationContext(),
					"LG Name should not be empty");

			return false;
		}
	}

	@Override
	public void onResume() {
		getActivity().setTitle(R.string.title_schedule);
		super.onResume();
	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		ArrayList<Slot> values = scheduleAdapter.getData();
		outState.putParcelableArrayList("schedules", values);

	}

}
