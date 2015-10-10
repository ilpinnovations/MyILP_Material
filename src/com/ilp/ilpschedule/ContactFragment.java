package com.ilp.ilpschedule;

import com.tcs.myilp.R;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class ContactFragment extends Fragment {
	public static final String TAG = "com.tcs.myilp.ContactFragment";

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		return super.onCreateView(inflater, container, savedInstanceState);
	}

	@Override
	public void onResume() {
		getActivity().setTitle(R.string.title_contacts);
		super.onResume();
	}
}
