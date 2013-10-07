/*
 * John Rice, DeltaCityLabs 2013
 */
package com.DeltaCityLabs.Fragments;

import java.util.ArrayList;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.DeltaCityLabs.CitySweep.MainActivity;
import com.DeltaCityLabs.Utilities.Report;
import com.example.idonteven.R;

public class ReportListFragment extends Fragment {
	//varblok----------------------------------------------
	public ArrayList<ReportListItem> oldData;
	//varblok==============================================
	
	// helpers-----------------------------------
	private class ReportListItem{
		Report report;
		public TextView rep, loc, time;
		public ReportListItem(Report r){
			//vars
			String tempS;
			Float tempFone, tempFtwo;
			report = r;
			
			//set rep
			tempS = report.getString(Report.key_tagtype);
			rep = new TextView(getActivity());
			rep.setText(tempS);
			rep.setTextSize(20);
			
			//set loc
			tempFone = report.getFloat(Report.key_lat);
			tempFtwo = report.getFloat(Report.key_lon);
			loc = new TextView(getActivity());
			loc.setText("\tlat: " + tempFone + "  lon: " + tempFtwo);
			loc.setTextSize(10);
			
			//set time
			time = new TextView(getActivity());
			time.setText("\t" + "NEVAR!");
			time.setTextSize(10);
		}
		
		public void Add(){
			View v = getView();
			LinearLayout l = (LinearLayout) v.findViewById(R.id.history_linear_layout);
			l.addView(rep,0);
			l.addView(loc,1);
			l.addView(time,2);
		}
		public void Remove(){
			LinearLayout l = (LinearLayout) getView().findViewById(R.id.history_linear_layout);
			l.removeView(rep);
			l.removeView(loc);
			l.removeView(time);
		}
	}
	// helpers===================================
	
	// constructors------------------------------
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		oldData = new ArrayList<ReportListFragment.ReportListItem>();
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		//load view
		Log.i("ReportList", "view created");
		View v = inflater.inflate(R.layout.report_lister, container, false);
		return v;
	}
	// constructors==============================
	
	// report management-------------------------
	public void addReport(Report r){
		ReportListItem item = new ReportListItem(r);
		item.Add();
		oldData.add(item);
	}
	
	public void removeReport(int i){
		ReportListItem itm = oldData.get(i);
		itm.Remove();
	}
	public void clearData(){
		for(ReportListItem r : oldData){
			r.Remove();
		}
		oldData.clear();
	}
	
	public void refresh(){
		Log.i("ReportLister", "refresh!");
		clearData();
		
		for(Report r : MainActivity.networkFragmnet.manager.historyData){
			addReport(r);
		}
	}
	// report management=========================
}
