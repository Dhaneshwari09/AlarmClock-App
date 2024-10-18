package com.cscorner.alarmclock;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.widget.Button;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class MainActivityy extends Activity {

    private TextView currentTimeTextView, currentDateTextView;
    private Handler handler = new Handler();
    private Runnable timeUpdater;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_activityy);

        currentTimeTextView = findViewById(R.id.current_time);
        currentDateTextView = findViewById(R.id.current_date);
        Button setAlarmButton = findViewById(R.id.set_alarm_button);

        currentTimeTextView = findViewById(R.id.current_time);

        Button viewAlarmsButton = findViewById(R.id.view_alarms_button); // New button for viewing alarms

        // Update time and date
        timeUpdater = new Runnable() {
            @Override
            public void run() {
                Calendar calendar = Calendar.getInstance();
                SimpleDateFormat timeFormat = new SimpleDateFormat("hh:mm:ss a", Locale.getDefault());
                SimpleDateFormat dateFormat = new SimpleDateFormat("EEE, MMM d, yyyy", Locale.getDefault());
                currentTimeTextView.setText(timeFormat.format(calendar.getTime()));
                currentDateTextView.setText(dateFormat.format(calendar.getTime()));

                handler.postDelayed(this, 1000);
            }
        };
        handler.post(timeUpdater);

        // Navigate to AlarmActivity to set a new alarm
        setAlarmButton.setOnClickListener(v -> startActivity(new Intent(MainActivityy.this, AlarmActivity.class)));

        // Navigate to AlarmListActivity to view alarms
        viewAlarmsButton.setOnClickListener(v -> startActivity(new Intent(MainActivityy.this,AlarmsListActivity.class)));
    }

    @Override
    protected void onDestroy() {
        handler.removeCallbacks(timeUpdater);  // Clean up when activity is destroyed
        super.onDestroy();
}
}
