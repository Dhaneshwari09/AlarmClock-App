package com.cscorner.alarmclock;

import static android.content.Context.MODE_PRIVATE;



import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class AlarmListAdapter extends RecyclerView.Adapter<AlarmListAdapter.AlarmViewHolder> {

    private List<AlarmModel> alarmList;
    private Context context;
    private AlarmManager alarmManager;

    public AlarmListAdapter(List<AlarmModel> alarmList, Context context) {
        this.alarmList = alarmList;
        this.context = context;
        this.alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
    }


    @NonNull
    @Override
    public AlarmViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.alarm_item, parent, false);
        return new AlarmViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AlarmViewHolder holder, int position) {
//        AlarmModel alarm = alarmList.get(position);
//        holder.timeTextView.setText(String.format("%02d:%02d", alarm.getHour(), alarm.getMinute()));
//
//        holder.deleteButton.setOnClickListener(v -> {
//            deleteAlarm(position);
//        });
        AlarmModel alarm = alarmList.get(position);

        // Create a Calendar object to determine AM/PM
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, alarm.getHour());
        calendar.set(Calendar.MINUTE, alarm.getMinute());

        // Get the hour in 12-hour format and the AM/PM indicator
        int hour = calendar.get(Calendar.HOUR);
        if (hour == 0) {
            hour = 12;  // Adjust for midnight/noon
        }
        String amPm = calendar.get(Calendar.AM_PM) == Calendar.AM ? "AM" : "PM";

        // Display the time in 12-hour format with AM/PM
        holder.timeTextView.setText(String.format(Locale.getDefault(), "%02d:%02d %s", hour, alarm.getMinute(), amPm));

        holder.deleteButton.setOnClickListener(v -> {
            deleteAlarm(position);
});

    }

    @Override
    public int getItemCount() {
        return alarmList.size();
    }

    public class AlarmViewHolder extends RecyclerView.ViewHolder {

        TextView timeTextView;
        ImageButton deleteButton;

        public AlarmViewHolder(@NonNull View itemView) {
            super(itemView);
            timeTextView = itemView.findViewById(R.id.timeTextView);
            deleteButton = itemView.findViewById(R.id.deleteButton); // Delete button instead of toggle
        }
    }

    private void deleteAlarm(int position) {
        AlarmModel alarm = alarmList.get(position);

        // Cancel the scheduled alarm
        Intent intent = new Intent(context, AlarmReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(
                context,
                alarm.getId(),  // Assuming each alarm has a unique ID
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );
        alarmManager.cancel(pendingIntent);  // Cancel the alarm

        // Remove the alarm from the list
        alarmList.remove(position);

        // Notify the adapter about the removed item
        notifyItemRemoved(position);

        // Save the updated alarm list
        saveAlarms();

        Toast.makeText(context, "Alarm Deleted", Toast.LENGTH_SHORT).show();
    }
    // Save the alarm list to SharedPreferences
    private void saveAlarms() {
        SharedPreferences sharedPreferences = context.getSharedPreferences("AlarmPreferences", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        Gson gson = new Gson();
        String json = gson.toJson(alarmList);
        editor.putString("AlarmList", json);
        editor.apply();
}
}