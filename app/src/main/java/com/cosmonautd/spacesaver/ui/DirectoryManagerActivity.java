package com.cosmonautd.spacesaver.ui;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.support.design.widget.Snackbar;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.ListView;
import android.widget.TextView;

import com.cosmonautd.spacesaver.baseclass.Directory;
import com.cosmonautd.spacesaver.R;
import com.cosmonautd.spacesaver.Saver;
import com.cosmonautd.spacesaver.database.DBHelper;
import com.eralp.circleprogressview.CircleProgressView;
import com.eralp.circleprogressview.ProgressAnimationListener;

import java.util.Arrays;
import java.util.Calendar;

import static com.cosmonautd.spacesaver.database.DBHelper.DIRECTORIES_COLUMN_START_DATE;

public class DirectoryManagerActivity extends AppCompatActivity
                                        implements DatePickerFragment.DatePickerListener,
        TimePickerFragment.TimePickerListener,
        PeriodPickerFragment.PeriodPickerListener {

    private Directory directory = new Directory();
    private Saver saver = new Saver();

    private DBHelper database;

    private int PROGRESS_MAX = 100;

    private CircleProgressView circleProgressView;
    private TextView pathTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_directory_manager);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayOptions(getSupportActionBar().DISPLAY_SHOW_CUSTOM);
        actionBar.setDisplayShowHomeEnabled(false);
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeButtonEnabled(true);

        pathTextView = (TextView) findViewById(R.id.pathTextView);
        circleProgressView = (CircleProgressView) findViewById(R.id.circle_progress_view);
        circleProgressView.setTextEnabled(true);
        circleProgressView.setInterpolator(new AccelerateDecelerateInterpolator());

        Intent intent = getIntent();
        byte intentFromCode = intent.getByteExtra("INTENT_FROM", SaverMainActivity.INTENT_FROM_CARD_VIEW_CODE);

        if(intentFromCode == SaverMainActivity.INTENT_FROM_ADD_BUTTON_CODE) {

            this.directory.setPath(intent.getStringExtra("PATH"));
            pathTextView.setText(this.directory.getPath());

            showDateDialog();

        } else if(intentFromCode == SaverMainActivity.INTENT_FROM_CARD_VIEW_CODE) {

            this.directory.setId(intent.getIntExtra("itemId", -1));

            database = DBHelper.getInstance(this);
            Cursor cursor = database.getDirectoryData(this.directory.getId());
            cursor.moveToFirst();
            this.directory.setPath(cursor.getString(cursor.getColumnIndex(DBHelper.DIRECTORIES_COLUMN_PATH)));
            Calendar startDate = Calendar.getInstance();
            startDate.setTimeInMillis(cursor.getLong(cursor.getColumnIndex(DIRECTORIES_COLUMN_START_DATE)));
            this.directory.setDate(startDate);
            this.directory.setPeriod(Integer.parseInt(cursor.getString(cursor.getColumnIndex(DBHelper.DIRECTORIES_COLUMN_PERIOD))));
            this.directory.setCycles(Integer.parseInt(cursor.getString(cursor.getColumnIndex(DBHelper.DIRECTORIES_COLUMN_CYCLES))));

            cursor.close();
            database.close();

            Log.d(Saver.TAG, "PATH: " + directory.getPath() + "\n"
                                + "START_DATE: " + directory.getDateString() + "\n"
                                + "PERIOD: " + directory.getPeriod() + "\n"
                                + "CYCLES: " + directory.getCycles());

            pathTextView.setText(this.directory.getPath());

            String[] labels = new String[] {
                    "Start date",
                    "Delete time",
                    "Period",
                    "Cycles completed"};

            String[] data = new String[] {
                    String.format("%02d/%02d/%04d",
                            startDate.get(Calendar.DAY_OF_MONTH),
                            startDate.get(Calendar.MONTH) + 1,
                            startDate.get(Calendar.YEAR)),
                    String.format("%02d:%02d",
                            startDate.get(Calendar.HOUR_OF_DAY),
                            startDate.get(Calendar.MINUTE)),
                    PeriodPickerFragment.getOption()[Arrays.asList(PeriodPickerFragment.getOptionMs()).indexOf(this.directory.getPeriod())],
                    String.valueOf(this.directory.getCycles() > -2 ? this.directory.getCycles() : 0)};

            ListView listManager = (ListView) findViewById(R.id.list_manager);
            ListManagerAdapter adapter = new ListManagerAdapter(this, labels, data);
            listManager.setAdapter(adapter);
            listManager.setOnTouchListener(new View.OnTouchListener() {

                public boolean onTouch(View v, MotionEvent event) {
                    return (event.getAction() == MotionEvent.ACTION_MOVE);
                }
            });

            computeTimeUntilEndOfCycle(this.directory.getDate(), this.directory.getPeriod(), this.directory.getCycles());

        } else if(intentFromCode == SaverMainActivity.INTENT_FROM_CARD_VIEW_CACHE_CODE) {

            SharedPreferences sharedpreferences = getSharedPreferences("SaverPreferences", Context.MODE_PRIVATE);

            if(sharedpreferences.contains("cleanCacheStartDate")) {

                Calendar startDate = Calendar.getInstance();
                startDate.setTimeInMillis(sharedpreferences.getLong("cleanCacheStartDate", -1));


                String[] labels = new String[] {
                        "Start date",
                        "Delete time",
                        "Period",
                        "Cycles completed"};

                String[] data = new String[] {
                        String.format("%02d/%02d/%04d",
                                startDate.get(Calendar.DAY_OF_MONTH),
                                startDate.get(Calendar.MONTH) + 1,
                                startDate.get(Calendar.YEAR)),
                        String.format("%02d:%02d",
                                startDate.get(Calendar.HOUR_OF_DAY),
                                startDate.get(Calendar.MINUTE)),
                        String.valueOf(sharedpreferences.getInt("cleanCachePeriod", -1)),
                        String.valueOf(sharedpreferences.getInt("cleanCacheCycles", -1))};

                ListView listManager = (ListView) findViewById(R.id.list_manager);
                ListManagerAdapter adapter = new ListManagerAdapter(this, labels, data);
                listManager.setAdapter(adapter);
                listManager.setOnTouchListener(new View.OnTouchListener() {

                    public boolean onTouch(View v, MotionEvent event) {
                        return (event.getAction() == MotionEvent.ACTION_MOVE);
                    }
                });

                computeTimeUntilEndOfCycle(startDate,
                        sharedpreferences.getInt("cleanCachePeriod", -1),
                        sharedpreferences.getInt("cleanCacheCycles", -1));
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_directory_manager, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.

        switch (item.getItemId()) {

            case R.id.action_delete: {

                if(this.directory.getId() > -1) {

                    database = DBHelper.getInstance(this);
                    database.deleteDirectory(this.directory.getId());
                    saver.cancelSaver(this.getApplicationContext(), directory);
                    database.close();
                    finish();
                }
                else Snackbar.make(findViewById(R.id.space), "Weird error, invalid item id :0", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        }

        return super.onOptionsItemSelected(item);
    }

    public void computeTimeUntilEndOfCycle(Calendar startDate, int period, int cycles) {

        if(startDate.getTimeInMillis() < Calendar.getInstance().getTimeInMillis() && cycles > -1) {

            // Cycle counting has started

            Calendar nextEndOfCycle = Calendar.getInstance();

            Log.d(Saver.TAG, "variable nextEndOfCycle is " + nextEndOfCycle.getTime().toString());
            long absoluteCycle = (Calendar.getInstance().getTimeInMillis() - startDate.getTimeInMillis())/period;
            nextEndOfCycle.setTimeInMillis(startDate.getTimeInMillis() + period*(absoluteCycle+1));
            Log.d(Saver.TAG, "variable nextEndOfCycle is " + nextEndOfCycle.getTime().toString());

            final long diff = nextEndOfCycle.getTimeInMillis() - Calendar.getInstance().getTimeInMillis();

            double ratio = ((double) (period - diff)) / (period);
            int progressValue = (int) (ratio * PROGRESS_MAX);

            circleProgressView.setStartAngle(-90);
            circleProgressView.setProgressWithAnimation(progressValue, 2000);

            circleProgressView.addAnimationListener(new ProgressAnimationListener() {

                @Override
                public void onValueChanged(float v) { }

                @Override
                public void onAnimationEnd() {

                    try {

                        if (circleProgressView.getProgress() < PROGRESS_MAX)
                            circleProgressView.setProgressWithAnimation(PROGRESS_MAX, (int) diff - 2000);

                    } catch(IllegalArgumentException E) {
                        E.printStackTrace();
                        Log.e(Saver.TAG, "Value of variable diff: " + diff);
                    }
                }
            });

        } else {

            // Cycle counting has not started. TODO: make it visible that the cycle counting didn't start yet

            final long diff = startDate.getTimeInMillis() - Calendar.getInstance().getTimeInMillis();

        }
    }

    public void showDateDialog(){

        FragmentManager fm = getSupportFragmentManager();
        DatePickerFragment newFragment = new DatePickerFragment();
        newFragment.show(fm, "DATE_DIALOG");

    }

    public void showTimeDialog(){

        FragmentManager fm = getSupportFragmentManager();
        TimePickerFragment newFragment = new TimePickerFragment();
        newFragment.show(fm, "TIME_DIALOG");

    }

    public void showPeriodDialog() {

        FragmentManager fm = getSupportFragmentManager();
        PeriodPickerFragment newFragment = new PeriodPickerFragment();
        newFragment.show(fm, "PERIOD_DIALOG");
    }

    @Override
    public void onDatePositiveClick(DialogFragment dialog) {

        DatePickerFragment datePickerFragment = (DatePickerFragment) dialog;

        this.directory.getDate().set(Calendar.DAY_OF_MONTH, datePickerFragment.getSelectedDay());
        this.directory.getDate().set(Calendar.MONTH, datePickerFragment.getSelectedMonth());
        this.directory.getDate().set(Calendar.YEAR, datePickerFragment.getSelectedYear());

        showTimeDialog();
    }

    @Override
    public void onDateNegativeClick(DialogFragment dialog) {

        finish();
    }

    @Override
    public void onTimePositiveClick(DialogFragment dialog) {

        TimePickerFragment timePickerFragment = (TimePickerFragment) dialog;

        this.directory.getDate().set(Calendar.HOUR_OF_DAY, timePickerFragment.getSelectedHour());
        this.directory.getDate().set(Calendar.MINUTE, timePickerFragment.getSelectedMinute());
        showPeriodDialog();
    }

    @Override
    public void onTimeNegativeClick(DialogFragment dialog) {

        finish();
    }

    @Override
    public void onPeriodPositiveClick(DialogFragment dialog) {

        PeriodPickerFragment periodPickerFragment = (PeriodPickerFragment) dialog;

        database = DBHelper.getInstance(this);

        this.directory.setPeriod(periodPickerFragment.getPeriod());
        this.directory.setId((int) database.insertDirectory(this.directory.getPath(),
                                                            this.directory.getDate(),
                                                            this.directory.getPeriod(),
                                                            this.directory.getCycles()));

        database.close();

        saver.setSaver(this.getApplicationContext(), directory);

        if(this.directory.getId() > -1) {

            Snackbar.make(findViewById(R.id.space), "It was a success :)", Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show();
        } else {

            Snackbar.make(findViewById(R.id.space), "Oh, shit :(", Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show();
        }

        String[] labels = new String[] {
                "Start date",
                "Delete time",
                "Period",
                "Cycles completed"};

        String[] data = new String[] {
                String.format("%02d/%02d/%04d",
                        this.directory.getDate().get(Calendar.DAY_OF_MONTH),
                        this.directory.getDate().get(Calendar.MONTH) + 1,
                        this.directory.getDate().get(Calendar.YEAR)),
                String.format("%02d:%02d",
                        this.directory.getDate().get(Calendar.HOUR_OF_DAY),
                        this.directory.getDate().get(Calendar.MINUTE)),
                PeriodPickerFragment.getOption()[Arrays.asList(PeriodPickerFragment.getOptionMs()).indexOf(this.directory.getPeriod())],
                String.valueOf(this.directory.getCycles() > -2 ? this.directory.getCycles() : 0)};

        ListView listManager = (ListView) findViewById(R.id.list_manager);
        ListManagerAdapter adapter = new ListManagerAdapter(this, labels, data);
        listManager.setAdapter(adapter);
        listManager.setOnTouchListener(new View.OnTouchListener() {

            public boolean onTouch(View v, MotionEvent event) {
                return (event.getAction() == MotionEvent.ACTION_MOVE);
            }
        });

        computeTimeUntilEndOfCycle(this.directory.getDate(), this.directory.getPeriod(), this.directory.getCycles());
    }

    @Override
    public void onPeriodNegativeClick(DialogFragment dialog) {

        finish();
    }
}
