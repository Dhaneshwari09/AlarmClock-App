package com.cscorner.alarmclock;

import android.app.AlarmManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.util.Log;
import android.widget.Toast;

import androidx.core.app.NotificationCompat;

import java.util.Calendar;

public class AlarmReceiver extends BroadcastReceiver {

    private static Ringtone ringtone;
    private static AlarmManager alarmManager;
    @Override
    public void onReceive(Context context, Intent intent) {

        // retrive alarm tone URI
        String alarmToneUri = intent.getStringExtra("ALARM_TONE");

        // Check if alarmToneUri is not null
        if (alarmToneUri == null) {
           Log.e("AlarmReceiver", "ALARM_TONE is null");
            return; // Exit if the URI is null to prevent the app from crashing
        }

        Uri alarmTone = Uri.parse(alarmToneUri);

        // Create a snooze intent
        Intent snoozeIntent = new Intent(context, SnoozeReceiver.class);
        snoozeIntent.putExtra("ALARM_TONE", alarmToneUri); // Pass the alarm tone URI back

        PendingIntent snoozePendingIntent = PendingIntent.getBroadcast(
                context,
                0,
                snoozeIntent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );

        // Create a dismiss intent
        Intent dismissIntent = new Intent(context, DismissReceiver.class);
        PendingIntent dismissPendingIntent = PendingIntent.getBroadcast(
                context,
                1,
                dismissIntent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );

        // Build a notification with snooze and dismiss actions
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, "ALARM_CHANNEL")
                .setContentTitle("Alarm is Ringing!")
//                .setContentText("Your alarm is going off!")
                .setSmallIcon(R.drawable.baseline_access_alarm_24)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setAutoCancel(true)
                .addAction(R.drawable.ic_snooze, "Snooze", snoozePendingIntent) // Snooze button
                .addAction(R.drawable.ic_dismiss, "Dismiss", dismissPendingIntent); // Dismiss button

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel("ALARM_CHANNEL", "Alarm Notifications", NotificationManager.IMPORTANCE_HIGH);
            notificationManager.createNotificationChannel(channel);
        }

        notificationManager.notify(1, builder.build());

        // Play the alarm tone
        try {

            if (ringtone == null) {
                ringtone = RingtoneManager.getRingtone(context, alarmTone);
                ringtone.setLooping(true);
                ringtone.play();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
        // Method to stop the ringtone
        public static void stopRingtone () {
          //  Ringtone ringtone = null;
            if (ringtone != null && ringtone.isPlaying()) {
                ringtone.stop();
               ringtone = null;
                // Reset the ringtone to null after stopping it
            }
        }
    }