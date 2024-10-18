package com.cscorner.alarmclock;

import java.io.Serializable;

public class AlarmModel  {

private int id;
    private int hour;
    private int minute;
    private String ringtoneUri;
    private boolean isEnabled;  // This could be useful if you later want to have alarms enabled/disabled

    public AlarmModel(int id, int hour, int minute, String ringtoneUri, boolean isEnabled) {
        this.id = id;
        this.hour = hour;
        this.minute = minute;
        this.ringtoneUri = ringtoneUri;
        this.isEnabled = isEnabled;
    }


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getHour() {
        return hour;
    }

    public void setHour(int hour) {
        this.hour = hour;
    }

    public int getMinute() {
        return minute;
    }

    public void setMinute(int minute) {
        this.minute = minute;
    }

    public String getRingtoneUri() {
        return ringtoneUri;
    }

    public void setRingtoneUri(String ringtoneUri) {
        this.ringtoneUri = ringtoneUri;
    }

    public boolean isEnabled() {
        return isEnabled;
    }

    public void setEnabled(boolean enabled)

    {
      isEnabled = enabled;
    }

    public String getFormattedTime() {
        return String.format("%02d:%02d", hour, minute);
}
}
