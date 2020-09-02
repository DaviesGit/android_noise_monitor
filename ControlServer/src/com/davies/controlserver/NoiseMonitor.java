package com.davies.controlserver;

import java.util.ArrayList;
import java.util.Date;

import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.os.Environment;

public class NoiseMonitor {
	private static boolean whetherMonitor = false;
	public static boolean startMonitor() {
		whetherMonitor = true;
		new Thread(new MonitorThread()).start();
		return true;
	}
	
	public static void stopMonitor() {
		whetherMonitor = false;
	}

	private static class MonitorThread implements Runnable {
		static final int SAMPLE_RATE_IN_HZ = 8000;
		static final int BUFFER_SIZE = AudioRecord.getMinBufferSize(SAMPLE_RATE_IN_HZ, AudioFormat.CHANNEL_IN_DEFAULT,
				AudioFormat.ENCODING_PCM_16BIT);
		AudioRecord audioRecord;

		public void run() {
			audioRecord = new AudioRecord(MediaRecorder.AudioSource.MIC, SAMPLE_RATE_IN_HZ,
					AudioFormat.CHANNEL_IN_DEFAULT, AudioFormat.ENCODING_PCM_16BIT, BUFFER_SIZE);
			if (null == audioRecord) {
				LogWriter.logOut("初始化录音引擎失败!");
				return;
			}
			audioRecord.startRecording();
			boolean bool0 = false;
			boolean bool1 = false;
			boolean bool2 = false;
			double sample0 = 0;
			double sample1 = 0;
			double sample2= 0;
			while (whetherMonitor) {
				bool0 = bool1;
				bool1 = bool2;
				sample0 = sample1;
				sample1 = sample2;
				sample2 = getSample(audioRecord);
				if (2000 < sample2) {
					bool2 = true;
				} else {
					bool2 = false;
				}
				if (bool0 && bool1 && bool2) {
					LogWriter.logOut("Alarm trigger!" + "sample0:" + sample0 + "sample1:" + sample1 + "sample2:" + sample2);
					PlaySound.paly(Environment.getExternalStorageDirectory().getPath() + "/SystemMedia/warn1.wav");
					try {
						Thread.sleep(60 * 1000);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					Operation.CloseScreen();
				}
				try {
					Thread.sleep(3000);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}

		static double getSample(AudioRecord audioRecord) {
			Date begin = new Date();
			long nBegin = begin.getTime();
			ArrayList<Double> volume = new ArrayList<Double>();
			short[] buffer = new short[BUFFER_SIZE];
			int nCount;
			long v;
			double value;
			while (true) {
				nCount = audioRecord.read(buffer, 0, BUFFER_SIZE);
				v = 0;
				for (int i = 0; i < nCount; ++i) {
					v += buffer[i] * buffer[i];
				}
				value = v / nCount;
				if (100 > value) {
					continue;
				}
				volume.add(10 * Math.log10(value));
				Date end = new Date();
				long nEnd = end.getTime();
				if (2000 < (nEnd - nBegin)) {
					double retValue = 0;
					for (int n = 0; n < volume.size(); ++n) {
						retValue += volume.get(n);
					}
					return retValue;
				}
			}
		}
	}
}
