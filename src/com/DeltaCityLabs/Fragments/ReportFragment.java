/*
 * John Rice, DeltaCityLabs 2013
 */
package com.DeltaCityLabs.Fragments;

import java.util.ArrayList;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;

import com.DeltaCityLabs.CitySweep.MainActivity;
import com.DeltaCityLabs.Utilities.Report;
import com.example.idonteven.R;

public class ReportFragment extends Fragment {
	// varblok--------------------------
	private Report trackedReport;
	private LocationManager lm;
	private LinearLayout layout;
	private AlertDialog.Builder builder;
	public String buttonLayout = "default";
	// varblok==========================
	
	// helper classes-----------------------------------------------------------

	private class reporter implements DialogInterface.OnClickListener {

		@Override
		public void onClick(DialogInterface dialog, int which) {
			if(trackedReport != null){
				Log.d("Report", "Adding selected report to transmiter");
				MainActivity.networkFragmnet.newReport(trackedReport);
			}
		}

	}

	private class canceler implements DialogInterface.OnClickListener {
		@Override
		public void onClick(DialogInterface dialog, int which) {
			trackedReport = null;
		}
	}

	private class clicker implements OnClickListener {
		private String internalTag;

		public clicker(Button b) {
			internalTag = (String) b.getText();
		}

		@Override
		public void onClick(View v) {
			trackedReport = generateReport(internalTag);
			
			builder.setTitle(internalTag);
			builder.setMessage("report a " + internalTag + "?");
			builder.setPositiveButton("Ok", new reporter());
			builder.setNegativeButton("Cancel", new canceler());
			AlertDialog ad = builder.create();
			ad.show();
		}
	}

	private class customTag implements OnEditorActionListener {

		@Override
		public boolean onEditorAction(TextView textview, int arg1, KeyEvent arg2) {
			trackedReport = generateReport(textview.getText().toString());
			
			builder.setTitle(textview.getText());
			builder.setMessage("Report a " + textview.getText() + "?");
			builder.setPositiveButton("Ok", new reporter());
			builder.setNegativeButton("Cancel", new canceler());
			AlertDialog ad = builder.create();
			ad.show();
			return false;
		}

	}

	// helper classes===========================================================

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		//init variables
		lm = (LocationManager) getActivity().getSystemService(
				Context.LOCATION_SERVICE);
		builder = new AlertDialog.Builder(getActivity());

		//inflate the view
		View v = inflater.inflate(R.layout.report_generator, container, false);
		layout = (LinearLayout) v.findViewById(R.id.report_list);
		//assign listeners
		EditText custom = (EditText) v.findViewById(R.id.button_custom);
		custom.setOnEditorActionListener(new customTag());
		
		refresh();
		return v;
	}
	
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
//		refresh();
	}
	
	public void refresh(){
		ArrayList<Button> list = MainActivity.networkFragmnet.loader.addButtonsToView(buttonLayout, layout);
		if(list == null){
			Log.e("ReportFragment", "couldn not load default buttons");
			return;
		}
		for(Button btn : list){
			btn.setOnClickListener(new clicker(btn));
		}
	}
	
	public Report generateReport(String tag){
		Report r = new Report();
		Location l = lm.getLastKnownLocation(LocationManager.GPS_PROVIDER);
		
		r.put(Report.key_tagtype, tag);
		r.put(Report.key_lat, Float.valueOf((float) l.getLatitude()));
		r.put(Report.key_lon, Float.valueOf((float) l.getLongitude()));
		
		return r;
	}
}
