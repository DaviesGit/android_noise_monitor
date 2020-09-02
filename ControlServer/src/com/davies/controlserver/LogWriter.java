package com.davies.controlserver;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import android.annotation.SuppressLint;
import android.os.Environment;

public class LogWriter {
	@SuppressLint("SimpleDateFormat")
	public synchronized static void logOut(String log) {
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
		Date now = new Date();
		String date = dateFormat.format(now);
		File logFile = new File(Environment.getExternalStorageDirectory().getPath() + "/ControlServerLog/ControlServerLog.txt");
		if (!logFile.exists()) {
			File directory = new File(Environment.getExternalStorageDirectory().getPath() + "/ControlServerLog");
			if (!directory.exists()) {
				if (!directory.mkdir()) {
					return;
				}
			} else if (!directory.isDirectory()) {
				return;
			}
			try {
				logFile.createNewFile();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				return;
			}
		}
		try {
			FileWriter logFileWriter = new FileWriter(logFile, true);
			logFileWriter.write(date + "---" + log + "\r\n");
			logFileWriter.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return;
		}
	}
}
