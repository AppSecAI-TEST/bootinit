package com.example.bootinit;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;

public class BootReceiver extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {
		Intent in = new Intent();
		in.setComponent(new ComponentName("com.example.bootinit", "com.example.bootinit.MainActivity"));
		in.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		context.startActivity(in);
		
	}

}
