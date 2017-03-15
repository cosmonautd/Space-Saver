package com.cosmonautd.spacesaver.ui;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.AdapterView;
import android.widget.ListView;

import com.cosmonautd.spacesaver.baseclass.Directory;
import com.cosmonautd.spacesaver.baseclass.Event;
import com.cosmonautd.spacesaver.R;
import com.cosmonautd.spacesaver.Saver;
import com.cosmonautd.spacesaver.database.DBHelper;

import net.rdrei.android.dirchooser.DirectoryChooserConfig;
import net.rdrei.android.dirchooser.DirectoryChooserFragment;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;

public class SaverMainActivity extends AppCompatActivity
                                implements DirectoryChooserFragment.OnFragmentInteractionListener {

    public Activity activity = this;

    static private final int GET_READ_WRITE_PERMISSION_CODE = 0;
    static protected final byte INTENT_FROM_ADD_BUTTON_CODE = 1;
    static protected final byte INTENT_FROM_CARD_VIEW_CODE = 2;
    static protected final byte INTENT_FROM_CARD_VIEW_CACHE_CODE = 3;

    private DirectoryChooserFragment mDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_saver_main);

        Toolbar bar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(bar);

        ActionBar actionBar = getSupportActionBar();
        /*actionBar.setIcon(R.mipmap.ic_launcher);
        actionBar.setTitle("Space Saver");
        actionBar.setSubtitle("spaaaaaaaaace!");*/ // This makes it look like WhatsApp

        actionBar.setDisplayShowCustomEnabled(true);
        View cView = getLayoutInflater().inflate(R.layout.action_bar_layout, null);
        actionBar.setCustomView(cView);

        final DirectoryChooserConfig config = DirectoryChooserConfig.builder()
                .newDirectoryName("New Directory")
                .allowNewDirectoryNameModification(true)
                .build();

        mDialog = DirectoryChooserFragment.newInstance(config);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    if(checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED
                            || checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {

                        requestPermissions(new String[] {Manifest.permission.WRITE_EXTERNAL_STORAGE,
                            Manifest.permission.READ_EXTERNAL_STORAGE}, GET_READ_WRITE_PERMISSION_CODE);

                    } else {

                        mDialog.show(getFragmentManager(), null);

                    }
                } else {

                    mDialog.show(getFragmentManager(), null);
                }
            }
        });


        SharedPreferences sharedpreferences = getSharedPreferences("SaverPreferences", Context.MODE_PRIVATE);

        if(!sharedpreferences.contains("cleanCacheStartDate")) {

            SharedPreferences.Editor editor = sharedpreferences.edit();
            editor.putLong("cleanCacheStartDate", Calendar.getInstance().getTimeInMillis() + 1000*60);
            editor.putInt("cleanCachePeriod", 1000*60*60*12);
            editor.putInt("cleanCacheCycles", -1);
            editor.commit();

            Saver.setSaverCache(this, Calendar.getInstance().getTimeInMillis() + 1000*60, 1000*60*60*12);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        DBHelper database = DBHelper.getInstance(this);
        final Directory[] directoryArray = database.getAllDirectories();
        database.close();

        final CardArrayAdapter cardArrayAdapter =
                new CardArrayAdapter(getApplicationContext(), R.layout.card_layout);

        // TODO: Removing all ways for a user to reach cache clean page, keep code, look for solution
        //cardArrayAdapter.add(new Card("Cache", "Date not set", "Period not set"));

        final ArrayList directoryList = new ArrayList<>(Arrays.asList(directoryArray));

        if(directoryArray.length > 0) {

            Collections.sort(directoryList, new Event() { });

            for (int i = 0; i < directoryList.size(); i++) {

                String pathInfo = ((Directory) directoryList.get(i)).getPath();
                pathInfo = pathInfo.substring(pathInfo.lastIndexOf("/"));

                String dateInfo = ((Directory) directoryList.get(i)).getDateString();

                String periodInfo = "Clean every ";
                periodInfo += PeriodPickerFragment.getOption()
                        [Arrays.asList(PeriodPickerFragment.getOptionMs())
                        .indexOf(((Directory) directoryList.get(i)).getPeriod())];

                Card card = new Card(pathInfo, dateInfo, periodInfo);
                cardArrayAdapter.add(card);
            }
        }

        ListView obj = (ListView) findViewById(R.id.directories_list);
        obj.setAdapter(cardArrayAdapter);

        obj.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> arg0, View view, int pos, long arg3) {

                Intent intent = new Intent(activity, DirectoryManagerActivity.class);

                // TODO: This was replaced because Cache card in currently inactive
                /*if(pos > 0) {

                    intent.putExtra("INTENT_FROM", INTENT_FROM_CARD_VIEW_CODE);
                    intent.putExtra("itemId", ((Directory) directoryList.get(pos-1)).getId());

                } else {

                    intent.putExtra("INTENT_FROM", INTENT_FROM_CARD_VIEW_CACHE_CODE);
                }*/

                intent.putExtra("INTENT_FROM", INTENT_FROM_CARD_VIEW_CODE);
                intent.putExtra("itemId", ((Directory) directoryList.get(pos)).getId());

                startActivity(intent);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.menu_saver_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        if (id == R.id.action_about) {

            showAboutDialog();
            return true;

        } else if(id == R.id.action_other_libraries) {

            showOtherLibrariesDialog();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onSelectDirectory(@NonNull String path) {

        Snackbar.make(findViewById(R.id.directories_list), "You chose " + path, Snackbar.LENGTH_LONG)
                .setAction("Action", null).show();
        mDialog.dismiss();

        Intent intent = new Intent(this, DirectoryManagerActivity.class);
        intent.putExtra("INTENT_FROM", INTENT_FROM_ADD_BUTTON_CODE);
        intent.putExtra("PATH", path);
        startActivity(intent);
    }

    @Override
    public void onCancelChooser() {
        mDialog.dismiss();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {

        switch(requestCode) {
            case GET_READ_WRITE_PERMISSION_CODE: {
                if(grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    mDialog.show(getFragmentManager(), null);

                } else {

                    Snackbar.make(findViewById(R.id.directories_list), "Okay, fuck you then", Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();
                }
                return;
            }
        }
    }

    public void showAboutDialog() {

        FragmentManager fm = getSupportFragmentManager();
        AboutFragment newFragment = new AboutFragment();
        newFragment.show(fm, "ABOUT_DIALOG");
    }

    public void showOtherLibrariesDialog() {

        FragmentManager fm = getSupportFragmentManager();
        OtherLibrariesFragment newFragment = new OtherLibrariesFragment();
        newFragment.show(fm, "OTHER_LIBRARIES_DIALOG");
    }
}
