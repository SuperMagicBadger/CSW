package com.DeltaCityLabs.Utilities;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;
import java.util.Set;

import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.Color;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

public class ReportLoader {
	// vars------------------
	//constants
	private static String preferenceTag = "ReportLoader";
	public HashMap<String, String[]> buttonSets;
	public ArrayList<String> activeButtons;
	// vars==================
	
	// constructors------------------------------
	public ReportLoader(){
		buttonSets = new HashMap<String, String[]>();
		activeButtons = new ArrayList<String>();
	}
	// constructors==============================
	
	// access------------------------------------
	public String[] getButtonNames(String buttonSet){
		if(buttonSets.containsKey(buttonSet)){
			String[] names = buttonSets.get(buttonSet);
			String[] namesCopy = new String[names.length];
			for(int i = 0; i < namesCopy.length; i++){
				namesCopy[i] = new String(names[i]);
			}
			return namesCopy;
		}
		return null;
	}
	
	public ArrayList<Button> addButtonsToView(String buttonSet, LinearLayout l){
		if(buttonSets.containsKey(buttonSet)){
			String[] names = buttonSets.get(buttonSet);
			ArrayList<Button> list = new ArrayList<Button>();
			
			for(String name : names){
				Button b = new Button(l.getContext());
				b.setText(name);
				b.setBackgroundColor(Color.alpha(0));
				list.add(b);
				l.addView(b, 0);
			}
			
			return list;
		}
		
		return null;
	}
	
	public void removeButtonsFromView(String buttonSet, View v){
		if(buttonSets.containsKey(buttonSet)){
			LinearLayout layout = (LinearLayout) v;
			String[] names = buttonSets.get(buttonSet);
			
			// remove button if its a part of the string set
			// linear search shouldn't be a problem seeing as how there
			// wont be that many buttons on screen at one time
			for(View child : v.getTouchables()){
				if(child instanceof Button){
					Button b = (Button) child;
					for(String name : names){
						if(name.compareTo(b.getText().toString()) == 0){
							layout.removeView(b);
							break;
						}
					}
				}
			}
			
		}
	}
	// access====================================
	
	// button group loader-----------------------
	public void recordButtonGroup(String tag, String[] set){
		String[] internalSet = new String[set.length];
		
		for(int i = 0; i < internalSet.length; i++){
			internalSet[i]  = new String(set[i]);
		}
		
		buttonSets.put(tag, internalSet);
	}
	
	public void removeButtonGroup(String tag){
		buttonSets.remove(tag);
	}
	// button group loader=======================
	
	// preference access-------------------------
	private String prefTag(String id){
		return preferenceTag + "_" + id;
	}
	
	public void loadFromPreferences(SharedPreferences preferences){
		Set<String> keys = preferences.getStringSet(prefTag("keys"), null);
		
		if(keys == null){
			//generate
			String[] buttonTags = new String[]{
				"asdf", "asdf", "fdsa", "fdsa"
			};
			buttonSets.put("default", buttonTags);
		} else {
			//load
			for(String setName : keys){
				//get and cut up the string
				String buttonlist = preferences.getString(prefTag(setName), "dicks");
				Scanner tokenizer = new Scanner(buttonlist);
				tokenizer.useDelimiter("/");
				
				//seperate out tags
				ArrayList<String> buttonNames = new ArrayList<String>();
				while(tokenizer.hasNext()){
					String s = tokenizer.next();
					buttonNames.add(s);
					Log.i("ReportLoader", s);
				}
				
				//add to button sets
				buttonSets.put(setName, buttonNames.toArray(new String[0]));
			}
		}
	}
	
	public void pushToPreferences(SharedPreferences preferences){
		Log.i("ReportLoader", "pushing...");
		//prepare the editor
		Editor editor = preferences.edit();
	
		editor.putStringSet(prefTag("keys"), buttonSets.keySet());
		
		for(String setName : buttonSets.keySet()){
			StringBuilder buttonTags = new StringBuilder();
			Log.i("ReportLoader", "set: " + setName);
			for(String buttonTag : buttonSets.get(setName)){
				buttonTags.append(buttonTag + "/");
			}
			editor.putString(prefTag(setName), buttonTags.toString());
			Log.i("ReportLoader", "button tags: " + buttonTags.toString());
		}
		
		editor.apply();
	}
	// preference access=========================
}
