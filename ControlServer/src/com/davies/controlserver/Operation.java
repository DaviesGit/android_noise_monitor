package com.davies.controlserver;

import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;

import android.content.Context;
import android.os.PowerManager;

public class Operation {
	private static PowerManager.WakeLock wakeLockBright;
	private static int wakeLockBrightAcquireTime = 0;

	public static boolean powerManagerInit(PowerManager powerManager) {
		wakeLockBright = powerManager.newWakeLock(PowerManager.SCREEN_BRIGHT_WAKE_LOCK, "TAG");
		return true;
	}

	public static boolean OpenScreen() {
		ProcessBuilder processBuilder = new ProcessBuilder("su");
		processBuilder.directory(new File("/system/bin"));
		try {
			Process proc = processBuilder.start();
			DataOutputStream outputStream = new DataOutputStream(proc.getOutputStream());
			outputStream.writeBytes("input keyevent 3\n");
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}
		wakeLockBright.acquire();
		++wakeLockBrightAcquireTime;
		return true;
	}

	public static boolean CloseScreen() {
		if (0 == wakeLockBrightAcquireTime) {
			return false;
		}
		ProcessBuilder processBuilder = new ProcessBuilder("su");
		processBuilder.directory(new File("/system/bin"));
		try {
			Process proc = processBuilder.start();
			DataOutputStream outputStream = new DataOutputStream(proc.getOutputStream());
			outputStream.writeBytes("input keyevent 26\n");
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}
		wakeLockBright.release();
		--wakeLockBrightAcquireTime;
		return true;
	}
}
