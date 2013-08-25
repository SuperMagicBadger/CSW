/*
 * John Rice, DeltaCityLabs 2013
 */
package com.DeltaCityLabs.Fragments;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.StreamCorruptedException;
import java.util.ArrayList;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.DeltaCityLabs.CitySweep.Data;
import com.example.idonteven.R;

public class ReportListFragment extends Fragment{
	//varblok----------------------------------------------
	public ArrayList<ReportListItem> oldData;
	//varblok==============================================
	private class ReportListItem{
		public Data d;
		public TextView rep, loc, time;
		public ReportListItem(Data _d){
			d = _d;
			
			//set rep
			rep = new TextView(getActivity());
			rep.setText(d.getTye());
			rep.setTextSize(20);
			
			//set loc
			loc = new TextView(getActivity());
			loc.setText("\tlat: " + d.latitude() + "  lon: " + d.longitude());
			loc.setTextSize(10);
			
			//set time
			time = new TextView(getActivity());
			time.setText("\t" + d.timestamp());
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
	public ReportListFragment(){
		oldData = new ArrayList<ReportListFragment.ReportListItem>();
		oldData.clear();
	}
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		//load view
		View v = inflater.inflate(R.layout.report_lister, container, false);
		return v;
	}
	
	@Override
	public void onStart() {
		super.onStart();
		loadData();
		Log.d("History", "starting history tab");
	}
	
	public void addReport(Data d){
		Data newd = new Data(new Bundle(d.getBundle()));
		ReportListItem item = new ReportListItem(newd);
		item.Add();
		oldData.add(item);
	}
	
	public void removeReport(int i){
		ReportListItem itm = oldData.get(i);
		itm.Remove();
	}
	
	public void loadData(){
		Log.d("History", "loading state");
		try {			
			FileInputStream file = getActivity().openFileInput("somefile");
			ObjectInputStream input = new ObjectInputStream(file);
			
			int size = input.readInt();
			Log.i("History", "Loading " + size + " instances");
			Data d;
			
			for(int i = 0; i < size; i++){
				d = new Data();
				d.load(input);
				ReportListItem item = new ReportListItem(d);
				item.Add();
				oldData.add(item);
			}
			input.close();
			file.close();
		} catch (FileNotFoundException e) {
			Log.e("History", "could not open history file");
		} catch (StreamCorruptedException e) {
			Log.e("History", "Stream corruted");
		} catch (IOException e) {
			Log.e("History", "some shit io error " + e); 
		}
	}
	public void saveData(){
		Log.i("History", "saving state");
		try {			
			FileOutputStream file = getActivity().openFileOutput("somefile", Context.MODE_PRIVATE);
			ObjectOutputStream output = new ObjectOutputStream(file);

			Log.i("History", "saving " + oldData.size() + " instances");
			output.writeInt(oldData.size());			
			
			for(ReportListItem i : oldData){
				i.d.dump(output);
			}
			output.close();
			file.close();
		} catch (FileNotFoundException e) {
			Log.e("History", "could not open history file");
		} catch (StreamCorruptedException e) {
			Log.e("History", "Stream corruted");
		} catch (IOException e) {
			Log.e("History", "some shit io error");
		}
	}
	public void clearData(){
		for(ReportListItem r : oldData){
			r.Remove();
		}
		oldData.clear();
	}
}
