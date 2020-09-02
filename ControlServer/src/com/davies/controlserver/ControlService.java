package com.davies.controlserver;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.os.PowerManager;
import android.widget.Toast;

public class ControlService extends Service {

	private static boolean whetherServerRun = false;
	private static Handler handler = new Handler();
	private static PowerManager powerManager;
	private static PowerManager.WakeLock wakeLockWake;
//	private static KeyguardManager keyguardManager;
//	private static KeyguardLock keyguardLock;
	private static ServerSocket serverSocket = null;
	private static List<Socket> mList = new ArrayList<Socket>();
	private static ExecutorService mExecutorService = null;

	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return null;
	}

	public void onCreate() {
		// prevent system sleep
		powerManager = (PowerManager) getSystemService(Context.POWER_SERVICE);
		Operation.powerManagerInit(powerManager);
		wakeLockWake = powerManager.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "TAG");
		wakeLockWake.acquire();
		Toast.makeText(this, "control service create!", Toast.LENGTH_SHORT).show();

	}

	public void onDestroy() {
		wakeLockWake.release();
		Toast.makeText(this, "control service destory", Toast.LENGTH_SHORT).show();
	}

	public int onStartCommand(Intent intent, int flags, int startId) {
		if (whetherServerRun) {
			return START_REDELIVER_INTENT;
		} else {
			whetherServerRun = true;
		}
		//StartNoiseMonitro
		NoiseMonitor.startMonitor();
		// Timing event
		new Thread(new Runnable() {
			public void run() {
				do {
					try {
						Thread.sleep(3 * 1000);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}

				} while (true);
			}
		}).start();

		// net work server
		new Thread(new Runnable() {
			public void run() {
				try {
					serverSocket = new ServerSocket(8899);
					mExecutorService = Executors.newCachedThreadPool();
					notice("server has tarted!");
					Socket client = null;
					while (true) {
						client = serverSocket.accept();
						mList.add(client);
						mExecutorService.execute(new ClientService(client));
						notice("a client has enter");
					}
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					notice("cannot start server!");
					return;
				}
			}
		}).start();

		return START_REDELIVER_INTENT;

	}

	// notice
	public void notice(final String noticeText) {
		handler.post(new Runnable() {
			public void run() {
				Toast.makeText(ControlService.this, noticeText, Toast.LENGTH_SHORT).show();
			}
		});
	}

	class ClientService implements Runnable {
		private Socket client;
		private char[] buffer = new char[8192];
		private InputStream inputStream = null;
		private OutputStream outputStream = null;
		private OutputStreamWriter outputStreamWriter = null;
		private InputStreamReader inputStreamReader = null;
		private BufferedWriter bufferedWriter = null;
		private BufferedReader bufferedReader = null;
		private BufferedInputStream bufferedInputStream = null;
		private BufferedOutputStream bufferedOutputStream = null;

		public ClientService(Socket client) {
			this.client = client;
		}

		@Override
		public void run() {
			// TODO Auto-generated method stub
			try {
				inputStream = client.getInputStream();
				bufferedInputStream = new BufferedInputStream(inputStream);
				inputStreamReader = new InputStreamReader(inputStream);
				bufferedReader = new BufferedReader(inputStreamReader);
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			try {
				outputStream = client.getOutputStream();
				bufferedOutputStream = new BufferedOutputStream(outputStream);
				outputStreamWriter = new OutputStreamWriter(outputStream);
				bufferedWriter = new BufferedWriter(outputStreamWriter);
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			int nCount = 0;
			byte[] bufferByte = new byte[8192];
			String returnMsg = null;
			while (true) {
				/*
				 * the data's structure is the first 20 bytes is head and others
				 * is body head type is /{xxxxrrrrnnnnnnnnnn xxxx is a command
				 * rrrr is a remarks nnnnnnnnnn is a data length
				 */

				try {
					nCount = bufferedInputStream.read(bufferByte);
					if (-1 == nCount) {
						break;
					}
					if (20 >= nCount) {
						continue;
					}
					String headString = new String(bufferByte, 0, 20);
					if (!(headString.substring(0, 2)).equals("/{")) {
						continue;
					}
					if (headString.substring(2, 6).equals("cmd ")) {
						String cmdString = new String(bufferByte, 20, nCount -20);

						if (doCommand(cmdString)) {
							returnMsg = "deal data successful!";
						} else {
							returnMsg = "deal data failed!";
						}
					}

				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				try {
					bufferedWriter.write(returnMsg);
					bufferedWriter.flush();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			notice("a client has exit!");
		}

		private final boolean doCommand(String command) {
			if (command.equals("OpenScreen")) {
				return Operation.OpenScreen();
			} else if (command.equals("CloseScreen")) {
				return Operation.CloseScreen();
			} else {
				return false;
			}
			
		}
	}
}
