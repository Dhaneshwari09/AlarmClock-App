package com.cscorner.alarmclock;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class AlarmsListActivity extends Activity {

    private RecyclerView recyclerView;
    private AlarmListAdapter adapter;
    private List<AlarmModel> alarmList;
    private static final String ALARM_PREFS = "AlarmPreferences";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alarms_list);

        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Load the saved alarms
        loadAlarms();

        // Set up the RecyclerView adapter
        adapter = new AlarmListAdapter(alarmList, this);
        recyclerView.setAdapter(adapter);
    }

    // Method to load saved alarms from SharedPreferences
    private void loadAlarms() {
        SharedPreferences sharedPreferences = getSharedPreferences(ALARM_PREFS, MODE_PRIVATE);
        Gson gson = new Gson();
        String json = sharedPreferences.getString("AlarmList", null);
        Type type = new TypeToken<List<AlarmModel>>() {}.getType();
        alarmList = json != null ? gson.fromJson(json, type) : new ArrayList<>();
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Reload the alarms whenever the activity resumes (in case any alarm was added/deleted)
        loadAlarms();
        adapter.notifyDataSetChanged(); // Notify adapter that data has changed
}
}
