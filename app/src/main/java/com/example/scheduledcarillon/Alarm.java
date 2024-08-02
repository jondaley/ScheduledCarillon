package com.example.scheduledcarillon;

import static android.content.Context.ALARM_SERVICE;

import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.util.Log;

import androidx.core.content.ContextCompat;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;

public class Alarm extends BroadcastReceiver {
    enum AlarmType{
        DAILY_START,
        STOP,
        SUNDAY_START,

        UNKNOWN
    }

    static DateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");
    // set this to true to make times shorter so you don't have to wait as long
    protected static boolean debug = false;

    protected static MainActivity activity = null;

    public static void setActivity(MainActivity ma){ activity = ma; }

    protected static long nextSundayTime = -1;

    protected static long nextDailyTime = -1;

    public static String getNextScheduledPlayTime() {
        if(nextSundayTime == -1 && nextDailyTime == -1)
            return "Nothing scheduled yet";
        if(nextSundayTime == -1)
            return dateFormat.format(nextDailyTime);
        if(nextDailyTime == -1)
            return dateFormat.format(nextSundayTime);
        if(nextSundayTime < nextDailyTime)
            return dateFormat.format(nextSundayTime);;
        return dateFormat.format(nextDailyTime);
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.i(Globals.LOG_TAG, System.currentTimeMillis() + " in AlarmReceiver onReceive()");
        String newString;
        Bundle extras = intent.getExtras();
        if(extras == null)
            newString= null;
        else{
            newString = extras.getString("operation");
            Log.i(Globals.LOG_TAG, "Operation is: "+ newString);
            switch(getAlarmType(newString)) {
                case DAILY_START:
                    scheduleStopAlarm();
                    scheduleDailyAlarm();
                    activity.setResourcesWithMusic(false);
                    activity.playMusic();
                    break;

                case SUNDAY_START:
                    scheduleStopAlarm();
                    scheduleSundayAlarm();
                    activity.setResourcesWithMusic(false);
                    activity.playMusic();
                    break;

                case STOP:
                    activity.stop(false);
                    break;

                case UNKNOWN:
                    // TODO: What??
                    break;
            }
        }
    }

    public static String getAlarmName(AlarmType at){
        switch(at){
            case DAILY_START:
                return "DailyStart";
            case STOP:
                return "Stop";
            case SUNDAY_START:
                return "SundayStart";
        }
        return "Unknown";
    }

    public static AlarmType getAlarmType(String alarmName){
        switch(alarmName){
            case "DailyStart":
                return AlarmType.DAILY_START;
            case "Stop":
                return AlarmType.STOP;
            case "SundayStart":
                return AlarmType.SUNDAY_START;
        }
        return AlarmType.UNKNOWN;
    }

    public static int getAlarmType(AlarmType at){
        switch(at){
            case DAILY_START:
                return 0;
            case STOP:
                return 1;
            case SUNDAY_START:
                return 2;
        }
        return -1;
    }

    public static void scheduleDailyAlarm() {
        AlarmManager alarmManager = (AlarmManager) activity.getSystemService(ALARM_SERVICE);
        Intent startIntent = new Intent(activity, Alarm.class);
        startIntent.putExtra("operation", getAlarmName(AlarmType.DAILY_START));

        Calendar cal = new GregorianCalendar();

        if(debug) {
            cal.setTimeInMillis(System.currentTimeMillis() + 1 * 60 * 1000);
        }
        else {
            // Get the next 4PM timer
            cal.set(Calendar.HOUR_OF_DAY, 16);
            cal.set(Calendar.MINUTE, 10);
            cal.set(Calendar.SECOND, 0);
            cal.set(Calendar.MILLISECOND, 0);
        }
        // if it is already after 4PM, set the timer for tomorrow
        if(cal.getTimeInMillis() < System.currentTimeMillis())
            cal.add(Calendar.DAY_OF_MONTH, 1);
        nextDailyTime = cal.getTimeInMillis();

        Log.i(Globals.LOG_TAG, "Next 4PM:" + dateFormat.format(nextDailyTime));

        PendingIntent pendingIntent = PendingIntent.getBroadcast(activity, getAlarmType(AlarmType.DAILY_START),
                startIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        AlarmManager.AlarmClockInfo ac = new AlarmManager.AlarmClockInfo(nextDailyTime, null);
        alarmManager.setAlarmClock(ac, pendingIntent);
    }

    public static void scheduleSundayAlarm() {
        AlarmManager alarmManager = (AlarmManager) activity.getSystemService(ALARM_SERVICE);
        Intent startIntent = new Intent(activity, Alarm.class);
        startIntent.putExtra("operation", getAlarmName(AlarmType.SUNDAY_START));

        Calendar cal = nextSunday();

        if(debug) {
            // "Sunday" will occur in 1 minutes
            cal.setTimeInMillis(System.currentTimeMillis() + (activity.isNineThirtyChecked() ? 15 : 30) * 1000);
        }
        else {
            // Get the next Sunday morning timer
            cal.set(Calendar.HOUR_OF_DAY, activity.isNineThirtyChecked() ? 9 : 10);
            cal.set(Calendar.MINUTE, 10);
            cal.set(Calendar.SECOND, 0);
            cal.set(Calendar.MILLISECOND, 0);
        }
        nextSundayTime = cal.getTimeInMillis();
        Log.i(Globals.LOG_TAG, "Next Sunday:" + dateFormat.format(nextSundayTime));

        PendingIntent pendingIntent = PendingIntent.getBroadcast(activity, getAlarmType(AlarmType.SUNDAY_START),
                startIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        AlarmManager.AlarmClockInfo ac = new AlarmManager.AlarmClockInfo(nextSundayTime, null);
        alarmManager.setAlarmClock(ac, pendingIntent);
    }

    protected static void scheduleStopAlarm() {
        AlarmManager alarmManager = (AlarmManager) activity.getSystemService(ALARM_SERVICE);
        Intent stopIntent = new Intent(activity, Alarm.class);
        stopIntent.putExtra("operation", getAlarmName(AlarmType.STOP));

        // we play for 15 minutes by default
        long delay = System.currentTimeMillis() + 15 * 60 * 1000;

        // Only wait 20 seconds in debug mode
        if(debug)
            delay = System.currentTimeMillis() + 20 * 1000;

        Log.i(Globals.LOG_TAG, "Set Stop:" + dateFormat.format(delay));

        PendingIntent pendingIntent = PendingIntent.getBroadcast(activity, getAlarmType(AlarmType.STOP),
                stopIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        AlarmManager.AlarmClockInfo ac = new AlarmManager.AlarmClockInfo(delay, null);
        alarmManager.setAlarmClock(ac, pendingIntent);
    }

    protected static Calendar nextSunday() {
        Calendar date = Calendar.getInstance();
        int diff = 1-date.get(Calendar.DAY_OF_WEEK);
        if(diff <= 0)
            diff += 7;
        date.add(Calendar.DAY_OF_MONTH, diff);
        return date;
    }
}
