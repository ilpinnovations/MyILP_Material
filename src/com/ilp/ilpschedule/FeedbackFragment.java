package com.ilp.ilpschedule;

import java.util.HashMap;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RatingBar;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.ilp.ilpschedule.db.DbHelper;
import com.ilp.ilpschedule.model.Employee;
import com.ilp.ilpschedule.model.Feedback;
import com.ilp.ilpschedule.model.Slot;
import com.ilp.ilpschedule.util.Constants;
import com.ilp.ilpschedule.util.Util;

public class FeedbackFragment extends Fragment {
	public static final String TAG = "com.tcs.myilp.FeedbackFragment";

	private EditText editTextFaculty, editTextCourse, editTextComment;
	private ImageButton imageButtonFeedbackSend;
	private RatingBar ratingBarFeedback;
	private long slot_id;
	private Bundle bundle;
	private RequestQueue reqQueue;
	private OnClickListener feedbackSubmitClickListner = new OnClickListener() {
		@Override
		public void onClick(View v) {
			// send feedback to server
			Util.showProgressDialog(getActivity());
			if (isDataValid()) {
				Feedback feedback = new Feedback();
				feedback.setComment(editTextComment.getText().toString().trim());
				feedback.setRating(ratingBarFeedback.getRating());
				feedback.setSlot_id(slot_id);
				DbHelper dbh = new DbHelper(getActivity());
				long id = dbh.addFeedback(feedback);
				if (id != -1) {
					Log.d(TAG, "feedback saved");
					if (Util.hasInternetAccess(getActivity())) {
						final Feedback finalfeedback = dbh.getFeedback(id);
						StringRequest request = new StringRequest(
								Request.Method.POST, Constants.URL_FEEDBACK,
								feedbackTaskSuccessListner,
								feedbackTaskErrorListner) {
							@Override
							protected java.util.Map<String, String> getParams()
									throws com.android.volley.AuthFailureError {
								Map<String, String> params = new HashMap<String, String>();
								params.put(
										Constants.NETWORK_PARAMS.FEEDBACK.COMMENT,
										editTextComment.getText().toString()
												.trim());
								params.put(
										Constants.NETWORK_PARAMS.FEEDBACK.RATE,
										String.valueOf(ratingBarFeedback
												.getRating()));
								DbHelper dbh = new DbHelper(getActivity());
								Slot s = dbh
										.getSlot(finalfeedback.getSlot_id());
								params.put(
										Constants.NETWORK_PARAMS.FEEDBACK.FACULTY,
										s.getFaculty());
								params.put(
										Constants.NETWORK_PARAMS.FEEDBACK.COURSE,
										s.getCourse());
								params.put(
										Constants.NETWORK_PARAMS.FEEDBACK.SLOT,
										s.getSlot());
								params.put(
										Constants.NETWORK_PARAMS.FEEDBACK.DATE,
										Constants.paramsDateFormat.format(s
												.getDate()));

								Employee employee = Util
										.getEmployee(getActivity());
								params.put(
										Constants.NETWORK_PARAMS.FEEDBACK.EMP_ID,
										String.valueOf(employee.getEmpId()));
								params.put(
										Constants.NETWORK_PARAMS.FEEDBACK.EMP_NAME,
										employee.getName());
								params.put(
										Constants.NETWORK_PARAMS.FEEDBACK.EMP_LOC,
										employee.getLocation());
								params.put(
										Constants.NETWORK_PARAMS.FEEDBACK.EMP_BATCH,
										employee.getLg());
								return params;
							};
						};
						getRequestQueue().add(request);
					} else {
						Toast.makeText(getActivity(),
								getString(R.string.toast_no_internet),
								Toast.LENGTH_SHORT).show();
						Util.hideProgressDialog(getActivity());
					}
				} else {
					Toast.makeText(getActivity(), "Invalid feedback!!",
							Toast.LENGTH_SHORT).show();
					Util.hideProgressDialog(getActivity());
				}
			} else {
				Toast.makeText(getActivity(), "Comment cannot be empty",
						Toast.LENGTH_SHORT).show();
				Util.hideProgressDialog(getActivity());
			}
		}
	};
	private Response.Listener<String> feedbackTaskSuccessListner = new Response.Listener<String>() {
		@Override
		public void onResponse(String response) {
			try {
				JSONObject jobj = new JSONObject(response);
				if (jobj.has("Android")) {
					JSONArray jarr = jobj.getJSONArray("Android");
					if (jarr.length() > 0) {
						jobj = jarr.getJSONObject(0);
						if (jobj.has("feed_result")) {
							String res = jobj.getString("feed_result");
							if (res.equalsIgnoreCase("success")) {
								res = getString(R.string.toast_feedback_success);
							} else if (res.equalsIgnoreCase("already")) {
								res = getString(R.string.toast_feedback_already);
							} else {
								res = getString(R.string.toast_feedback_fail);
							}
							Toast.makeText(getActivity(), res,
									Toast.LENGTH_SHORT).show();
						}
					}
				}
				Log.d(TAG, "got the response of submittion ->" + response);
			} catch (JSONException ex) {
				Log.d(TAG, "error parsing result" + ex.getLocalizedMessage());
			} finally {
				Util.hideProgressDialog(getActivity());
			}
			Log.d(TAG, response);
		}
	};
	private Response.ErrorListener feedbackTaskErrorListner = new Response.ErrorListener() {

		@Override
		public void onErrorResponse(VolleyError error) {
			Util.toast(getActivity().getApplicationContext(),
					"Error connecting server. Try again!");
			Log.d(TAG, "error" + error + error.getLocalizedMessage());
			Util.hideProgressDialog(getActivity());
		}

	};

