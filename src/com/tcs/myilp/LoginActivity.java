package com.tcs.myilp;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;

import com.tcs.model.Employee;
import com.tcs.util.Constants;
import com.tcs.util.Util;

public class LoginActivity extends ActionBarActivity {
	private Toolbar toolbar;
	private Spinner locationSpinner;
	private EditText editTextName, editTextEmail, editTextEmpId, editTextBatch,
			editTextLg;

	/**
	 * Simple {@link ArrayAdapter} of type {@link String}
	 */
	private ArrayAdapter<String> locationAdapter;

	public LoginActivity() {

	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_login);
		if (savedInstanceState == null) {
			// getFragmentManager().beginTransaction()
			// .add(R.id.container, new MainFragment()).commit();
		}
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
		Employee emp = new Employee();
		String tmp = editTextEmpId.getText().toString();
		long empId = -1;
		if (tmp != null && tmp.trim().length() > 0) {
			empId = Long.parseLong(tmp);
			emp.setEmpId(empId);
		}
		emp.setName(editTextName.getText().toString());
		emp.setBatch(editTextBatch.getText().toString());
		emp.setLg(editTextLg.getText().toString());
		emp.setEmail(editTextEmail.getText().toString());
		emp.setLocation((String) locationSpinner.getSelectedItem());

		int saveError = Util.saveEmployee(getApplicationContext(), emp);
		if (saveError == Constants.EMP_ERRORS.NO_ERROR) {
			startMainActivity();
		} else {
			Util.toast(getApplicationContext(), Util.getErrorMsg(saveError));
		}
	}
}
