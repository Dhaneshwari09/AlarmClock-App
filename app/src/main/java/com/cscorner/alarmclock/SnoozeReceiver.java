package com.cscorner.alarmclock;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

import java.util.Calendar;

public class SnoozeReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {

        AlarmReceiver.stopRingtone();

        // Handle snooze logic
        // For example, snooze the alarm for 10 minutes
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Intent snoozeIntent = new Intent(context, AlarmReceiver.class);
        snoozeIntent.putExtra("ALARM_TONE", intent.getStringExtra("ALARM_TONE")); // Pass the alarm tone URI
        PendingIntent snoozePendingIntent = PendingIntent.getBroadcast(
                context,
                0,
                snoozeIntent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );

        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.MINUTE, 1); // Snooze for 10 minutes

        if (alarmManager != null) {
            alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), snoozePendingIntent);
        }

        Toast.makeText(context, "Alarm Snoozed for 1 minutes", Toast.LENGTH_SHORT).show();

    }
}
