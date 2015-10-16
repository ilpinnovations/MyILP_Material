package com.ilp.ilpschedule;

import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ListView;

import com.ilp.ilpschedule.adapter.DrawerAdapter;
import com.ilp.ilpschedule.model.DrawerItemViewHolder;
import com.ilp.ilpschedule.model.Employee;
import com.ilp.ilpschedule.model.SlotViewHolder;
import com.ilp.ilpschedule.util.Constants;
import com.ilp.ilpschedule.util.Util;

public class MainActivity extends ActionBarActivity {
	public static final String TAG = "MainActivity";
	private Toolbar toolbar;
	private ListView drawerList;
	private ActionBarDrawerToggle drawerToggler;
	private DrawerLayout drawerLayout;
	private DrawerAdapter drawerAdapter;
	private String currentTitle;
	private OnClickListener drawerItemClickListner;
	private OnClickListener scheduleItemClickListner;
	private boolean navigateBackToFragment = false;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		if (savedInstanceState == null) {
			getFragmentManager()
					.beginTransaction()
					.add(R.id.container, new ScheduleFragment(),
							ScheduleFragment.TAG).commit();
			currentTitle = getString(R.string.title_schedule);
		} else {
			currentTitle = savedInstanceState.getString("title");
		}

		if (toolbar == null) {
			toolbar = (Toolbar) findViewById(R.id.toolbar);
			setSupportActionBar(toolbar);
		}
		if (drawerAdapter == null)
			drawerAdapter = new DrawerAdapter(getApplicationContext(),
					R.layout.drawer_item,
					Util.getDrawerItemList(getApplicationContext()),
					getDrawerItemClickListner());
		if (drawerList == null) {
			drawerList = (ListView) findViewById(R.id.listViewDrawerMenu);
			drawerList.setAdapter(drawerAdapter);
		}
		if (drawerLayout == null)
			drawerLayout = (DrawerLayout) findViewById(R.id.drawer);
		if (drawerToggler == null) {
			drawerToggler = new ActionBarDrawerToggle(this, drawerLayout,
					toolbar, R.string.opne, R.string.close) {
				@Override
				public void onDrawerClosed(View drawerView) {
					if (getTitle().toString().equalsIgnoreCase(
							getString(R.string.app_name)))
						setTitle(currentTitle);
					super.onDrawerClosed(drawerView);
				}

				@Override
				public void onDrawerOpened(View drawerView) {
					currentTitle = getTitle().toString();
					setTitle(R.string.app_name);
					super.onDrawerOpened(drawerView);
				}

			};
		}
		drawerLayout.setDrawerListener(drawerToggler);
		drawerToggler.syncState();
		Util.resetProgressDialog();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		int id = item.getItemId();
		if (id == R.id.action_logout) {

			logout();
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		outState.putString("title", currentTitle);
	}

	private void changeFragment(String tag, Bundle bundle) {
		Fragment fragment;
		fragment = getFragmentManager().findFragmentByTag(tag);

		if (fragment == null) {
			switch (tag) {
			case ScheduleFragment.TAG:
				fragment = new ScheduleFragment();
				currentTitle = getString(R.string.title_schedule);
				break;
			case BadgeFragment.TAG:
				fragment = new BadgeFragment();
				currentTitle = getString(R.string.title_badges);
				break;
			case ContactFragment.TAG:
				fragment = new ContactFragment();
				currentTitle = getString(R.string.title_contacts);
				break;
			case NotificationFragment.TAG:
				fragment = new NotificationFragment();
				currentTitle = getString(R.string.title_notification);
				break;
			case FeedbackFragment.TAG:
				fragment = new FeedbackFragment();
				currentTitle = getString(R.string.title_feedback);
				navigateBackToFragment = true;
				break;
			case LocationActivity.TAG:
				fragment = null;
				currentTitle = getString(R.string.title_location);
				break;
			}
		}
		if (tag.equalsIgnoreCase(FeedbackFragment.TAG))
			((FeedbackFragment) fragment).setData(bundle);
		if (fragment != null) {
			FragmentTransaction fragmentTransaction = getFragmentManager()
					.beginTransaction().replace(R.id.container, fragment, tag);
			fragmentTransaction.addToBackStack(tag);
			fragmentTransaction.commit();
		} else
			startActivity(new Intent(getApplicationContext(),
					LocationActivity.class));
	}

	@Override
	public void setTitle(CharSequence title) {
		super.setTitle(title);
	}

	public OnClickListener getScheduleItemClickListner() {
		if (scheduleItemClickListner == null) {
			scheduleItemClickListner = new OnClickListener() {
				@Override
				public void onClick(View view) {
					Employee emp = Util.getEmployee(getApplicationContext());
					SlotViewHolder svh = (SlotViewHolder) view.getTag();
					String emp_id = svh.getFacultyContent().getText()
							.toString().replaceAll("[^0-9]", "");
					Bundle bundle = new Bundle();
					if (emp_id != null && emp_id.trim().length() > 0) {
						if (Integer.parseInt(emp_id) == emp.getEmpId()) {
							bundle.putBoolean(
									Constants.BUNDLE_KEYS.FEEDBACK_FRAGMENT.IS_FACULTY,
									true);
						}
					}
					bundle.putCharSequence(
							Constants.BUNDLE_KEYS.FEEDBACK_FRAGMENT.FACULTY,
							svh.getFacultyContent().getText());
					bundle.putCharSequence(
							Constants.BUNDLE_KEYS.FEEDBACK_FRAGMENT.COURSE, svh
									.getCourseContent().getText());
					bundle.putLong(
							Constants.BUNDLE_KEYS.FEEDBACK_FRAGMENT.SLOT_ID,
							svh.getId());
					changeFragment(FeedbackFragment.TAG, bundle);
				}
			};
		}
		return scheduleItemClickListner;
	}

	public OnClickListener getDrawerItemClickListner() {
		if (drawerItemClickListner == null) {
			drawerItemClickListner = new OnClickListener() {
				@Override
				public void onClick(View view) {
					DrawerItemViewHolder dvh = (DrawerItemViewHolder) view
							.getTag();
					drawerLayout.closeDrawers();
					changeFragment(dvh.getTag(), null);
				}
			};
		}
		return drawerItemClickListner;
	}

	private void logout() {
		Util.clearPref(getApplicationContext());
		startActivity(new Intent(getApplicationContext(), LoginActivity.class));
		finish();
	}

	@Override
	public void onBackPressed() {
		if (navigateBackToFragment) {
			getFragmentManager().popBackStack();
			navigateBackToFragment = false;
		} else
			super.onBackPressed();
	}
}