	private RequestQueue getRequestQueue() {
		if (reqQueue == null)
			reqQueue = Volley.newRequestQueue(getActivity());
		return reqQueue;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.fragment_feedback, container,
				false);

		editTextCourse = (EditText) rootView
				.findViewById(R.id.editTextFeedbackCourse);

		editTextFaculty = (EditText) rootView
				.findViewById(R.id.editTextFeedbackFaculty);

		imageButtonFeedbackSend = (ImageButton) rootView
				.findViewById(R.id.imageButtonFeedbackSend);

		imageButtonFeedbackSend.setOnClickListener(feedbackSubmitClickListner);

		ratingBarFeedback = (RatingBar) rootView
				.findViewById(R.id.ratingBarFeedback);

		editTextComment = (EditText) rootView
				.findViewById(R.id.editTextFeedbackComment);

		return rootView;
	}

	@Override
	public void onStart() {
		editTextFaculty.setText((String) bundle
				.get(Constants.BUNDLE_KEYS.FEEDBACK_FRAGMENT.FACULTY));
		editTextCourse.setText((String) bundle
				.get(Constants.BUNDLE_KEYS.FEEDBACK_FRAGMENT.COURSE));
		slot_id = (Long) bundle
				.get(Constants.BUNDLE_KEYS.FEEDBACK_FRAGMENT.SLOT_ID);
		DbHelper dbh = new DbHelper(getActivity());
		Feedback feedback = dbh.getFeedbackBySlotId(slot_id);
		editTextComment.setText("");
		ratingBarFeedback.setRating(1.0f);
		if (feedback != null) {
			if (feedback.getComment() != null)
				editTextComment.setText(feedback.getComment());
			if (feedback.getRating() > 0) {
				ratingBarFeedback.setRating(feedback.getRating());
			}
		}
		super.onStart();
	}

	@Override
	public void onResume() {
		getActivity().setTitle(R.string.title_feedback);
		super.onResume();
	}

	private boolean isDataValid() {
		return Util.checkString(editTextComment.getText().toString());
	}

	public void setData(Bundle bundle) {
		this.bundle = bundle;
	}
}
