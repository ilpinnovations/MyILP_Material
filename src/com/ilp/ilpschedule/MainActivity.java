package com.ilp.ilpschedule;

import android.app.Fragment;
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
import com.ilp.ilpschedule.model.SlotViewHolder;
import com.ilp.ilpschedule.util.Util;
import com.tcs.myilp.R;

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

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		if (savedInstanceState == null) {
			getFragmentManager().beginTransaction()
					.add(R.id.container, new ScheduleFragment()).commit();
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
					setTitle(currentTitle);
					super.onDrawerClosed(drawerView);
				}

				@Override
				public void onDrawerOpened(View drawerView) {

					setTitle(R.string.app_name);
					super.onDrawerOpened(drawerView);
				}
			};
		}
		drawerLayout.setDrawerListener(drawerToggler);
		drawerToggler.syncState();

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
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
			}
		}
		if (bundle != null)
			fragment.setArguments(bundle);
		getFragmentManager().beginTransaction()
				.replace(R.id.container, fragment, tag).commit();
	}

	public OnClickListener getScheduleClickListner() {
		if (scheduleItemClickListner == null) {
			scheduleItemClickListner = new OnClickListener() {
				@Override
				public void onClick(View view) {
					SlotViewHolder svh = (SlotViewHolder) view.getTag();
					Bundle bundle = new Bundle();
					bundle.putCharSequence("faculty", svh.getFacultyContent()
							.getText());
					bundle.putCharSequence("course", svh.getCourseContent()
							.getText());
					bundle.putLong("slot_id", svh.getId());
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
}