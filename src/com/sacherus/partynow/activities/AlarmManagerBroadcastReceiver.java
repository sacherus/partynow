package com.sacherus.partynow.activities;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.PowerManager;
import android.support.v4.app.NotificationCompat;

import com.sacherus.partynow.R;
import com.sacherus.partynow.rest.RestApi;

public class AlarmManagerBroadcastReceiver extends BroadcastReceiver {


	@Override
	public void onReceive(Context context, Intent intent) {
		PowerManager pm = (PowerManager) context
				.getSystemService(Context.POWER_SERVICE);
		PowerManager.WakeLock wl = pm.newWakeLock(
				PowerManager.PARTIAL_WAKE_LOCK, "YOUR TAG");

	// Acquire the lock
		wl.acquire();
		// Toast.makeText(context, "Checking for new parties...",
		// Toast.LENGTH_LONG).show();
		createNotification(context);
		RestApi.i().getPartiesInArea(RestApi.i().getKms());
		wl.release();

	}

	public void SetAlarm(Context context) {
		AlarmManager am = (AlarmManager) context
				.getSystemService(Context.ALARM_SERVICE);
		Intent intent = new Intent(context, AlarmManagerBroadcastReceiver.class);
		PendingIntent pi = PendingIntent.getBroadcast(context, 0, intent, 0);
		// After after 30 seconds
		am.setRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis(),
				RestApi.i().getInterval(), pi);
	}

	private void createNotification(Context context) {
		Intent intent = new Intent(context, Parties.class);
		PendingIntent pi = PendingIntent.getActivity(context, 0,
	            intent, 0);
		// After after 30 seconds
		
		Notification notification = new NotificationCompat.Builder(context)
				.setContentTitle("PartyNow")
				.setContentText("There are "+RestApi.i().getPartiesInArea()+" parties within "+RestApi.i().getKms()+" km")
				.setSmallIcon(R.drawable.ic_launcher).setContentIntent(pi)
				.build();
		
		NotificationManager notificationManager = (NotificationManager) context
				.getSystemService(context.NOTIFICATION_SERVICE);
		notification.flags |= Notification.FLAG_AUTO_CANCEL;
		notificationManager.notify(0, notification);
		
	}
	
	

}
