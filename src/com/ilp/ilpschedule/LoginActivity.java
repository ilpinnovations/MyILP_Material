package com.ilp.ilpschedule;

import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;

import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.ilp.ilpschedule.model.Employee;
import com.ilp.ilpschedule.util.Constants;
import com.ilp.ilpschedule.util.Util;

public class LoginActivity extends ActionBarActivity {
	public static final String TAG = "com.ilp.schedule.LoginActivity";
	private Toolbar toolbar;
	private Spinner locationSpinner;
	private EditText editTextName, editTextEmail, editTextEmpId, editTextBatch,
			editTextLg;
	private GoogleCloudMessaging gcm;
	/**
	 * Simple {@link ArrayAdapter} of type {@link String}
	 */
	private ArrayAdapter<String> locationAdapter;

	public LoginActivity() {

	}

	private GoogleCloudMessaging getGcm() {
		if (gcm == null)
			gcm = GoogleCloudMessaging.getInstance(getApplicationContext());
		return gcm;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_login);
		locationAdapter = new ArrayAdapter<String>(getApplicationContext(),
				R.layout.location_item, getResources().getStringArray(
						R.array.locations));
		toolbar = (Toolbar) findViewById(R.id.toolbar);
		editTextName = (EditText) findViewById(R.id.editTextLoginName);
		editTextEmail = (EditText) findViewById(R.id.editTextLoginEmail);
		editTextEmpId = (EditText) findViewById(R.id.editTextLoginEmployeeId);
		editTextBatch = (EditText) findViewById(R.id.editTextLoginBatch);
		editTextLg = (EditText) findViewById(R.id.editTextLoginLg);
		setSupportActionBar(toolbar);
		locationSpinner = ((Spinner) findViewById(R.id.spinnerLoginLocation));
		locationSpinner.setAdapter(locationAdapter);
	}

	@Override
	protected void onStart() {
		super.onStart();
		if (Util.checkLogin(getApplicationContext())) {
			// redirect to main activity
			startMainActivity();
		}

	}

	private void startMainActivity() {
		startActivity(new Intent(getApplicationContext(), MainActivity.class));
		finish();
	}

	public void login(View view) {
		if (Util.hasInternetAccess(getApplicationContext())) {
			Util.showProgressDialog(this);
			Employee emp = new Employee();
			String tmp = editTextEmpId.getText().toString();
			long empId = -1;
			if (tmp != null && tmp.trim().length() > 0) {
				empId = Long.parseLong(tmp);
				emp.setEmpId(empId);
			}
			emp.setName(editTextName.getText().toString()
					.toUpperCase(Locale.US));
			emp.setBatch(editTextBatch.getText().toString()
					.toUpperCase(Locale.US));
			emp.setLg(editTextLg.getText().toString().toUpperCase(Locale.US));
			emp.setEmail(editTextEmail.getText().toString());
			emp.setLocation((String) locationSpinner.getSelectedItem());

			int saveError = Util.saveEmployee(getApplicationContext(), emp);
			if (saveError == Constants.EMP_ERRORS.NO_ERROR) {
				TelephonyManager tManager = (TelephonyManager) getBaseContext()
						.getSystemService(Context.TELEPHONY_SERVICE);
				String imei = tManager.getDeviceId();
				Map<String, String> params = new HashMap<>();
				params.put("name", emp.getName());
				params.put("email", emp.getEmail());
				params.put("id", String.valueOf(emp.getEmpId()));
				params.put("loc", emp.getLocation());
				params.put("batch", emp.getLg());
				params.put("imei", imei);
				RegisterAsyncTask rat = new RegisterAsyncTask(params);
				rat.execute();
			} else {
				Util.toast(getApplicationContext(), Util.getErrorMsg(saveError));
			}

		} else {
			Util.toast(getApplicationContext(),
					getString(R.string.toast_no_internet));
		}
	}

	private class RegisterAsyncTask extends AsyncTask<Void, Boolean, Boolean> {
		private Map<String, String> parameters;
		private String regId;

		public RegisterAsyncTask(Map<String, String> parameters) {
			this.parameters = parameters;
		}

		private void post(String endpoint) throws IOException {
			URL url;
			try {
				url = new URL(endpoint);
			} catch (MalformedURLException e) {
				throw new IllegalArgumentException("invalid url: " + endpoint);
			}
			String body = Util.getUrlEncodedString(parameters);
			Log.d(TAG, "Posting '" + body + "' to " + url);

			byte[] bytes = body.getBytes();

			HttpURLConnection conn = null;
			try {
				Log.d("URL", "> " + url);
				conn = (HttpURLConnection) url.openConnection();
				conn.setDoOutput(true);
				conn.setUseCaches(false);
				conn.setFixedLengthStreamingMode(bytes.length);
				conn.setRequestMethod("POST");
				conn.setRequestProperty("Content-Type",
						"application/x-www-form-urlencoded;charset=UTF-8");
				// post the request
				OutputStream out = conn.getOutputStream();
				out.write(bytes);
				out.close();
				// handle the response
				int status = conn.getResponseCode();
				// If response is not success
				if (status != 200) {
					throw new IOException("Post failed with error code "
							+ status);
				}
			} finally {
				if (conn != null) {
					conn.disconnect();
				}
			}
		}

		@Override
		protected Boolean doInBackground(Void... params) {
			boolean res = false;
			int MAX_ATTEMPTS = 4;
			int BACKOFF_MILLI_SECONDS = 1000;
			try {
				regId = getGcm().register(Constants.GOOGLE_SENDER_ID);
				if (regId != null && regId.trim().length() > 0) {
					publishProgress(true);
					// register with app server ilp server
					parameters.put("regId", regId);
					long backoff = BACKOFF_MILLI_SECONDS + (int) Math.random()
							* 1000;
					for (int i = 1; i <= MAX_ATTEMPTS; i++) {
						Log.d(TAG, "Attempt #" + i + " to register");
						try {
							post(Constants.URL_REGISTER);
							res = true;
							Log.d(TAG, "success to register on attempt " + i);
							break;
						} catch (IOException e) {
							Log.d(TAG, "Failed to register on attempt " + i
									+ ":" + e);
							if (i == MAX_ATTEMPTS) {
								break;
							}
							try {
								Log.d(TAG, "Sleeping for " + backoff
										+ " ms before retry");
								Thread.sleep(backoff);
							} catch (InterruptedException e1) {
								// Activity finished before we complete - exit.
								Log.d(TAG,
										"Thread interrupted: abort remaining retries!");
								Thread.currentThread().interrupt();
								return res;
							}
							backoff *= 2;
						}
					}
				}
			} catch (IOException e) {
				Log.d(TAG,
						"fail to register with gcm" + e.getLocalizedMessage());
				res = false;
			}
			return res;
		}

		@Override
		protected void onProgressUpdate(Boolean... values) {
			if (values[0]) {
				Util.saveGcmRegistrationId(getApplicationContext(), regId);
				Log.d(TAG, "registeration with google done" + regId);
				Util.doLogin(getApplicationContext());
			} else {
				Log.d(TAG, "fail to register with google try again");
				Util.toast(getApplicationContext(),
						getString(R.string.toast_reg_error));
			}
		}

		@Override
		protected void onPostExecute(Boolean result) {
			if (result) {
				startMainActivity();
			} else {
				Util.toast(getApplicationContext(),
						getString(R.string.toast_reg_error));
			}
			Util.hideProgressDialog(getParent());
		}
	}
}
