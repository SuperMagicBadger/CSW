package com.DeltaCityLabs.Utilities;

import java.util.ArrayList;

import android.util.Log;

public class Report {
	// varblok---------------
	public static final String key_tagtype = "type";
	public static final String key_lat = "latitude";
	public static final String key_lon = "longitude";
	public static final String key_time = "time";
	public static final String key_username = "userName";
	public static final String key_userepoch = "userNumber";
	public static final String key_keyorder = "keyorder";
	
	public ArrayList<String> tags;
	public ArrayList<Object> data;
	// varblok===============
	
	// constructor-------------------------------
	public Report(){
		tags = new ArrayList<String>();
		data = new ArrayList<Object>();
	}
	// constructor===============================
	
	// meta--------------------------------------
	public int size(){
		if(tags.size() == data.size())
			return tags.size();
		return -1;
	}
	// meta======================================
	
	// access------------------------------------
	public Object get(String s){
		if(tags.contains(s)){
			return data.get(tags.indexOf(s));
		}
		return null;
	}
	
	public String getString(String s){
		Object o = get(s);
		if(o != null){
			if(o instanceof String){
				return (String) o;
			} else {
				Log.e("Report", "requested string was not a string");
				return null;
			}
		}
		Log.e("Report", "requested tag does not exist: " + s);
		return null;
	}
	
	public float getFloat(String s){
		Object o = get(s);
		if(o instanceof Float){
			return (Float) o;
		}
		return 0;
	}
	
	public int getInt(String s){
		Object o = get(s);
		if(o instanceof Integer){
			return (Integer) o;
		}
		return 0;
	}
	
	@Override
	public String toString() {
		StringBuilder ret = new StringBuilder();
		
		String date = getString(key_time);
		if(date != null){
			ret.append(date + " ");
		}
		
		for(int i = 0; i < tags.size(); i++){
			if(tags.get(i).compareTo(key_time) != 0){
				ret.append(tags.get(i) + "=" + data.get(i) + " ");
			}
		}
		
		ret.append("\n");
		
		return ret.toString();
	}
	// access====================================
	
	// manips------------------------------------
	public void put(String t, Object o){
		Log.i("ReportPO", "Adding " + (o instanceof String ? "string" : "non-string") + " value:" + o.toString());
		tags.add(t);
		data.add(o);
	}
	// manips====================================
}
