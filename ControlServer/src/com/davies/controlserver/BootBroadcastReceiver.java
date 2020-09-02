package com.davies.controlserver;

import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

public class BootBroadcastReceiver extends BroadcastReceiver {

	public void onReceive(Context context, Intent intent) {
		if (intent.getAction().equals("android.intent.action.BOOT_COMPLETED")) {
			ProcessBuilder processBuilder = new ProcessBuilder("su");
			processBuilder.directory(new File("/system/bin"));
//set the debug port 
			try {
				Process proc = processBuilder.start();
				DataOutputStream outputStream = new DataOutputStream(proc.getOutputStream());
				outputStream.writeBytes("setprop service.adb.tcp.port 5566\n");
				outputStream.writeBytes("stop adbd\n");
				outputStream.writeBytes("start adbd\n");
			} catch (IOException e) {
				e.printStackTrace();
			}
//start the service
			Intent serviceIntent = new Intent(context, ControlService.class);
			context.startService(serviceIntent);
			Toast.makeText(context, "boot finish! and set finish!", Toast.LENGTH_SHORT).show();
		}
	}
}
