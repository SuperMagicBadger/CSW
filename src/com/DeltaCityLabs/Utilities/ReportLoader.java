package com.DeltaCityLabs.Utilities;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;
import java.util.Set;

import com.example.idonteven.R;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.Color;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

public class ReportLoader {
	// vars------------------
	// constants
	private static String preferenceTag = "ReportLoader";
	public HashMap<String, ArrayList<String>> buttonSets;
	public ArrayList<String> activeButtons;

	// vars==================

	// constructors------------------------------
	public ReportLoader() {
		buttonSets = new HashMap<String, ArrayList<String>>();
		activeButtons = new ArrayList<String>();
	}

	// constructors==============================

	// access------------------------------------
	
	public String[] getSetList() {
		String[] setlist = new String[buttonSets.size()];
		int i = 0;
		for (String s : buttonSets.keySet()) {
			setlist[i++] = s;
		}
		return setlist;
	}

	public String groupString(String setName) {
		StringBuilder builder = new StringBuilder();

		builder.append(setName);

		if (buttonSets.containsKey(setName)) {
			for (String s : buttonSets.get(setName)) {
				builder.append("/" + s);
			}
		}

		return builder.toString();
	}

	public String[] getButtonNames(String buttonSet) {
		if (buttonSets.containsKey(buttonSet)) {
			ArrayList<String> names = buttonSets.get(buttonSet);
			String[] namesCopy = new String[names.size()];
			for (int i = 0; i < namesCopy.length; i++) {
				namesCopy[i] = new String(names.get(i));
			}
			return namesCopy;
		}
		return null;
	}

	public ArrayList<Button> addButtonsToView(String buttonSet, LinearLayout l) {
		if (buttonSets.containsKey(buttonSet)) {
			ArrayList<String> names = buttonSets.get(buttonSet);
			ArrayList<Button> list = new ArrayList<Button>();

			for (String name : names) {
				Button b = new Button(l.getContext());
				b.setText(name);
				b.setBackgroundColor(Color.alpha(0));
				list.add(b);
				l.addView(b, l.getChildCount() - 1);
			}

			return list;
		}

		return null;
	}

	public void removeButtonsFromView(String buttonSet, View v) {
		if (buttonSets.containsKey(buttonSet)) {
			LinearLayout layout = (LinearLayout) v;
			ArrayList<String> names = buttonSets.get(buttonSet);

			// remove button if its a part of the string set
			// linear search shouldn't be a problem seeing as how there
			// wont be that many buttons on screen at one time
			for (View child : v.getTouchables()) {
				if (child instanceof Button) {
					Button b = (Button) child;
					for (String name : names) {
						if (name.compareTo(b.getText().toString()) == 0) {
							layout.removeView(b);
							break;
						}
					}
				}
			}

		}
	}

	// access====================================

	// manips------------------------------------
	public void addSet(String setname, ArrayList<String> set) {
		buttonSets.put(setname, set);
	}

	public void addSet(String setname) {
		if (!buttonSets.containsKey(setname)) {
			buttonSets.put(setname, new ArrayList<String>());
		}
	}
	
	public void parseSet(String message){
		Scanner s = new Scanner(message);
		s.useDelimiter("/");
		ArrayList<String> list = new ArrayList<String>();
		
		String groupName = s.next();
		
		while(s.hasNext()){
			list.add(s.next());
		}
		
		addSet(groupName, list);
	}

	public void addToSet(String setname, String buttontag) {
		if (buttonSets.containsKey(setname)) {
			buttonSets.get(setname).add(buttontag);
		}
	}

	public void removeSet(String setname) {
		buttonSets.remove(setname);
	}

	public void removeFromSet(String setname, String buttontag) {
		if (buttonSets.containsKey(setname)) {
			buttonSets.get(setname).remove(buttontag);
		}
	}

	// manips====================================

	// button group loader-----------------------
	public void recordButtonGroup(String tag, String[] set) {
		ArrayList<String> internalSet = new ArrayList<String>();

		for (String s : set) {
			internalSet.add(s);
		}

		buttonSets.put(tag, internalSet);
	}

	public void removeButtonGroup(String tag) {
		buttonSets.remove(tag);
	}

	// button group loader=======================

	// preference access-------------------------
	private String prefTag(String id) {
		return preferenceTag + "_" + id;
	}

	public void loadFromPreferences(Context c, SharedPreferences preferences) {
		Set<String> keys = preferences.getStringSet(prefTag("keys"), null);

		if (keys == null) {
			// generate
			ArrayList<String> buttonTags = new ArrayList<String>();
			for (String s : c.getResources().getStringArray(
					R.array.str_report_button_list)) {
				buttonTags.add(s);
			}
			buttonSets.put("default", buttonTags);
		} else {
			// load
			for (String setName : keys) {
				// get and cut up the string
				String buttonlist = preferences.getString(prefTag(setName),
						"dicks");
				Scanner tokenizer = new Scanner(buttonlist);
				tokenizer.useDelimiter("/");

				// seperate out tags
				ArrayList<String> buttonNames = new ArrayList<String>();
				while (tokenizer.hasNext()) {
					String s = tokenizer.next();
					buttonNames.add(s);
					Log.i("ReportLoader", s);
				}

				// add to button sets
				buttonSets.put(setName, buttonNames);
			}
		}
	}

	public void pushToPreferences(SharedPreferences preferences) {
		Log.i("ReportLoader", "pushing...");
		// prepare the editor
		Editor editor = preferences.edit();

		editor.putStringSet(prefTag("keys"), buttonSets.keySet());

		for (String setName : buttonSets.keySet()) {
			StringBuilder buttonTags = new StringBuilder();
			Log.i("ReportLoader", "set: " + setName);
			for (String buttonTag : buttonSets.get(setName)) {
				buttonTags.append(buttonTag + "/");
			}
			editor.putString(prefTag(setName), buttonTags.toString());
			Log.i("ReportLoader", "button tags: " + buttonTags.toString());
		}

		editor.apply();
	}
	// preference access=========================
}
