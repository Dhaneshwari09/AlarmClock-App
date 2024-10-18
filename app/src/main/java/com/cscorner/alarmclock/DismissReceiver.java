package com.cscorner.alarmclock;

import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

import androidx.annotation.NonNull;

public class DismissReceiver extends BroadcastReceiver {
// Handle dismiss logic
@Override
public void onReceive(Context context, Intent intent) {
    // Handle dismiss logic
    AlarmReceiver.stopRingtone();
    NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
    notificationManager.cancel(1); // Dismiss the notification
    Toast.makeText(context, "Alarm Dismissed", Toast.LENGTH_SHORT).show();
}

}
