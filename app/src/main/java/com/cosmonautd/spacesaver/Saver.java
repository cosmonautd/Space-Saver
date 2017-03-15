package com.cosmonautd.spacesaver;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.util.Log;

import com.cosmonautd.spacesaver.baseclass.Directory;
import com.cosmonautd.spacesaver.database.DBHelper;

import java.io.File;
import java.lang.reflect.Method;
import java.util.Calendar;

/**
 * Created by David Borges on 09/03/17.
 */

public class Saver extends BroadcastReceiver {

    public static final String TAG = "SpaceSaver";

    @Override
    public void onReceive(Context context, Intent intent) {

        int id = intent.getIntExtra("ID", -1);

        if(id > -1) {

            DBHelper database = DBHelper.getInstance(context);
            Directory directory = new Directory();
            directory.setId(id);

            Cursor cursor = database.getDirectoryData(directory.getId());
            cursor.moveToFirst();
            directory.setPath(cursor.getString(cursor.getColumnIndex(DBHelper.DIRECTORIES_COLUMN_PATH)));
            Calendar startDate = Calendar.getInstance();
            startDate.setTimeInMillis(cursor.getLong(cursor.getColumnIndex(DBHelper.DIRECTORIES_COLUMN_START_DATE)));
            directory.setDate(startDate);
            directory.setPeriod(Integer.parseInt(cursor.getString(cursor.getColumnIndex(DBHelper.DIRECTORIES_COLUMN_PERIOD))));
            directory.setCycles(Integer.parseInt(cursor.getString(cursor.getColumnIndex(DBHelper.DIRECTORIES_COLUMN_CYCLES))));

            cursor.close();

            Log.d(TAG, "Saver " + id + " activated. Current time: " + Calendar.getInstance().getTime().toString());
            Log.d(TAG, "Cleaning " + directory.getPath() + ". Current time: " + Calendar.getInstance().getTime().toString());

            int nDirs = 0;

            File dir = new File(directory.getPath());
            if (dir.isDirectory()) {
                String[] children = dir.list();
                for (int i = 0; i < children.length; i++) {
                    File content = new File(dir, children[i]);
                    if(content.isDirectory()) {
                        nDirs++;
                    } else {
                        content.delete();
                    }
                }
            }

            if ((new File(directory.getPath()).list().length <= nDirs)) {

                Log.d(TAG, "Directory " + directory.getPath() + " cleaned. Current time: " + Calendar.getInstance().getTime().toString());
                database.updateDirectory(
                        directory.getId(),
                        directory.getPath(),
                        directory.getDate(),
                        directory.getPeriod(),
                        directory.getCycles() + 1);

            } else
                Log.d(TAG, "Couldn't clean " + directory.getPath() + ". Current time: " + Calendar.getInstance().getTime().toString());

            database.close();

        } else if(id == -100) {

            Log.d(Saver.TAG, "Trying to clean cache");

            // TODO: The Following code does not work. It seems Android M and above doesn't allow an unprivileged app to clean cache.

            PackageManager packageManager = context.getPackageManager();
            Method[] methods = packageManager.getClass().getDeclaredMethods();
            for(Method m : methods) {
                if(m.getName().equals("freeStorage")) {
                    try {
                        long desiredFreeStorage = 8*1024*1024*1024;
                        m.invoke(packageManager, desiredFreeStorage, null);
                        SharedPreferences sharedpreferences = context.getSharedPreferences("SaverPreferences", Context.MODE_PRIVATE);
                        SharedPreferences.Editor editor = sharedpreferences.edit();
                        editor.putInt("cleanCacheCycles", sharedpreferences.getInt("cleanCacheCycles", -1) + 1);
                        editor.commit();
                    } catch(Exception E) {
                        E.printStackTrace();
                        Log.e(Saver.TAG, "Couldn't clean cache. I'm sorry");
                    }

                    break;
                }
            }
        }
    }

    public static void setSaver(Context context, Directory directory) {

        Calendar now = Calendar.getInstance();

        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(context, Saver.class);
        intent.putExtra("ID", directory.getId());
        PendingIntent alarmIntent = PendingIntent.getBroadcast(context, directory.getId(), intent, PendingIntent.FLAG_UPDATE_CURRENT);

        alarmManager.setRepeating(AlarmManager.RTC, directory.getDate().getTimeInMillis(), directory.getPeriod(), alarmIntent);

        Log.d(TAG, "Saver started. Current time: " + now.getTime().toString());
    }

    public static void setSaverUpdateStart(Context context, Directory directory, Calendar newStart) {

        Calendar now = Calendar.getInstance();

        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(context, Saver.class);
        intent.putExtra("ID", directory.getId());
        PendingIntent alarmIntent = PendingIntent.getBroadcast(context, directory.getId(), intent, PendingIntent.FLAG_UPDATE_CURRENT);

        alarmManager.setRepeating(AlarmManager.RTC, newStart.getTimeInMillis(), directory.getPeriod(), alarmIntent);

        Log.d(TAG, "Saver started. Current time: " + now.getTime().toString());
    }

    public static void setSaverCache(Context context, long startTimeMillis, int period) {

        Calendar now = Calendar.getInstance();

        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(context, Saver.class);
        intent.putExtra("ID", -100);
        PendingIntent alarmIntent = PendingIntent.getBroadcast(context, -100, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        alarmManager.setRepeating(AlarmManager.RTC, startTimeMillis, period, alarmIntent);

        Log.d(TAG, "Saver started. Current time: " + now.getTime().toString());
    }

    public static void cancelSaver(Context context, Directory directory) {

        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(context, Saver.class);
        intent.putExtra("ID", directory.getId());
        PendingIntent alarmIntent = PendingIntent.getBroadcast(context, directory.getId(), intent, PendingIntent.FLAG_UPDATE_CURRENT);
        alarmManager.cancel(alarmIntent);

        Calendar now = Calendar.getInstance();
        Log.d(TAG, "Saver canceled. Current time: " + now.getTime().toString());
    }
}
