/*
 * John Rice, DeltaCityLabs 2013
 */
package com.DeltaCityLabs.Fragments;

import java.io.IOException;
import java.util.List;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.location.Address;
import android.location.Geocoder;
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
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;

import com.DeltaCityLabs.CitySweep.Data;
import com.DeltaCityLabs.CitySweep.MainActivity;
import com.example.idonteven.R;

public class ReportFragment extends Fragment {
	// helper classes-----------------------------------------------------------
	public interface reportListener {
		public void onRecieveReport(Data d);
	}

	private class reporter implements DialogInterface.OnClickListener {

		@Override
		public void onClick(DialogInterface dialog, int which) {
			if (d == null) {
				Log.e("Report", "Null data pointer. No data");
				return;
			}

			// alert to a new message
			if (listener != null) {
				listener.onRecieveReport(d);
			}
			Log.d("Report", "Adding selected report to transmiter");
		}

	}

	private class canceler implements DialogInterface.OnClickListener {
		@Override
		public void onClick(DialogInterface dialog, int which) {
		}
	}

	private class clicker implements OnClickListener {
		private String internalTag;

		public clicker(Button b) {
			internalTag = (String) b.getText();
		}

		@Override
		public void onClick(View v) {
			// set tag
			d.setupEventReport(internalTag);
			// set location
			try {
				Location l = lm
						.getLastKnownLocation(LocationManager.GPS_PROVIDER);
				d.setLatLon(l.getLatitude(), l.getLongitude());
			} catch (Exception e) {
				d.setLatLon(0, 0);

			}
			Log.d("Report", d.toString());

			String addr;
			
			try {
				if (Geocoder.isPresent()) {
					addr = getAddress(d.latitude(), d.longitude());
				} else {
					throw new Exception("no geocoder present");
				}
			} catch (Exception e) {
				Log.e("Report", "could not load address " + e.toString());
				addr = "Latitude: " + d.latitude() + "\nLongitude: "
						+ d.longitude() + "\n";
			}

			//set user data
			if(MainActivity.cswPreferences.getBoolean(MainActivity.key_sendmetrics, false)){
			}
			
			builder.setTitle(d.getTye());
			builder.setMessage("Report a " + d.getTye() + " at\n" + addr);
			builder.setPositiveButton("Ok", new reporter());
			builder.setNegativeButton("Cancel", new canceler());
			AlertDialog ad = builder.create();
			ad.show();

		}

	}

	private class customTag implements OnEditorActionListener {

		@Override
		public boolean onEditorAction(TextView textview, int arg1, KeyEvent arg2) {
			// set tag
			d.setupEventReport(textview.getText().toString());
			// set location
			try {
				Location l = lm
						.getLastKnownLocation(LocationManager.GPS_PROVIDER);
				d.setLatLon(l.getLatitude(), l.getLongitude());
			} catch (Exception e) {
				d.setLatLon(0, 0);
			}
			Log.d("Report", d.toString());

			builder.setTitle(d.getTye());
			builder.setMessage("Report a " + d.getTye() + " at\n" + "latitude: "
					+ d.latitude() + "\n" + "longitude: " + d.longitude() + "\n");
			builder.setPositiveButton("Ok", new reporter());
			builder.setNegativeButton("Cancel", new canceler());
			AlertDialog ad = builder.create();
			ad.show();
			return false;
		}

	}

	// helper classes===========================================================
	// varblok--------------------------
	private LocationManager lm;
	private Geocoder geo;
	private AlertDialog.Builder builder;
	private Data d;
	public reportListener listener;
	// varblok==========================

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		d = new Data();
		lm = (LocationManager) getActivity().getSystemService(
				Context.LOCATION_SERVICE);
		geo = new Geocoder(getActivity());
		builder = new AlertDialog.Builder(getActivity());

		View v = inflater.inflate(R.layout.report_generator, container, false);

		for (View button : v.getTouchables()) {
			if (button instanceof Button) {
				button.setOnClickListener(new clicker((Button) button));
			} else if (button instanceof EditText) {
				((EditText) button).setOnEditorActionListener(new customTag());
			}
		}

		return v;
	}

	private String getAddress(double latitude, double longitude)
			throws IOException {
		List<Address> list = geo.getFromLocation(latitude, longitude, 1);
		StringBuilder sb = new StringBuilder("Address:\n");

		if (list != null && list.size() > 0) {
			Address a = list.get(0);
			for (int i = 0; i < a.getMaxAddressLineIndex(); i++) {
				sb.append("\t");
				sb.append(a.getAddressLine(i));
				sb.append("\n");
			}
		} else {
			return ("Latitude: " + d.latitude() + "\nLongitude: " + d.longitude() + "\n");
		}

		return sb.toString();
	}

}
