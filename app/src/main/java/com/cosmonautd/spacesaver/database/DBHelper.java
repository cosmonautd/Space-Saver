package com.cosmonautd.spacesaver.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteDatabase;

import com.cosmonautd.spacesaver.baseclass.Directory;

import java.util.Calendar;

public class DBHelper extends SQLiteOpenHelper {

    private static DBHelper instance = null;

    public static final String DATABASE_NAME = "SpaceSaver.db";
    public static final String DIRECTORIES_TABLE_NAME = "directories";
    public static final String DIRECTORIES_COLUMN_ID = "id";
    public static final String DIRECTORIES_COLUMN_PATH = "path";
    public static final String DIRECTORIES_COLUMN_START_DATE = "start_date";
    public static final String DIRECTORIES_COLUMN_PERIOD = "period";
    public static final String DIRECTORIES_COLUMN_CYCLES = "cycles";


    public static DBHelper getInstance(Context context) {
        if(instance == null) instance = new DBHelper(context.getApplicationContext());
        return instance;
    }

    public DBHelper(Context context) {
        super(context, DATABASE_NAME , null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        db.execSQL(
                "create table " + DIRECTORIES_TABLE_NAME + " (" +
                        DIRECTORIES_COLUMN_ID + " integer primary key, " +
                        DIRECTORIES_COLUMN_PATH + " text, " +
                        DIRECTORIES_COLUMN_START_DATE + " integer, " +
                        DIRECTORIES_COLUMN_PERIOD + " integer, " +
                        DIRECTORIES_COLUMN_CYCLES + " integer" +
                        ")"
        );
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        db.execSQL("DROP TABLE IF EXISTS " + DIRECTORIES_TABLE_NAME);
        onCreate(db);
    }

    public long insertDirectory(String path, Calendar startDate, int period, int cycles) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();

        contentValues.put(DIRECTORIES_COLUMN_PATH, path);
        contentValues.put(DIRECTORIES_COLUMN_START_DATE, startDate.getTimeInMillis());
        contentValues.put(DIRECTORIES_COLUMN_PERIOD, period);
        contentValues.put(DIRECTORIES_COLUMN_CYCLES, cycles);

        long output = db.insert(DIRECTORIES_TABLE_NAME, null, contentValues);

        db.close();
        return output;
    }

    public Cursor getDirectoryData(long id){
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery( "select * from " + DIRECTORIES_TABLE_NAME +" where " + DIRECTORIES_COLUMN_ID + "="+id+"", null );
    }

    public int numberOfDirectories(){
        SQLiteDatabase db = this.getReadableDatabase();
        int output = (int) DatabaseUtils.queryNumEntries(db, DIRECTORIES_TABLE_NAME);
        db.close();
        return output;
    }

    public boolean updateDirectory(int id, String path, Calendar startDate, int period, int cycles) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(DIRECTORIES_COLUMN_PATH, path);
        contentValues.put(DIRECTORIES_COLUMN_START_DATE, startDate.getTimeInMillis());
        contentValues.put(DIRECTORIES_COLUMN_PERIOD, period);
        contentValues.put(DIRECTORIES_COLUMN_CYCLES, cycles);
        db.update(DIRECTORIES_TABLE_NAME, contentValues, "id = ? ", new String[] { Integer.toString(id) } );
        db.close();
        return true;
    }

    public Integer deleteDirectory(long id) {
        SQLiteDatabase db = this.getWritableDatabase();
        int output = db.delete(DIRECTORIES_TABLE_NAME, "id = ? ", new String[] { Long.toString(id) });
        db.close();
        return output;
    }

    public Directory[] getAllDirectories() {
        Directory[] directoryList = new Directory[numberOfDirectories()];

        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.rawQuery("SELECT * from " + DIRECTORIES_TABLE_NAME, null);
        cursor.moveToFirst();

        for (int i = 0; i < directoryList.length; i++) {
            int id = Integer.parseInt(cursor.getString(cursor.getColumnIndex(DIRECTORIES_COLUMN_ID)));
            String path = cursor.getString(cursor.getColumnIndex(DIRECTORIES_COLUMN_PATH));
            Calendar startDate = Calendar.getInstance();
            startDate.setTimeInMillis(cursor.getLong(cursor.getColumnIndex(DIRECTORIES_COLUMN_START_DATE)));
            int period = Integer.parseInt(cursor.getString(cursor.getColumnIndex(DIRECTORIES_COLUMN_PERIOD)));
            int cycles = Integer.parseInt(cursor.getString(cursor.getColumnIndex(DIRECTORIES_COLUMN_CYCLES)));
            directoryList[i] = new Directory(id, path, startDate, period, cycles);
            cursor.moveToNext();
        }

        cursor.close();
        db.close();
        return directoryList;
    }
}