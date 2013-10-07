package com.DeltaCityLabs.Fragments;

import android.support.v4.app.Fragment;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.util.Log;

import com.DeltaCityLabs.CitySweep.NetworkService;
import com.DeltaCityLabs.CitySweep.NetworkService.LocalBinder;
import com.DeltaCityLabs.Utilities.Report;
import com.DeltaCityLabs.Utilities.ReportLoader;
import com.DeltaCityLabs.Utilities.ReportManager;

public class NetworkFragment extends Fragment{

	// varblok-------------------------
	public static final String tag = "csw_netwrok_fragment";
	public ReportLoader loader;
	public ReportManager manager;
	private Connection con;
	private NetworkService service;
	// varblok=========================
	
	// helpers-------------------------
	private class Connection implements ServiceConnection{

		@Override
		public void onServiceConnected(ComponentName name, IBinder binder) {
			LocalBinder lbinder = (LocalBinder) binder;
			service = lbinder.getService();
		}

		@Override
		public void onServiceDisconnected(ComponentName name) {
			service = null;
		}
		
	}
	// helpers=========================
	
	// fragment management-------------
	@Override
	public void onCreate(Bundle savedInstanceState) {
		// setup the fragment
		super.onCreate(savedInstanceState);
		setRetainInstance(true);
		
		// load the managers
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
		loader = new ReportLoader();
		loader.loadFromPreferences(getActivity(), prefs);

		manager = new ReportManager();
		manager.load(getActivity());
		
		Log.i("NetworkFrament", "netfrag created... act:" + getActivity());
		
		// bind the service
		con = new Connection();
//		doBindService();
	}
	
	@Override
	public void onDestroy() {
		// dump the loaders
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
		loader.pushToPreferences(prefs);
		
		manager.dump(getActivity());
		Log.i("NetworkFrament", "netfrag destroyed");
		super.onDestroy();
	}

	// fragment management=============
	
	// service management--------------
	public void doBindService(){
		getActivity().bindService(
				new Intent(getActivity(), NetworkService.class),
				con, 
				Context.BIND_AUTO_CREATE
				);
	}
	
	public void doReleaseService(){
		getActivity().unbindService(con);
	}
	// service management==============
	
	public void newReport(Report r){
		manager.newReport(r);
		service.send(r.toString());
	}
}
