package com.DeltaCityLabs.Fragments;

import java.util.ArrayList;

import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.DeltaCityLabs.CitySweep.ButtonEditor;
import com.DeltaCityLabs.CitySweep.MainActivity;
import com.DeltaCityLabs.Utilities.ReportLoader;
import com.example.idonteven.R;

public class SetingsFragment extends Fragment implements OnClickListener {

	// varblok-----------------------------------
	private ArrayList<GroupRow> gRowData;
	private LinearLayout list;
	private Button addRowButton;
	private Builder bd;

	// varblok===================================

	// helpers-----------------------------------

	public class GroupRow extends LinearLayout implements OnClickListener {

		// varblok--------------------------
		TextView tag;
		Button setButton;
		Button deleteButton;
		Button editButton;

		// varblok==========================

		public GroupRow(Context context, String tagName) {
			// setup layout
			super(context);
			setOrientation(LinearLayout.HORIZONTAL);
			setGravity(Gravity.RIGHT);

			// add items
			setButton = new Button(context);
			setButton.setText("Use");
			setButton.setTextSize(10);
			setButton.setOnClickListener(this);

			deleteButton = new Button(context);
			deleteButton.setText("Delete");
			deleteButton.setTextSize(10);
			deleteButton.setOnClickListener(this);

			editButton = new Button(context);
			editButton.setText("Edit");
			editButton.setTextSize(10);
			editButton.setOnClickListener(this);

			tag = new TextView(context);
			tag.setText(tagName);
			tag.setTextSize(20);

			addView(tag);
			addView(setButton);
			addView(editButton);
			addView(deleteButton);
		}

		@Override
		public void onClick(View v) {
			if (v == setButton) {
				Log.i("SettingsActivity", "setting " + tag.getText());
				MainActivity.generator.buttonLayout = tag.getText().toString();
			} else if (v == deleteButton) {
				Log.i("SettingsActivity", "deleteing " + tag.getText());
				LinearLayout parent = (LinearLayout) getParent();
				parent.removeView(this);
				MainActivity.networkFragmnet.loader.removeButtonGroup(tag.getText().toString());
			} else if (v == editButton) {
				Log.i("SettingsActivity", "editing " + tag.getText());
				Intent i = new Intent(getActivity(), ButtonEditor.class);
				i.putExtra(ButtonEditor.group_data_tag, MainActivity.networkFragmnet.loader.groupString(tag.getText().toString()));
				getActivity().startActivityForResult(i, ButtonEditor.return_tag);
			}
		}

	}

	// helpers===================================

	// generators--------------------------------
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		gRowData = new ArrayList<GroupRow>();
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View v = inflater.inflate(R.layout.settings, container, false);

		list = (LinearLayout) v.findViewById(R.id.settings_list_layout);
		addRowButton = (Button) v.findViewById(R.id.new_row_button);
		addRowButton.setOnClickListener(this);
		Log.i("heya", "checkit");
		return v;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		bd = new Builder(getActivity());
		bd.setTitle("New Set");
		bd.setMessage("name the new set");
	}

	public void clear() {
		Log.i("SettingsFrag", "clear...");
		for (GroupRow r : gRowData) {
			list.removeView(r);
		}
		gRowData.clear();
	}

	public void refresh() {
		clear();
		for (String s : MainActivity.networkFragmnet.loader.buttonSets.keySet()) {
			GroupRow r = new GroupRow(getActivity(), s);
			gRowData.add(r);
			list.addView(r, list.getChildCount() - 2);
		}
	}
	// generators================================

	@Override
	public void onClick(View v) {
		GroupRow newrow = new GroupRow(getActivity(), "Set "
				+ (list.getChildCount() - 2));
		list.addView(newrow, list.getChildCount() - 2);
		MainActivity.networkFragmnet.loader.addSet(newrow.tag.getText()
				.toString());
	}
}
