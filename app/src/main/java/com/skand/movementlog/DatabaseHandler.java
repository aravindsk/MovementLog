package com.skand.movementlog;


import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by user on 22-06-2017.
 */

public class DatabaseHandler extends SQLiteOpenHelper {

    // All Static variables
    // Database Version
    private static final int DATABASE_VERSION = 7;

    // Database Name
    private static final String DATABASE_NAME = "c25k";

    // activityLog table name
    private static final String TABLE_ACTIVITY_LOG = "activityLog";

    // activityLog Table Columns names
    private static final String KEY_ID = "id";
    private static final String KEY_EXERCISE_ID = "exercise_id";
    private static final String KEY_ACTIVITY_ID = "activity_id";
    private static final String KEY_ACTIVITY_TS = "activity_ts";
    private static final String KEY_LAT_LNG = "lat_lng";
    private static final String KEY_CREATION_TS = "creation_ts";


    public DatabaseHandler(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);

    }

    // Creating Tables
    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_ACTIVITY_LOG_TABLE = "CREATE TABLE " + TABLE_ACTIVITY_LOG + "("
                + KEY_ID + " INTEGER  PRIMARY KEY,"
                + KEY_EXERCISE_ID + " INTEGER , "
                + KEY_ACTIVITY_ID + " INTEGER , "
                + KEY_ACTIVITY_TS + " TEXT,"
                + KEY_LAT_LNG + " TEXT, "
                + KEY_CREATION_TS + " TEXT"
                + ")";
        db.execSQL(CREATE_ACTIVITY_LOG_TABLE);


    }

    // Upgrading database
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Drop older table if existed
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_ACTIVITY_LOG);

        // Create tables again
        onCreate(db);

    }


    // Adding new contact
    public void addExerciseLog(ExerciseLog exerciseLog) {

        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_EXERCISE_ID, exerciseLog.getExercise_id());
        values.put(KEY_ACTIVITY_ID, exerciseLog.getActivity_id());
        values.put(KEY_ACTIVITY_TS, exerciseLog.getActivity_ts());
        values.put(KEY_LAT_LNG, exerciseLog.getLatLng());
        values.put(KEY_CREATION_TS, exerciseLog.getCreation_Ts());

        // Inserting Row
        db.insert(TABLE_ACTIVITY_LOG, null, values);
        db.close(); // Closing database connection


    }

    // Getting single contact
    public ExerciseLog getExerciseLog(int id) {


        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(TABLE_ACTIVITY_LOG, new String[]{KEY_ID, KEY_EXERCISE_ID,KEY_ACTIVITY_ID,
                        KEY_ACTIVITY_TS, KEY_LAT_LNG, KEY_CREATION_TS}, KEY_ACTIVITY_ID + "=?",
                new String[]{String.valueOf(id)}, null, null, null, null);
        if (cursor != null)
            cursor.moveToFirst();

        ExerciseLog exerciseLog = new ExerciseLog(Integer.parseInt(cursor.getString(0)),
                Integer.parseInt(cursor.getString(1)), Integer.parseInt(cursor.getString(2)), cursor.getString(3), cursor.getString(4), cursor.getString(5));
        // return contact
        return exerciseLog;

    }

    // Getting All Contacts
    public List<ExerciseLog> getAllExerciseLog() {


        List<ExerciseLog> exerciseLogList = new ArrayList<ExerciseLog>();
        // Select All Query
        String selectQuery = "SELECT  * FROM " + TABLE_ACTIVITY_LOG;

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);


        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {

                ExerciseLog exerciseLog = new ExerciseLog();
                //exerciseLog.setID(Integer.parseInt(cursor.getString(0)));
                exerciseLog.setExercise_id(Integer.parseInt(cursor.getString(1)));
                exerciseLog.setActivity_id(Integer.parseInt(cursor.getString(2)));
                exerciseLog.setActivity_ts(cursor.getString(3));
                exerciseLog.setLatLng(cursor.getString(4));
                exerciseLog.setCreation_Ts(cursor.getString(5));
                // Adding contact to list
                exerciseLogList.add(exerciseLog);
            } while (cursor.moveToNext());
        }
        cursor.close();
        // return contact list
        return exerciseLogList;

    }
    // Getting All Contacts
    public List<ExerciseLog> getExerciseDetails(int exercise_id) {


        List<ExerciseLog> exerciseLogList = new ArrayList<ExerciseLog>();
        // Select All Query
        String selectQuery = "SELECT  * FROM " + TABLE_ACTIVITY_LOG+" where exercise_id = "+String.valueOf( exercise_id);

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);


        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {

                ExerciseLog exerciseLog = new ExerciseLog();
                //exerciseLog.setID(Integer.parseInt(cursor.getString(0)));
                exerciseLog.setExercise_id(Integer.parseInt(cursor.getString(1)));
                exerciseLog.setActivity_id(Integer.parseInt(cursor.getString(2)));
                exerciseLog.setActivity_ts(cursor.getString(3));
                exerciseLog.setLatLng(cursor.getString(4));
                exerciseLog.setCreation_Ts(cursor.getString(5));
                // Adding contact to list
                exerciseLogList.add(exerciseLog);
            } while (cursor.moveToNext());
        }
        cursor.close();
        // return contact list
        return exerciseLogList;

    }

    // Getting contacts Count
    public int getExerciseLogCount() {

        String countQuery = "SELECT  * FROM " + TABLE_ACTIVITY_LOG;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(countQuery, null);
        cursor.close();

        // return count
        return cursor.getCount();

    }

    public String[][] getActivitySummary() {
    //Remove hardcoded array size
        String[][] x = new String[100][2];
        int i = 0, j = 0;
        String activitySummaryQuery = "SELECT  exercise_id,min(creation_ts) act_id FROM " + TABLE_ACTIVITY_LOG + " group by exercise_id order by exercise_id desc LIMIT 100 ";
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(activitySummaryQuery, null);

        cursor.moveToFirst();

        while (!cursor.isAfterLast()) {

            Log.d("DB",String.valueOf(cursor.getInt(0)));
            Log.d("DB",String.valueOf(cursor.getString(1)));

            x[i][0] = String.valueOf(cursor.getInt(0));
            x[i][1] = String.valueOf(cursor.getString(1));

            i++;
            cursor.moveToNext();

        }
        cursor.close();

        return x;
    }

    public int getNewActivityID() {
        int maxActivityID = 0;
        String maxActivityIDQuery = "SELECT  max(activity_id) act_id FROM " + TABLE_ACTIVITY_LOG;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(maxActivityIDQuery, null);


        cursor.moveToFirst();

        while (!cursor.isAfterLast()) {

            maxActivityID = cursor.getInt(0) + 1;


            cursor.moveToNext();

        }
        cursor.close();

        // return count
        //return cursor.getInt(0);
        return maxActivityID;

    }

    public int getNewExerciseID() {
        int maxExerciseID = 0;
        String maxActivityIDQuery = "SELECT  max(exercise_id) act_id FROM " + TABLE_ACTIVITY_LOG;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(maxActivityIDQuery, null);


        cursor.moveToFirst();


        while (!cursor.isAfterLast()) {

            maxExerciseID = cursor.getInt(0) + 1;


            cursor.moveToNext();

        }
        cursor.close();

        // return count
        //return cursor.getInt(0);
        return maxExerciseID;

    }

    // Updating single contact
    public int updateExerciseLog(ExerciseLog exerciseLog) {

        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_ACTIVITY_ID, exerciseLog.getActivity_id());
        values.put(KEY_ACTIVITY_TS, exerciseLog.getActivity_ts());
        values.put(KEY_LAT_LNG, exerciseLog.getLatLng());
        values.put(KEY_CREATION_TS, exerciseLog.getCreation_Ts());

        // updating row
        return db.update(TABLE_ACTIVITY_LOG, values, KEY_ID + " = ?",
                new String[]{String.valueOf(exerciseLog.getID())});

    }

    // Deleting single contact
    public void deleteExerciseLog(ExerciseLog exerciseLog) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_ACTIVITY_LOG, KEY_ID + " = ?",
                new String[]{String.valueOf(exerciseLog.getID())});
        db.close();
    }

}
