package com.ilp.ilpschedule;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Criteria;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;

import com.android.volley.RequestQueue;
import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.ilp.ilpschedule.model.Employee;
import com.ilp.ilpschedule.model.ILPLocation;
import com.ilp.ilpschedule.util.Constants;
import com.ilp.ilpschedule.util.Util;

public class LocationActivity extends ActionBarActivity {

	public static final String TAG = "com.ilp.ilpschedule.LocationActivity";

	private GoogleMap map;
	private LocationManager locationManager;
	private ImageButton imageButtonIlp, imageButtonMyLocation,
			imageButtonHostel, imageButtonSearch;
	private ProgressBar progressBarSearchLocation;
	private EditText editTextSearch;
	private double latitude = 0;
	private double longitude = 0;
	private RequestQueue reqQue;
	private int PROXIMITY_RADIUS = 5000;
	private Listener<String> searchPlacesSuccessListner = new Listener<String>() {
		public void onResponse(String result) {
			if (Util.checkString(result)) {
				Log.d(TAG, result);
				PlacesDisplayTask placesDisplayTask = new PlacesDisplayTask();
				Object[] toPass = new Object[4];
				toPass[0] = getMap();
				toPass[1] = result;
				toPass[2] = R.drawable.ic_marker;
				toPass[3] = PROXIMITY_RADIUS;
				placesDisplayTask.execute(toPass);
			}
			hideProgressLocationSearch();
		};
	};
	private ErrorListener searchPlacesErrorListner = new ErrorListener() {

		@Override
		public void onErrorResponse(VolleyError error) {
			// TODO Auto-generated method stub
			Log.d(TAG, "error" + error);
			Util.toast(getApplicationContext(),
					getString(R.string.toast_loc_search_error));
			hideProgressLocationSearch();
		};
	};
	private OnClickListener imageButtonIlpClickListner = new OnClickListener() {
		@Override
		public void onClick(View v) {

			getMap().clear();
			Employee emp = Util.getEmployee(getApplicationContext());
			List<ILPLocation> ilp_locs = Util.getLocations(emp.getLocation(),
					Constants.LOCATIONS.TYPE.ILP);
			if (ilp_locs.size() > 0) {
				Util.toast(getApplicationContext(), "Locating you ILPs");
				for (ILPLocation loc : ilp_locs) {
					LatLng latLon = new LatLng(loc.getLat(), loc.getLon());
					getMap().moveCamera(CameraUpdateFactory.newLatLng(latLon));
					getMap().animateCamera(CameraUpdateFactory.zoomTo(15));
					getMap().addMarker(
							new MarkerOptions().position(latLon).title(
									loc.getName()));

				}
			} else {
				Util.toast(getApplicationContext(), "No ILPs found");
			}
		}
	};
	private OnClickListener imageButtonHostelClickListner = new OnClickListener() {

		@Override
		public void onClick(View v) {

			getMap().clear();
			Employee emp = Util.getEmployee(getApplicationContext());
			List<ILPLocation> hotel_locs = Util.getLocations(emp.getLocation(),
					Constants.LOCATIONS.TYPE.HOSTEL);
			if (hotel_locs.size() > 0) {
				Util.toast(getApplicationContext(), "Locating Hostels");
				for (ILPLocation loc : hotel_locs) {
					LatLng latLon = new LatLng(loc.getLat(), loc.getLon());
					getMap().moveCamera(CameraUpdateFactory.newLatLng(latLon));
					getMap().animateCamera(CameraUpdateFactory.zoomTo(15));
					getMap().addMarker(
							new MarkerOptions().position(latLon).title(
									loc.getName()));

				}
			} else {
				Util.toast(getApplicationContext(), "No hostels found");
			}
		}
	};
	private OnClickListener imageButtonMyLocationClickListner = new OnClickListener() {

		@Override
		public void onClick(View v) {
			Util.toast(getApplicationContext(), "Locating you");
			getMap().clear();
			getMyLocation();
		}
	};
	private LocationListener locationListner = new LocationListener() {

		@Override
		public void onLocationChanged(android.location.Location location) {
			Util.toast(getApplicationContext(), "locating to location");
			latitude = location.getLatitude();
			longitude = location.getLongitude();
			LatLng latLng = new LatLng(latitude, longitude);
			getMap().moveCamera(CameraUpdateFactory.newLatLng(latLng));
			getMap().animateCamera(CameraUpdateFactory.zoomTo(18));
		}

		@Override
		public void onStatusChanged(String provider, int status, Bundle extras) {
			// TODO Auto-generated method stub

		}

		@Override
		public void onProviderEnabled(String provider) {
			// TODO Auto-generated method stub

		}

		@Override
		public void onProviderDisabled(String provider) {
			// TODO Auto-generated method stub

		}
	};

	private void showProgressLocationSearch() {
		progressBarSearchLocation.setVisibility(View.VISIBLE);
		imageButtonSearch.setVisibility(View.GONE);
	}

	private void hideProgressLocationSearch() {
		progressBarSearchLocation.setVisibility(View.GONE);
		imageButtonSearch.setVisibility(View.VISIBLE);
	}

