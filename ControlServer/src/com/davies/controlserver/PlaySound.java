package com.davies.controlserver;

import java.io.IOException;

import android.media.MediaPlayer;

public class PlaySound {
	static MediaPlayer mediaPlayer  = new MediaPlayer();
	public static void  paly(String path) {
		Operation.OpenScreen();
		stop();
		try {
			mediaPlayer.setDataSource(path);
			mediaPlayer.prepare();
			mediaPlayer.start();
		} catch (IllegalArgumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SecurityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalStateException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	public static void stop() {
		mediaPlayer.reset();
	}
}
