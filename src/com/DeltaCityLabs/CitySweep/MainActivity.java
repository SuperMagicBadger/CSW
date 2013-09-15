/*
 * John Rice, DeltaCityLabs 2013
 */
package com.DeltaCityLabs.CitySweep;

import android.app.ActionBar;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.FragmentTransaction;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
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
import android.widget.EditText;

import com.DeltaCityLabs.Fragments.ReportFragment;
import com.DeltaCityLabs.Fragments.ReportListFragment;
import com.DeltaCityLabs.Utilities.ReportLoader;
import com.DeltaCityLabs.Utilities.ReportManager;
import com.example.idonteven.R;

//DONE discalimer for 911 with a link
//DONE fix netwrok connection
//DONE location instead of lon/lat
//DONE privacy policy popup
//TODO custom theme
//DONE unique identifier: epoc as user?

//TODO clear history
//TODO Research metrics available

public class MainActivity extends FragmentActivity implements
		ActionBar.TabListener {

	/**
	 * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
	 * one of the sections/tabs/pages.
	 */
	public class SectionsPagerAdapter extends FragmentPagerAdapter {
		MainActivity act;

		public SectionsPagerAdapter(FragmentManager fm, MainActivity m) {
			super(fm);
			act = m;
		}

		@Override
		public Fragment getItem(int position) {
			switch (position) {
			case 0:
				return rf;
			case 1:
				return hf;
			default:
				return null;
			}
		}

		@Override
		public int getCount() {
			return 2;
		}

		@Override
		public CharSequence getPageTitle(int position) {
			if (position < R.integer.tab_count) {
				return getResources().getStringArray(R.array.tab_titles)[position];
			}
			return null;
		}
	}

	// helper classes=============================

	// varblok-------------------------------------
	//constants
	public static final String key_firstrun = "first_run";
	public static final String key_userepoch = "user_epoch";
	public static final String key_username = "user_name";
	public static final String key_sendmetrics = "send_metrics";
	private SectionsPagerAdapter mSectionsPagerAdapter;
	// fragments
	private ReportFragment rf;
	private ReportListFragment hf;
	private AlertDialog.Builder bd;
	//statics
	public static SharedPreferences cswPreferences;
	public static SharedPreferences.Editor cswPEditor;
	public static ReportManager reportManager; 
	public static ReportLoader reportLoader;
	// varblok====================================
	/**
	 * The {@link ViewPager} that will host the section contents.
	 */
	ViewPager mViewPager;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		// init variables
		rf = new ReportFragment();
		hf = new ReportListFragment();
		cswPreferences = PreferenceManager.getDefaultSharedPreferences(this);
		cswPEditor = cswPreferences.edit();
		bd = new Builder(this);

		reportManager = new ReportManager();
		reportManager.load(this);
		
		reportLoader = new ReportLoader();
		reportLoader.loadFromPreferences(cswPreferences);
		
		// Set up the action bar.
		final ActionBar actionBar = getActionBar();
		actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
		actionBar.setDisplayShowTitleEnabled(false);
		actionBar.setDisplayUseLogoEnabled(false);
		actionBar.setDisplayShowHomeEnabled(false);
		
		// Create the adapter that will return a fragment for each of the three
		// primary sections of the application.
		mSectionsPagerAdapter = new SectionsPagerAdapter(
				getSupportFragmentManager(), this);

		// Set up the ViewPager with the sections adapter.
		mViewPager = (ViewPager) findViewById(R.id.pager);
		mViewPager.setAdapter(mSectionsPagerAdapter);

		// When swiping between different sections, select the corresponding
		// tab. We can also use ActionBar.Tab#select() to do this if we have
		// a reference to the Tab.
		mViewPager
				.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
					@Override
					public void onPageSelected(int position) {
						actionBar.setSelectedNavigationItem(position);
					}
				});
		
		
		// For each of the sections in the app, add a tab to the action bar.
		for (int i = 0; i < mSectionsPagerAdapter.getCount(); i++) {
			// Create a tab with text corresponding to the page title defined by
			// the adapter. Also specify this Activity object, which implements
			// the TabListener interface, as the callback (listener) for when
			// this tab is selected.
			actionBar.addTab(actionBar.newTab()
					.setText(mSectionsPagerAdapter.getPageTitle(i))
					.setTabListener(this));
		}
		
		
		
		//manage initial preferences
		if(cswPreferences.getBoolean(key_firstrun, true)){
			//settings
			long epoc = System.currentTimeMillis();
			cswPEditor.putBoolean(key_firstrun, false);
			cswPEditor.putLong(key_userepoch, epoc);
			cswPEditor.putString(key_username, "Generic User");
			Log.i("Activity", "Its the first run, yo. And your epoc is... " + epoc);
			Log.i("Activity", "initial setting of username to Generic User");
			cswPEditor.commit();
		} else {
			Log.i("Activity", "you already got an epoch:" + cswPreferences.getLong(key_userepoch, -1));
		}
		
		
	}
	
	@Override
	protected void onDestroy() {
		reportManager.dump(this);
		reportLoader.pushToPreferences(cswPreferences);
		super.onDestroy();
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater i = getMenuInflater();
		i.inflate(R.menu.thatonemenu, menu);
		menu.findItem(R.id.sendmetrics).setChecked(cswPreferences.getBoolean(key_sendmetrics, false));
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
		case R.id.sendmetrics:
			item.setChecked(!item.isChecked());
			if(item.isChecked()){
				createUserNameDialog();
			}
			cswPEditor.putBoolean(key_sendmetrics, item.isChecked());
			cswPEditor.commit();
			break;
		case R.id.metriclist:
			break;
		case R.id.clear:
			hf.clearData();
			break;
		}
		return super.onOptionsItemSelected(item);
	}

	private void createUserNameDialog(){
		final EditText input = new EditText(this);
		input.setSingleLine(true);
		input.setHint("Generic User");
		
		bd.setMessage("Entrer User Name");
		bd.setView(input);
		
		bd.setPositiveButton("Accept", new OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				if(input.getText().toString().length() > 0){
					cswPEditor.putString(key_username, input.getText().toString());
					cswPEditor.commit();
				} else {
					cswPEditor.putString(key_username, "Generic User");
					cswPEditor.commit();
				}
			}
		});

		
		bd.create().show();
	}
	
	@Override
	public void onTabSelected(ActionBar.Tab tab,
			FragmentTransaction fragmentTransaction) {
		if(tab.getPosition() == 1){
			hf.refresh();
		}
		mViewPager.setCurrentItem(tab.getPosition());
	}

	@Override
	public void onTabUnselected(ActionBar.Tab tab,
			FragmentTransaction fragmentTransaction) {
	}

	@Override
	public void onTabReselected(ActionBar.Tab tab,
			FragmentTransaction fragmentTransaction) {
	}
}
