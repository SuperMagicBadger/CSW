package com.DeltaCityLabs.CitySweep;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.util.LinkedList;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

import com.DeltaCityLabs.Fragments.NetworkFragment;
import com.example.idonteven.R;

public class NetworkService extends Service {

	// varblok-------------------------
	// internals
	public NetworkFragment fragment;
	public LocalBinder binder;
	public LinkedList<String> queue;
	// networking
	public boolean connected;
	private Socket socket;
	private OutputStream ostream;
	// threading
	public boolean run;
	NetworkThread thread;

	// varblok=========================

	// helpers-------------------------
	public class LocalBinder extends Binder {
		public NetworkService getService() {
			return NetworkService.this;
		}
	}

	// helpers=========================

	// threading-----------------------
	private class NetworkThread extends Thread {
		@Override
		public void run() {
			// connect to net
			while (run && !connect()) {
				try {
					sleep(100);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}

			while (run) {
				Log.i("NetworkServiceThread", "Spin...");

				// check for empty queue
				while (queue.isEmpty()) {
					try {
						synchronized (queue) {
							Log.i("NetworkServiceThread", "waiting on queue");
							queue.wait();
						}
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}

				// send some data
				try {
					ostream.write(queue.peek().getBytes());
					queue.pop();
				} catch (IOException e) {
					Log.e("NetworkServiceThread",
							"could not send data: " + e.getMessage());
					e.printStackTrace();
				}
			}
		}
	}

	// threading=======================

	// fragment management-------------
	@Override
	public void onCreate() {
		Log.i("NetworkService", "netservice created");
		super.onCreate();

		binder = new LocalBinder();
		queue = new LinkedList<String>();

		connected = false;
		run = true;

		thread = new NetworkThread();
		thread.start();
	}

	@Override
	public void onDestroy() {
		Log.i("NetworkService", "netservice destroyed");
		run = false;
		super.onDestroy();
	}

	@Override
	public IBinder onBind(Intent intent) {
		return binder;
	}

	// fragment management=============

	// service methods-----------------
	public boolean connect() {
		if (!connected) {
			InetAddress address;
			try {
				address = InetAddress.getByName(getResources().getString(
						R.string.str_server_adress));

				socket = new Socket(address, getResources().getInteger(
						R.integer.socket_address));

				ostream = socket.getOutputStream();

				connected = true;
				Log.i("NetworkService", "connected");
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return connected;
	}

	public void disconnect() throws IOException {
		if (connected) {
			connected = false;
			ostream.close();
			socket.close();
		}
	}

	public synchronized void send(String message) {
		synchronized (queue) {
			queue.add(message);
			queue.notifyAll();
			Log.i("NetworkService", "notifying queue");
		}
	}
	// service methods=================

}
