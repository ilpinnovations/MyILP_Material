package com.tcs.myilp;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.tcs.adapter.NotificationAdapter;
import com.tcs.model.Notification;
import com.tcs.util.Constants;
import com.tcs.util.Util;

public class NotificationFragment extends Fragment {
	public static final String TAG = "com.tcs.myilp.NotificationFragment";
	private ListView notificationList;
	private NotificationAdapter notificationAdapter;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.fragment_notification,
				container, false);

		if (notificationAdapter == null)
			notificationAdapter = new NotificationAdapter(getActivity()
					.getApplicationContext(), new ArrayList<Notification>());

		if (notificationList == null) {
			notificationList = (ListView) rootView
					.findViewById(R.id.listViewNotification);
			notificationList.setAdapter(notificationAdapter);
		}
		return rootView;
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
						n = new Notification(Notification.inputDateFormat.parse(obj
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
		Util.showProgressDialog(getActivity());
		if (requestQueue == null)
			requestQueue = Volley.newRequestQueue(getActivity());
		StringRequest request = new StringRequest(Constants.URL_NOTIFICATION,
				requestSuccessListner, requestErrorListner);
		requestQueue.add(request);
	}
}
