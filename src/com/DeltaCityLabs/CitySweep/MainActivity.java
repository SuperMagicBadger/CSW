/*
 * John Rice, DeltaCityLabs 2013
 */
package com.DeltaCityLabs.CitySweep;

import android.app.ActionBar;
import android.app.ActionBar.Tab;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.support.v4.app.FragmentTransaction;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.DeltaCityLabs.Fragments.NetworkFragment;
import com.DeltaCityLabs.Fragments.ReportFragment;
import com.DeltaCityLabs.Fragments.ReportListFragment;
import com.DeltaCityLabs.Fragments.SetingsFragment;
import com.example.idonteven.R;

public class MainActivity extends FragmentActivity implements
		ActionBar.TabListener {

	// varblok-------------------------------------
	// pager
	ViewPager pager;
	PagerAdapter adapter;
	// fragments
	public static SetingsFragment settingsFrag;
	public static NetworkFragment networkFragmnet;
	public static ReportFragment generator;
	public static ReportListFragment history;
	private Intent networkServiceIntent;
	private AlertDialog.Builder bd;
	// constants
	public static final String key_firstrun = "first_run";
	public static final String key_userepoch = "user_epoch";
	public static final String key_username = "user_name";
	public static final String key_sendmetrics = "send_metrics";
	// statics
	private SharedPreferences cswPreferences;
	private SharedPreferences.Editor cswPEditor;

	// varblok====================================

	// helpers------------------------------------
	private class PagerAdapter extends FragmentPagerAdapter {

		public PagerAdapter(FragmentManager fm) {
			super(fm);
		}

		@Override
		public Fragment getItem(int arg0) {
			switch (arg0) {
			case 0:
				return generator;
			case 1:
				return history;
			case 2:
				return settingsFrag;
			default:
				return null;
			}
		}

		@Override
		public int getCount() {
			return 3;
		}

		@Override
		public CharSequence getPageTitle(int position) {
			if (position < R.integer.tab_count) {
				return getResources().getStringArray(R.array.tab_titles)[position];
			}
			return "Settings";
		}

	}

	// helpers====================================

	/**
	 * The {@link ViewPager} that will host the section contents.
	 */

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		// init variables
		generator = new ReportFragment();
		history = new ReportListFragment();
		settingsFrag = new SetingsFragment();
		cswPreferences = PreferenceManager.getDefaultSharedPreferences(this);
		cswPEditor = cswPreferences.edit();
		bd = new Builder(this);
		networkServiceIntent = new Intent(this, NetworkService.class);

		// get net fragment
		FragmentManager fman = getSupportFragmentManager();
		networkFragmnet = (NetworkFragment) fman
				.findFragmentByTag(NetworkFragment.tag);
		if (networkFragmnet == null) {
			networkFragmnet = new NetworkFragment();
			fman.beginTransaction().add(networkFragmnet, NetworkFragment.tag)
					.commit();
		}

		// start services
		startService(networkServiceIntent);

		// Set up the action bar.
		final ActionBar actionBar = getActionBar();
		actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
		actionBar.setDisplayShowTitleEnabled(false);
		actionBar.setDisplayUseLogoEnabled(false);
		actionBar.setDisplayShowHomeEnabled(false);

		// Create the adapter that will return a fragment for each of the three
		// primary sections of the application.
		adapter = new PagerAdapter(getSupportFragmentManager());

		// Set up the ViewPager with the sections adapter.
		pager = (ViewPager) findViewById(R.id.pager);
		if (pager == null) {
			Log.e("MainActivity", "Could not load pager");
			assert false;
		}
		pager.setAdapter(adapter);

		// When swiping between different sections, select the corresponding
		// tab. We can also use ActionBar.Tab#select() to do this if we have
		// a reference to the Tab.
		pager.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
			@Override
			public void onPageSelected(int position) {
				actionBar.setSelectedNavigationItem(position);
			}
		});

		// For each of the sections in the app, add a tab to the action bar.
		for (int i = 0; i < adapter.getCount(); i++) {
			// Create a tab with text corresponding to the page title defined by
			// the adapter. Also specify this Activity object, which implements
			// the TabListener interface, as the callback (listener) for when
			// this tab is selected.
			actionBar.addTab(actionBar.newTab()
					.setText(adapter.getPageTitle(i)).setTabListener(this));
		}

		// manage initial preferences
		if (cswPreferences.getBoolean(key_firstrun, true)) {
			// settings
			long epoc = System.currentTimeMillis();
			cswPEditor.putBoolean(key_firstrun, false);
			cswPEditor.putLong(key_userepoch, epoc);
			cswPEditor.putString(key_username, "Generic User");
			Log.i("Activity", "Its the first run, yo. And your epoc is... "
					+ epoc);
			Log.i("Activity", "initial setting of username to Generic User");
			cswPEditor.commit();
		} else {
			Log.i("Activity",
					"you already got an epoch:"
							+ cswPreferences.getLong(key_userepoch, -1));
		}

	}

	@Override
	protected void onStart() {
		super.onStart();
		networkFragmnet.doBindService();
	}

	@Override
	protected void onStop() {
		networkFragmnet.doReleaseService();
		super.onStop();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater i = getMenuInflater();
		i.inflate(R.menu.thatonemenu, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.about:
			bd.setMessage(getResources().getString(R.string.str_about));
			bd.create().show();
			break;
		case R.id.policy:
			bd.setMessage(getResources()
					.getString(R.string.str_privacy_message));
			bd.create().show();
			break;
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
		Log.i("MainActivity", "got a result: " + (resultCode  == RESULT_OK) + " " + 
				Integer.toHexString(requestCode) + " " + Integer.toHexString(ButtonEditor.return_tag));
		if (resultCode == RESULT_OK) {
			switch(requestCode){
			case ButtonEditor.return_tag:
				Log.i("MainActivity", intent.getExtras().getString(ButtonEditor.group_data_tag));
				networkFragmnet.loader.parseSet(intent.getExtras().getString(ButtonEditor.group_data_tag));
				break;
			}
		}
	}

	// Tab navigation--------------------------------------
	@Override
	public void onTabReselected(Tab tab, android.app.FragmentTransaction ft) {
	}

	@Override
	public void onTabSelected(Tab tab, android.app.FragmentTransaction ft) {
		pager.setCurrentItem(tab.getPosition());
		switch (tab.getPosition()) {
		case 1:
			history.refresh();
			break;
		case 2:
			settingsFrag.refresh();
			break;
		}
	}

	@Override
	public void onTabUnselected(Tab tab, android.app.FragmentTransaction ft) {
	}
	// Tab navigation======================================
}