	private OnClickListener searchButtonClickListner = new OnClickListener() {

		@Override
		public void onClick(View v) {
			if (Util.hasInternetAccess(getApplicationContext())) {
				showProgressLocationSearch();
				String type = editTextSearch.getText().toString();
				Map<String, String> params = new HashMap<>();
				params.put(Constants.NETWORK_PARAMS.MAP.LOCATION, latitude
						+ "," + longitude);
				params.put(Constants.NETWORK_PARAMS.MAP.RADIUS,
						String.valueOf(PROXIMITY_RADIUS));
				params.put(Constants.NETWORK_PARAMS.MAP.TYPES, type);
				params.put(Constants.NETWORK_PARAMS.MAP.SENSOR,
						String.valueOf(true));
				params.put(Constants.NETWORK_PARAMS.MAP.KEY,
						Constants.GOOGLE_MAP_API_KEY);
				StringBuilder googlePlacesUrl = new StringBuilder(
						Constants.URL_GOOGLE_MAP_SEARCH).append(Util
						.getUrlEncodedString(params));
				Log.d(TAG, "ping at ->>" + googlePlacesUrl.toString());
				StringRequest request = new StringRequest(
						googlePlacesUrl.toString(), searchPlacesSuccessListner,
						searchPlacesErrorListner);
				getReqQueue().add(request);
			} else {
				Util.toast(getApplicationContext(),
						getString(R.string.toast_no_internet));
			}
		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_location);
		imageButtonIlp = (ImageButton) findViewById(R.id.imageButtonLocationIlp);
		imageButtonIlp.setOnClickListener(imageButtonIlpClickListner);
		imageButtonMyLocation = (ImageButton) findViewById(R.id.imageButtonLocationMyLocation);
		imageButtonMyLocation
				.setOnClickListener(imageButtonMyLocationClickListner);
		imageButtonHostel = (ImageButton) findViewById(R.id.imageButtonLocationHostel);
		imageButtonHostel.setOnClickListener(imageButtonHostelClickListner);
		imageButtonSearch = (ImageButton) findViewById(R.id.imageButtonLocationSearch);
		imageButtonSearch.setOnClickListener(searchButtonClickListner);

		editTextSearch = (EditText) findViewById(R.id.editTextLocationSearch);
		progressBarSearchLocation = (ProgressBar) findViewById(R.id.progressBarLocationSearch);
		if (!Util.isGooglePlayServicesAvailable(this)) {
			finish();
		}
		// set to a default ILP location depending on persons ILP
		Employee emp = Util.getEmployee(getApplicationContext());
		ILPLocation loc = Util.getLocations(emp.getLocation(),
				Constants.LOCATIONS.TYPE.ILP).get(0);
		longitude = loc.getLon();
		latitude = loc.getLat();

	}

	private GoogleMap getMap() {
		if (map == null) {
			SupportMapFragment fragment = ((SupportMapFragment) getSupportFragmentManager()
					.findFragmentById(R.id.fragmentLocationMap));
			if (fragment != null)
				map = fragment.getMap();
		}
		return map;
	}

	@Override
	protected void onStart() {
		initMap();
		getMyLocation();
		super.onStart();
	}

	private void initMap() {
		getMap().setMapType(GoogleMap.MAP_TYPE_NORMAL);
		getMap().setMyLocationEnabled(true);
		getMap().getUiSettings().setZoomControlsEnabled(false);
		getMap().getUiSettings().setCompassEnabled(true);
		getMap().getUiSettings().setRotateGesturesEnabled(true);
		getMap().getUiSettings().setZoomGesturesEnabled(true);
	}

	private RequestQueue getReqQueue() {
		if (reqQue == null)
			reqQue = Volley.newRequestQueue(getApplicationContext());
		return reqQue;
	}

	public android.location.Location getMyLocation() {
		checkGps();
		getMap().setMyLocationEnabled(true);
		Criteria criteria = new Criteria();

		String bestProvider = getLocationManager().getBestProvider(criteria,
				false);
		Log.d("Provider", bestProvider);
		getLocationManager().requestLocationUpdates(bestProvider, 20000, 0,
				locationListner);
		android.location.Location location = getLocationManager()
				.getLastKnownLocation(bestProvider);
		if (location != null) {
			latitude = location.getLatitude();
			longitude = location.getLongitude();
			LatLng latLng = new LatLng(latitude, longitude);
			getMap().moveCamera(CameraUpdateFactory.newLatLng(latLng));
			getMap().animateCamera(CameraUpdateFactory.zoomTo(15));
		}
		return location;
	}

	private void checkGps() {
		if (!getLocationManager().isProviderEnabled(
				LocationManager.GPS_PROVIDER)) {
			buildAlertMessageNoGps();
		}
	}

	private void buildAlertMessageNoGps() {
		final AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setMessage(
				"Your GPS seems to be disabled, do you want to enable it?")
				.setCancelable(false)
				.setPositiveButton("Yes",
						new DialogInterface.OnClickListener() {
							public void onClick(final DialogInterface dialog,
									final int id) {
								startActivity(new Intent(
										android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS));
							}
						})
				.setNegativeButton("No", new DialogInterface.OnClickListener() {
					public void onClick(final DialogInterface dialog,
							final int id) {
						dialog.cancel();
						onBackPressed();
					}
				});
		final AlertDialog alert = builder.create();
		alert.show();
	}

	private LocationManager getLocationManager() {
		if (locationManager == null) {
			locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
		}
		return locationManager;
	}
}