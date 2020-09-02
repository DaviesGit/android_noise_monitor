package com.davies.controlserver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Environment;
import android.widget.Toast;

public class Testbroadcast extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {
		// TODO Auto-generated method stub
		
		Toast.makeText(context, "hello,this just a test broadcast!", Toast.LENGTH_SHORT).show();
		Intent serviceIntent = new Intent(context, ControlService.class);
		context.startService(serviceIntent);
	
		
	}

}
