package com.DeltaCityLabs.CitySweep;

import java.util.ArrayList;
import java.util.Scanner;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.view.textservice.TextInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;

import com.example.idonteven.R;

public class ButtonEditor extends Activity implements OnClickListener{

	// varblok-----------------------------------
	public static final int return_tag = 12345;
	public static final String group_data_tag = "group_data";
	public static final String delim_tag = "delim";
	LinearLayout list;
	private String group;
	ArrayList<ButtonRow> bRowData;

	// varblok===================================

	// helpers-----------------------------------
	public class ButtonRow extends LinearLayout implements OnClickListener{
		// varblok--------------------------
		public EditText tag;
		public Button deleteButton;
		// varblok==========================

		public ButtonRow(Context context, String tagName) {
			// setup layout
			super(context);
			setOrientation(LinearLayout.HORIZONTAL);
			setGravity(Gravity.RIGHT);

			deleteButton = new Button(context);
			deleteButton.setText("Delete");
			deleteButton.setTextSize(10);
			deleteButton.setOnClickListener(this);

			tag = new EditText(context);
			tag.setText(tagName);
			tag.setTextSize(20);

			addView(tag);
			addView(deleteButton);
		}

		@Override
		public void onClick(View v) {
			Log.i("SettingsActivity", "deleteing " + tag.getText());
			LinearLayout parent = (LinearLayout) getParent();
			parent.removeView(this);
			bRowData.remove(this);
		}
	}

	// helpers===================================

	// Fragment----------------------------------
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.button_editor);
		list = (LinearLayout) findViewById(R.id.button_editor_layout);
		findViewById(R.id.done_button).setOnClickListener(this);
		
		findViewById(R.id.new_button_button).setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				ButtonRow row = new ButtonRow(ButtonEditor.this, "new button");
				list.addView(row, list.getChildCount() - 1);
				bRowData.add(row);
			}
		});
		
		bRowData = new ArrayList<ButtonEditor.ButtonRow>();
		
		Intent i = getIntent();
		String group = i.getExtras().getString(group_data_tag);
		String delim = i.getExtras().getString(delim_tag);
		
		Scanner s = new Scanner(group);
		s.useDelimiter("/");
		
		getActionBar().setTitle(s.next());
		getActionBar().setDisplayUseLogoEnabled(false);
		
		while(s.hasNext()){
			ButtonRow row = new ButtonRow(this, s.next());
			list.addView(row, list.getChildCount() - 1);
			bRowData.add(row);
		}
	}
	// Fragment==================================



	@Override
	public void onClick(View v) {
		StringBuilder builder = new StringBuilder();
		builder.append(getActionBar().getTitle().toString());
		
		for(ButtonRow row : bRowData){
			builder.append("/" + row.tag.getText().toString());
		}
		
		Intent i = new Intent();
		i.putExtra(ButtonEditor.group_data_tag, builder.toString());
		setResult(RESULT_OK, i);
		finish();
	}
}
