package com.tcs.myilp;

import java.util.Calendar;

import android.app.DatePickerDialog;
import android.app.DatePickerDialog.OnDateSetListener;
import android.app.Dialog;
import android.app.DialogFragment;
import android.os.Bundle;

public class DatePickFragment extends DialogFragment {
	public static final String TAG = "com.tcs.myilp.DatePickerFragment";
	private OnDateSetListener listner;

	public DatePickFragment(OnDateSetListener listner) {
		this.listner = listner;
	}

	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		final Calendar c = Calendar.getInstance();
		int year = c.get(Calendar.YEAR);
		int month = c.get(Calendar.MONTH);
		int day = c.get(Calendar.DAY_OF_MONTH);
		return new DatePickerDialog(getActivity(), listner, year, month, day);
	}
}
