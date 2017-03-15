package com.cosmonautd.spacesaver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.cosmonautd.spacesaver.baseclass.Directory;
import com.cosmonautd.spacesaver.database.DBHelper;

import java.util.Calendar;

/**
 * Created by drgn on 11/03/17.
 */

public class SaverReboot extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {

        if(intent.getAction().equals("android.intent.action.BOOT_COMPLETED")) {

            DBHelper database = DBHelper.getInstance(context);
            final Directory[] directoryArray = database.getAllDirectories();
            database.close();

            if(directoryArray.length > 0)

                for (Directory directory: directoryArray) {

                    if(directory.getDate().getTimeInMillis() > Calendar.getInstance().getTimeInMillis()) {
                        Saver.setSaver(context, directory);
                    }
                    else {

                        long newStartMillis = directory.getDate().getTimeInMillis() + (
                                1 + ((Calendar.getInstance().getTimeInMillis() - directory.getDate().getTimeInMillis()))/directory.getPeriod())
                                * directory.getPeriod();

                        Calendar newStart = Calendar.getInstance();
                        newStart.setTimeInMillis(newStartMillis);

                        Saver.setSaverUpdateStart(context, directory, newStart);
                    }
                }
        }
    }
}
