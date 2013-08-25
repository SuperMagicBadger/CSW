/*
 * John Rice, DeltaCityLabs 2013
 */

package com.DeltaCityLabs.CitySweep;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

import android.os.Bundle;
import android.util.Log;

public class Data {
	// varblok----------------
	public static final String key_tagtype = "type";
	public static final String key_lat = "latitude";
	public static final String key_lon = "longitude";
	public static final String key_time = "time";
	public static final String key_username = "userName";
	public static final String key_userepoch = "userNumber";
	public static final String key_keyorder = "keyorder";
	public Bundle data;

	// varblok================

	// constructors-------------------------------
	public Data() {
		data = new Bundle();
		data.putStringArrayList(key_keyorder, new ArrayList<String>());
	}

	public Data(Bundle b) {
		data = b;
		Log.d("Data", "Created from bundle: " + this.toString());
	}

	public void setupEventReport(String tag) {
		data.clear();
		data.putStringArrayList(key_keyorder, new ArrayList<String>());
		setTime();

		if (MainActivity.cswPreferences.getBoolean(
				MainActivity.key_sendmetrics, false)) {
			this.addUserName(true);
			this.addUserEpoch(true);
		}
		Log.d("Data", "new tag: " + tag);
		setType(tag);
		Log.d("Data", "reset with message " + data.getString(key_tagtype)
				+ " and " + data.getStringArrayList(key_keyorder).size()
				+ " tokens");
	}

	// constructors===============================

	// transfer data------------------------------
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		for (String s : data.getStringArrayList(key_keyorder)) {
			if (s == key_time) {
				builder.append(data.getString(s) + " ");
			} else {
				builder.append(s + "=" + data.get(s) + " ");
			}
		}
		return builder.toString();
	}

	public Bundle getBundle() {
		return data;
	}

	public void dump(ObjectOutputStream stream) throws IOException {
		ArrayList<String> s = data.getStringArrayList(key_keyorder);
		stream.writeInt(s.size());
		Log.d("Data", "saving " + s.size() + " keys");

		for (String key : s) {
			stream.writeUTF(key);
		}

		for (String key : data.keySet()) {
			if (key != key_keyorder) {
				stream.writeUTF(key);
				if (key.compareTo(key_lon) == 0 || key.compareTo(key_lat) == 0) {
					stream.writeDouble(data.getDouble(key));
				} else {
					stream.writeUTF(data.getString(key));
				}
			}
		}
	}

	public void load(ObjectInputStream stream) throws IOException {
		ArrayList<String> s = data.getStringArrayList(key_keyorder);
		int size = stream.readInt();
		Log.d("Data", "loading " + size + " keys");
		s.ensureCapacity(size);
		for (int i = 0; i < size; i++) {
			s.add(stream.readUTF());
		}

		for (int i = 0; i < size; i++) {
			String key = stream.readUTF();
			if (key.compareTo(key_lon) == 0 || key.compareTo(key_lat) == 0) {
				data.putDouble(key, stream.readDouble());
			} else {
				data.putString(key, stream.readUTF());
			}
		}

	}

	// transfer data==============================

	// location-------------------------------------------------------
	public void setLatLon(double lat, double lon) {
		data.putDouble(key_lat, lat);
		data.getStringArrayList(key_keyorder).add(key_lat);
		data.putDouble(key_lon, lon);
		data.getStringArrayList(key_keyorder).add(key_lon);
		Log.e("Data", data.getStringArrayList(key_keyorder).toString());
	}

	public double latitude() {
		return data.getDouble(key_lat);
	}

	public double longitude() {
		return data.getDouble(key_lon);
	}

	// ===============================================================

	// type-----------------------------------------------------------
	public void setType(String value) {
		data.putString(key_tagtype, value);
		data.getStringArrayList(key_keyorder).add(key_tagtype);
	}

	public String getTye() {
		return data.getString(key_tagtype);
	}

	// type===========================================================

	// time-----------------------------------------------------------
	public String timestamp() {
		return data.getString(key_time);
	}

	public void setTime() {
		Calendar c = Calendar.getInstance();
		SimpleDateFormat format = new SimpleDateFormat(
				"kk:mm:ss:SSS  MM/dd/yyyy", Locale.getDefault());
		data.putString(key_time, format.format(c.getTime()));
		data.getStringArrayList(key_keyorder).add(key_time);
	}

	// time===========================================================

	// user data------------------------------------------------------
	public void addUserName(boolean include) {
		if (include) {
			data.putString(key_username, MainActivity.cswPreferences.getString(
					MainActivity.key_username, "Generic User"));
			data.getStringArrayList(key_keyorder).add(key_username);
		} else {
			data.remove(key_username);
			data.getStringArrayList(key_keyorder).remove(key_username);
		}
	}

	public void addUserEpoch(boolean include) {
		if (include) {
			data.putLong(key_userepoch, MainActivity.cswPreferences.getLong(
					MainActivity.key_userepoch, -1));
			data.getStringArrayList(key_keyorder).add(key_userepoch);
		} else {
			data.remove(key_userepoch);
			data.getStringArrayList(key_keyorder).remove(key_userepoch);
		}
	}

	public String userName() {
		return data.getString(key_username);
	}

	public double userEpoch() {
		return data.getDouble(key_userepoch);
	}
	// user data======================================================
}
