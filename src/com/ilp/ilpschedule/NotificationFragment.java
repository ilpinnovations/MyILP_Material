package com.ilp.ilpschedule;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONObject;

import android.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.ilp.ilpschedule.adapter.NotificationAdapter;
import com.ilp.ilpschedule.model.Notification;
import com.ilp.ilpschedule.util.Constants;
import com.ilp.ilpschedule.util.Util;
import com.tcs.myilp.R;

public class NotificationFragment extends Fragment {
	public static final String TAG = "com.tcs.myilp.NotificationFragment";
	private ListView notificationList;
	private NotificationAdapter notificationAdapter;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.fragment_notification,
				container, false);

		notificationAdapter = new NotificationAdapter(getActivity()
				.getApplicationContext(), new ArrayList<Notification>());

		notificationList = (ListView) rootView
				.findViewById(R.id.listViewNotification);
		notificationList.setAdapter(notificationAdapter);
		notificationList.setEmptyView((TextView) rootView
				.findViewById(R.id.textViewNotificationEmptyView));
		setHasOptionsMenu(true);
		return rootView;
	}

	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		inflater.inflate(R.menu.notification_menu, menu);
		super.onCreateOptionsMenu(menu, inflater);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		int id = item.getItemId();
		if (id == R.id.action_refresh) {
			fetchNotifications();
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	public void onResume() {
		getActivity().setTitle(R.string.title_notification);
		super.onResume();
	}

	@Override
	public void onStart() {
		fetchNotifications();
		super.onStart();
	}

	private RequestQueue requestQueue;
	private Response.Listener<String> requestSuccessListner = new Response.Listener<String>() {

		@Override
		public void onResponse(String result) {
			Log.d(TAG, result);
			ArrayList<Notification> nots = new ArrayList<>();
			try {
				JSONObject obj = new JSONObject(result);
				if (obj.has("Android")) {
					JSONArray arr = obj.getJSONArray("Android");
					Notification n;
					for (int i = 0; i < arr.length(); i++) {
						obj = arr.getJSONObject(i);
						n = new Notification(
								Notification.inputDateFormat.parse(obj
										.getString("msg_date")),
								obj.getString("message"), obj.getInt("s_no"));
						nots.add(n);
						Log.d(TAG, n.toString());
					}
				}
			} catch (Exception ex) {
				Log.d(TAG, ex.getLocalizedMessage());
			} finally {
				Util.hideProgressDialog(getActivity());
			}
			notificationAdapter.setData(nots);
		}
	};
	private Response.ErrorListener requestErrorListner = new Response.ErrorListener() {

		@Override
		public void onErrorResponse(VolleyError result) {
			Log.d(TAG, "error " + result.getLocalizedMessage());
			Util.hideProgressDialog(getActivity());
		}

	};

	private void fetchNotifications() {
		if (Util.hasInternetAccess(getActivity())) {
			Util.showProgressDialog(getActivity());
			if (requestQueue == null)
				requestQueue = Volley.newRequestQueue(getActivity());
			StringRequest request = new StringRequest(
					Constants.URL_NOTIFICATION, requestSuccessListner,
					requestErrorListner);
			requestQueue.add(request);
		} else {
			Toast.makeText(getActivity(),
					getString(R.string.toast_no_internet), Toast.LENGTH_SHORT)
					.show();
		}
	}
}
