package com.ilp.ilpschedule;

import com.tcs.myilp.R;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class LocationFragment extends Fragment {
	public static final String TAG = "com.ilp.ilpschedule.LocationFragment";

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		return inflater.inflate(R.layout.fragment_location, container, false);
	}

	@Override
	public void onResume() {
		getActivity().setTitle(
				R.string.title_location);
		super.onResume();
	}
}
