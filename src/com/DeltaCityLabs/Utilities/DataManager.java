/*
 * John Rice, DeltaCityLabs 2013
 */
package com.DeltaCityLabs.Utilities;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.util.ArrayList;

import android.annotation.SuppressLint;
import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.util.Log;

import com.DeltaCityLabs.CitySweep.Data;
import com.example.idonteven.R;

public class DataManager extends Service {
	// helper class---------------------
	private class Spinner extends Thread {
		public boolean spin = true;
		public static final long delay = 10000;
		public static final long connectDealy = 60000;
		
		@Override
		public void run() {
			super.run();

			// connect to server
			while (spin && dataConnection == null) {
				try {
					InetAddress i = InetAddress.getByName(getResources()
							.getString(R.string.str_server_adress));
					dataConnection = new Socket(i, getResources().getInteger(
							R.integer.socket_address));
					outputPipe = dataConnection.getOutputStream();
					dataConnection.getInputStream();
					Log.d("Spinner", "connection established");
				} catch (Exception e) {
					try {
						Log.e("Spinner", "could not establish connection");
						dataConnection = null;
						sleep(connectDealy);
					} catch (InterruptedException e1) {
					}
				}
			}
			if (dataConnection == null) {
				return;
			}

			// send data
			while (spin) {
				sendReport();
				try {
					outputPipe.flush();
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				try {
					sleep(delay);
				} catch (InterruptedException e) {
					spin = false;
				}
			}

			// disconnect
			try {
				outputPipe = null;
				dataConnection.close();
			} catch (Exception e) {
				Log.e("Spinner", "Could not close connection");
			}
		}
	}

	@SuppressLint("HandlerLeak")
	private class handler extends Handler {
		@Override
		public void handleMessage(Message msg) {
			data.add(new Data(msg.getData()));
			sendReport();
			if (data.size() > 0) {
				Log.d("Message", data.get(data.size() - 1).toString());
			}
		}
	}
	//helper classes===================
	//varblok--------------------------
	private static final String outputfilename = "dataStorage";
	private ArrayList<Data> data;
	private Spinner s;
	private Socket dataConnection;
	private Messenger dataStream;
	public OutputStream outputPipe;
	// varblok==========================

	@Override
	public void onCreate() {
		super.onCreate();
		// create resources
		s = new Spinner();
		dataStream = new Messenger(new handler());
		data = new ArrayList<Data>();
		// fill data
		try {
			FileInputStream file = openFileInput(outputfilename);
			ObjectInputStream inputStrm = new ObjectInputStream(file);
			data = new ArrayList<Data>();
			
			int size = inputStrm.readInt();
			Log.i("Service", "Loading " + size + " instances");
			Data b;
			
			for(int i = 0; i < size; i++){
				b = new Data();
				b.load(inputStrm);
			}
			
			inputStrm.close();
			file.close();
		} catch (FileNotFoundException e1) {
			Log.e("Service", "no such file");
			data = new ArrayList<Data>();
		} catch (IOException e) {
			Log.e("Service", "file io error" + e.toString());
			data = new ArrayList<Data>();
		}

		// start bg thread
		s.start();

		Log.d("service", "Data manager created");
	}

	@Override
	public void onDestroy() {
		Log.d("service", "Data manager destroyed");
		s.spin = false;

		// dump data
		try {
			FileOutputStream file = openFileOutput(outputfilename, MODE_PRIVATE);
			ObjectOutputStream outputStrm = new ObjectOutputStream(file);
			
			outputStrm.writeInt(data.size());
			Log.i("Service", "Saving " + data.size() + " instances");
			
			for (Data d : data) {
				d.dump(outputStrm);
			}
			
			outputStrm.close();
			file.close();
		} catch (FileNotFoundException e) {
			Log.e("Service", "could not open file");
		} catch (Exception e) {
			Log.e("Service", "some io error " + e.getMessage());
		}
		try {
			s.join();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		super.onDestroy();
	}

	@Override
	public IBinder onBind(Intent i) {
		Log.d("service", "Data manager bound");
		return dataStream.getBinder();
	}

	public void sendReport() {
		if (dataConnection != null) {
			if (data.size() > 0) {
				try {
					outputPipe.write(data.get(0).toString().getBytes());
					Log.d("Service", "data sent: " + data.get(0).toString());
					data.remove(0);
				} catch (IOException e) {
					Log.e("Service", "well... shit");
				}
			}
		}
	}
}
