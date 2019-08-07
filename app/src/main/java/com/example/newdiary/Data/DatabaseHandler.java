package com.example.newdiary.Data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.example.newdiary.Models.Entry;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;

public class DatabaseHandler extends SQLiteOpenHelper {

    private final ArrayList<Entry> entryList = new ArrayList<>();

    public DatabaseHandler(Context context) {
        super(context, Constants.DATABASE_NAME, null, Constants.DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        // Create entries database table
        String CREATE_TABLE = "CREATE TABLE " + Constants.TABLE_NAME + "("
                + Constants.KEY_ID + " INTEGER PRIMARY KEY, " + Constants.ENTRY_TITLE +
                " TEXT, " + Constants.ENTRY_TEXT + " TEXT, " + Constants.DATE_NAME + " LONG);";

        db.execSQL(CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        // Drop existing table
        db.execSQL("DROP TABLE IF EXISTS " + Constants.TABLE_NAME);

        // Create new table
        onCreate(db);
    }

    // Get total number of entries
    public int getTotalEntries() {

        int totalEntries = 0;

        String query = "SELECT * FROM " + Constants.TABLE_NAME;

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(query, null);

        totalEntries = cursor.getCount();

        cursor.close();

        return totalEntries;
    }

    // Delete Entry
    public void deleteEntry(int id) {

        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(Constants.TABLE_NAME, Constants.KEY_ID + " = ?",
                new String[]{String.valueOf(id)});
        db.close();
    }

    // Add an entry
    public void addEntry(Entry entry) {

        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(Constants.ENTRY_TITLE, entry.getTitle());
        values.put(Constants.ENTRY_TEXT, entry.getText());
        values.put(Constants.DATE_NAME, System.currentTimeMillis());

        db.insert(Constants.TABLE_NAME, null, values);

        db.close();
    }

    // Get all entries
    public ArrayList<Entry> getEntries(){

        entryList.clear();

        SQLiteDatabase dba = this.getReadableDatabase();

        Cursor cursor = dba.query(Constants.TABLE_NAME,
                new String[]{Constants.KEY_ID, Constants.ENTRY_TITLE, Constants.ENTRY_TEXT,
                        Constants.DATE_NAME}, null, null, null, null, Constants.DATE_NAME + " DESC ");

        //loop through...
        if (cursor.moveToFirst()) {
            do {

                Entry entry = new Entry();
                entry.setTitle(cursor.getString(cursor.getColumnIndex(Constants.ENTRY_TITLE)));
                entry.setText(cursor.getString(cursor.getColumnIndex(Constants.ENTRY_TEXT)));
                entry.setEntryId(cursor.getInt(cursor.getColumnIndex(Constants.KEY_ID)));


                DateFormat dateFormat = DateFormat.getDateInstance();
                String date = dateFormat.format(new Date(cursor.getLong(cursor.getColumnIndex(Constants.DATE_NAME))).getTime());

                entry.setDate(date);

                entryList.add(entry);

            } while (cursor.moveToNext());


        }

        cursor.close();
        dba.close();

        return entryList;

    }
}
