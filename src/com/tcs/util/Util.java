package com.tcs.util;

import java.util.ArrayList;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import com.tcs.model.DrawerItem;
import com.tcs.model.Employee;
import com.tcs.myilp.BadgeFragment;
import com.tcs.myilp.ContactFragment;
import com.tcs.myilp.NotificationFragment;
import com.tcs.myilp.R;
import com.tcs.myilp.ScheduleFragment;

public class Util {
	private static final String TAG = "Util";

	public static int saveEmployee(Context context, Employee emp) {
		int empError = emp.isValid();
		if (empError == Constants.EMP_ERRORS.NO_ERROR) {
			SharedPreferences spf = context.getSharedPreferences(
					Constants.PREF_FILE_NAME, Context.MODE_PRIVATE);
			spf.edit()
					.putLong(Constants.PREF_KEYS.EMP_ID, emp.getEmpId())
					.putString(Constants.PREF_KEYS.EMP_NAME, emp.getName())
					.putString(Constants.PREF_KEYS.EMP_EMAIL, emp.getEmail())
					.putString(Constants.PREF_KEYS.EMP_BATCH, emp.getBatch())
					.putString(Constants.PREF_KEYS.EMP_LG, emp.getLg())
					.putString(Constants.PREF_KEYS.EMP_LOCATION,
							emp.getLocation())
					.putBoolean(Constants.PREF_KEYS.IS_LOGIN, true).commit();

		}
		return empError;

	}

	public static Employee getEmployee(Context context) {
		SharedPreferences spf = context.getSharedPreferences(
				Constants.PREF_FILE_NAME, Context.MODE_PRIVATE);
		Employee emp = new Employee();

		emp.setEmpId(spf.getLong(Constants.PREF_KEYS.EMP_ID, -1));
		emp.setName(spf.getString(Constants.PREF_KEYS.EMP_NAME, null));
		emp.setBatch(spf.getString(Constants.PREF_KEYS.EMP_BATCH, null));
		emp.setEmail(spf.getString(Constants.PREF_KEYS.EMP_EMAIL, null));
		emp.setLg(spf.getString(Constants.PREF_KEYS.EMP_LG, null));
		emp.setLocation(spf.getString(Constants.PREF_KEYS.EMP_LOCATION, null));
		if (emp.isValid() == Constants.EMP_ERRORS.NO_ERROR)
			return emp;
		else
			return null;
	}

	public static String getErrorMsg(int errorCode) {
		String msg = "";
		switch (errorCode) {
		case Constants.EMP_ERRORS.NAME.BLANK:
			msg = "Name cannot be blank";
			break;
		case Constants.EMP_ERRORS.NAME.INVALID:
			msg = "Invalid name";
			break;
		case Constants.EMP_ERRORS.EMAIL.BLANK:
			msg = "Email cannot be blank";
			break;
		case Constants.EMP_ERRORS.EMAIL.INVALID:
			msg = "Invalid email";
			break;
		case Constants.EMP_ERRORS.BATCH.BLANK:
			msg = "Batch cannot be blank";
			break;
		case Constants.EMP_ERRORS.BATCH.INVALID:
			msg = "Invalid batch";
			break;
		case Constants.EMP_ERRORS.EMP_ID.BLANK:
			msg = "Employee ID cannot be blank";
			break;
		case Constants.EMP_ERRORS.EMP_ID.INVALID:
			msg = "Invalid employee Id";
			break;
		case Constants.EMP_ERRORS.LOCATION.BLANK:
			msg = "Location cannot be blank";
			break;
		case Constants.EMP_ERRORS.LOCATION.INVALID:
			msg = "Invalid Location";
			break;
		case Constants.EMP_ERRORS.EMP_LG.BLANK:
			msg = "LG Name cannot be blank";
			break;
		case Constants.EMP_ERRORS.EMP_LG.INVALID:
			msg = "Invalid LG name";
			break;
		}
		return msg;
	}

	public static boolean checkLogin(Context context) {
		SharedPreferences spf = context.getSharedPreferences(
				Constants.PREF_FILE_NAME, Context.MODE_PRIVATE);
		return spf.getBoolean(Constants.PREF_KEYS.IS_LOGIN, false);
	}

	public static ArrayList<DrawerItem> getDrawerItemList(Context context) {
		Log.d(TAG, " in Util");
		ArrayList<DrawerItem> data = new ArrayList<>();
		int[] ICONS = { R.drawable.ic_schedule, R.drawable.ic_notification,
				R.drawable.ic_badge, R.drawable.ic_contact };
		int[] ACTIVE_ICONS = { R.drawable.ic_schedule_active,
				R.drawable.ic_notification_active, R.drawable.ic_badge_active,
				R.drawable.ic_contact_active };
		int[] OPTIONS = { R.string.drawer_options_schedule,
				R.string.drawer_options_notifications,
				R.string.drawer_options_badges,
				R.string.drawer_options_contacts };
		String[] TAGS = { ScheduleFragment.TAG, NotificationFragment.TAG,
				BadgeFragment.TAG, ContactFragment.TAG };
		data.add(new DrawerItem(Constants.DRAWER_ITEM_TYPE.HEADER));
		for (int i = 0; i < 4; i++) {
			data.add(new DrawerItem(context.getString(OPTIONS[i]), ICONS[i],
					ACTIVE_ICONS[i], Constants.DRAWER_ITEM_TYPE.OPTION, TAGS[i]));
		}
		return data;
	}

	public static void hideKeyboard(Activity activity) {
		InputMethodManager imm = (InputMethodManager) activity
				.getSystemService(Context.INPUT_METHOD_SERVICE);
		imm.hideSoftInputFromWindow(
				activity.getCurrentFocus().getWindowToken(), 0);
	}

	public static boolean hasInternetAccess(Context applicationContext) {
		ConnectivityManager connectivityManager = (ConnectivityManager) applicationContext
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo activeNetworkInfo = connectivityManager
				.getActiveNetworkInfo();
		return activeNetworkInfo != null && activeNetworkInfo.isConnected();

	}

	public static void toast(Context context, String msg) {
		Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
	}

	private static ProgressDialog progressDialog;
	private static boolean WORK_IN_PROGRESS = false;

	public static ProgressDialog getProgressDialog(Activity activity) {
		progressDialog = new ProgressDialog(activity);
		progressDialog.setOwnerActivity(activity);
		progressDialog.setIndeterminate(true);
		progressDialog.setMessage("Please wait..");
		progressDialog.setCancelable(false);
		return progressDialog;
	}

	public static void showProgressDialog(Activity activity) {
		if (!WORK_IN_PROGRESS) {
			getProgressDialog(activity).show();
			WORK_IN_PROGRESS = !WORK_IN_PROGRESS;
		}
	}

	public static void hideProgressDialog(Activity activity) {
		if (WORK_IN_PROGRESS && progressDialog != null
				&& progressDialog.isShowing()) {
			progressDialog.dismiss();
			WORK_IN_PROGRESS = !WORK_IN_PROGRESS;
		}else{
			
		}
	}
}
