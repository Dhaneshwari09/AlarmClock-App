package com.cscorner.alarmclock;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.SharedPreferences;
import android.icu.text.MessagePattern;
import android.icu.text.SimpleDateFormat;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class AlarmActivity extends Activity {

    private TimePicker timePicker;
    private Uri selectedToneUri;
    private List<AlarmModel> alarmList;
    private AlarmManager alarmManager;
    private static final int TONE_PICKER_REQUEST = 1;
    private static final String ALARM_PREFS = "AlarmPreferences";



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alarm);

        timePicker = findViewById(R.id.timePicker);
        Button chooseToneButton = findViewById(R.id.choose_tone);
        Button saveAlarmButton = findViewById(R.id.save_alarm_button);

        alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);

        // Load the saved alarms
        loadAlarms();

        // Load saved ringtone or set default alarm tone
        selectedToneUri = getSavedRingtoneUri();
        if (selectedToneUri == null) {
            selectedToneUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM);
        }

        chooseToneButton.setOnClickListener(v -> {
            Intent intent = new Intent(RingtoneManager.ACTION_RINGTONE_PICKER);
            intent.putExtra(RingtoneManager.EXTRA_RINGTONE_TYPE, RingtoneManager.TYPE_ALARM);
            intent.putExtra(RingtoneManager.EXTRA_RINGTONE_SHOW_DEFAULT, true);
            intent.putExtra(RingtoneManager.EXTRA_RINGTONE_SHOW_SILENT, true);
            if (selectedToneUri != null) {
                intent.putExtra(RingtoneManager.EXTRA_RINGTONE_TITLE, selectedToneUri);
            }
            startActivityForResult(intent, TONE_PICKER_REQUEST);
        });

        saveAlarmButton.setOnClickListener(v -> setAlarm());
    }

    private void setAlarm() {
        int hour = timePicker.getHour();
        int minute = timePicker.getMinute();

        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, hour);
        calendar.set(Calendar.MINUTE, minute);
        calendar.set(Calendar.SECOND, 0);

        // If time has passed for today, set for next day
        if (calendar.getTimeInMillis() < System.currentTimeMillis()) {
            calendar.add(Calendar.DAY_OF_YEAR, 1);
        }
        int alarmId = generateAlarmId();
        AlarmModel newAlarm = new AlarmModel(alarmId, hour, minute, selectedToneUri.toString(), true);
        alarmList.add(newAlarm);
        saveAlarms();

        Intent intent = new Intent(this, AlarmReceiver.class);
        intent.putExtra("ALARM_TONE", selectedToneUri.toString());

        PendingIntent pendingIntent = PendingIntent.getBroadcast(
                this, alarmId, intent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);

        if (alarmManager != null) {
            alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);
        }

        Toast.makeText(this, "Alarm Set!", Toast.LENGTH_SHORT).show();
        finish();  // Close the activity after saving the alarm
    }

    private int generateAlarmId() {
        return alarmList.size() > 0 ? alarmList.get(alarmList.size() - 1).getId() + 1 : 1;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == TONE_PICKER_REQUEST && resultCode == RESULT_OK) {
            selectedToneUri = data.getParcelableExtra(RingtoneManager.EXTRA_RINGTONE_PICKED_URI);
            if (selectedToneUri != null) {
                saveRingtoneUri(selectedToneUri);
            } else {
                Toast.makeText(this, "No Ringtone Selected", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void saveRingtoneUri(Uri ringtoneUri) {
        SharedPreferences sharedPreferences = getSharedPreferences(ALARM_PREFS, MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("RingtoneUri", ringtoneUri.toString());
        editor.apply();
    }

    private Uri getSavedRingtoneUri() {
        SharedPreferences sharedPreferences = getSharedPreferences(ALARM_PREFS, MODE_PRIVATE);
        String uriString = sharedPreferences.getString("RingtoneUri", null);
        return uriString != null ? Uri.parse(uriString) : null;
    }
    // Method to save the alarm list in SharedPreferences
    private void saveAlarms() {
        SharedPreferences sharedPreferences = getSharedPreferences(ALARM_PREFS, MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        Gson gson = new Gson();
        String json = gson.toJson(alarmList);
        editor.putString("AlarmList", json);
        editor.apply();
    }

    // Method to load saved alarms from SharedPreferences
    private void loadAlarms() {
        SharedPreferences sharedPreferences = getSharedPreferences(ALARM_PREFS, MODE_PRIVATE);
        Gson gson = new Gson();
        String json = sharedPreferences.getString("AlarmList", null);
        Type type = new TypeToken<List<AlarmModel>>() {
        }.getType();
        alarmList = json != null ? gson.fromJson(json, type) : new ArrayList<>();
    }
}