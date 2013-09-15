package com.DeltaCityLabs.Utilities;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.ArrayList;

import android.content.Context;
import android.util.Log;


public class ReportManager {
	// varblok-------------------------
	//constants
	public final String reportFileName = "h2_repport_file";
	public final String historyFileName = "h2_history_file";
	private final String report_terminator = "END_OF_REPORT";
	// data queues
	public ArrayList<Report> waitingData;
	public ArrayList<Report> historyData;
	// varblok=========================
	
	// constructors------------------------------
	public ReportManager(){
		Log.i("ReportManager", "created!");
		waitingData = new ArrayList<Report>();
		historyData = new ArrayList<Report>();
	}
	// constructors==============================
	
	// report------------------------------------
	public void newReport(Report d){
		Log.i("ReportManager", "adding repport with " + d.size() + " keys");
		historyData.add(d);
		waitingData.add(d);
	}
	
	public boolean queueRepport(Report d){
		if(!waitingData.contains(d)){
			waitingData.add(d);
			return true;
		}
		return false;
	}
	
	public Report[] getHistory(){
		Report[] history = new Report[historyData.size()];
		 
		for(int i = 0; i < history.length; i++){
			history[i] = historyData.get(i);
		}
		
		return history;
	}
	
	public int reportsQueued(){
		return waitingData.size();
	}
	
	public Report peekNextToSend(){
		return waitingData.get(0);	
	}
	
	public Report popNextToSend(){
		Report ret = null;
		
		if(waitingData.size() > 0){
			ret = waitingData.remove(0);
		}
		
		return ret;
	}
	// report====================================
	
	// file io-----------------------------------
	public void dump(Context con) {
		// vars--------------
		ObjectOutputStream objectWriter;
		FileOutputStream output;

		try {
			
			//write history
			output = con.openFileOutput(historyFileName, Context.MODE_PRIVATE);
			objectWriter = new ObjectOutputStream(output);
		
			for(Report d : historyData){
				for(int i = 0; i < d.size(); i++){
					objectWriter.writeChars(d.tags.get(i));
					objectWriter.writeObject(d.data.get(i));
				}
				objectWriter.writeChars(report_terminator);
			}
			
			objectWriter.close();
			output.close();
			
			//write pending reports
			output = con.openFileOutput(reportFileName, Context.MODE_PRIVATE);
			objectWriter = new ObjectOutputStream(output);
			
			for(Report d : waitingData){
				for(int i = 0; i < d.size(); i++){
					objectWriter.writeChars(d.tags.get(i));
					objectWriter.writeObject(d.data.get(i));
				}
				objectWriter.writeChars(report_terminator);
			}
			
			objectWriter.close();
			output.close();
			
		} catch (FileNotFoundException e) {
			Log.e("ReportMan", "could not find file.  " + e.getMessage());
			e.printStackTrace();
		} catch (IOException e) {
			Log.e("ReportMan", "file io error: " + e.getMessage());
			e.printStackTrace();
		}		
		
		//dump waiting queue
		
	}
	
	public void load(Context con){
		
	}
	// file io===================================
}
