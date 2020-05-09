package com.oliviaxie.simplediary.Data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.oliviaxie.simplediary.Models.Entry;

import java.util.ArrayList;

public class DatabaseHandler extends SQLiteOpenHelper {

    private final ArrayList<Entry> entryList = new ArrayList<>();
    private Context context;

    public DatabaseHandler(Context context) {
        super(context, Constants.DATABASE_NAME, null, Constants.DATABASE_VERSION);
        this.context = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        // Create entries database table
        String CREATE_TABLE = "CREATE TABLE " + Constants.TABLE_NAME + "("
                + Constants.KEY_ID + " LONG PRIMARY KEY, " + Constants.ENTRY_TITLE +
                " TEXT, " + Constants.ENTRY_TEXT + " TEXT, " + Constants.DATE_NAME + " INTEGER);";

        db.execSQL(CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        // Drop existing table
        db.execSQL("DROP TABLE IF EXISTS " + Constants.TABLE_NAME);

        // Create new table
        onCreate(db);
    }

    // Delete Entry
    public void deleteEntry(long id) {

        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(Constants.TABLE_NAME, Constants.KEY_ID + " = ?",
                new String[]{String.valueOf(id)});
        db.close();
    }

    // Edit entry
    public void editEntry(Entry entry) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put(Constants.ENTRY_TITLE, entry.getTitle());
        values.put(Constants.ENTRY_TEXT, entry.getText());
        values.put(Constants.DATE_NAME, entry.getDate());

        db.update(Constants.TABLE_NAME, values, Constants.KEY_ID + "=" + entry.getEntryId(), null);
        db.close();
    }

    // Add an entry
    public void addEntry(Entry entry) {

        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(Constants.ENTRY_TITLE, entry.getTitle());
        values.put(Constants.ENTRY_TEXT, entry.getText());
        values.put(Constants.DATE_NAME, entry.getDate());
        values.put(Constants.KEY_ID, entry.getEntryId());

        if (!exists(entry.getEntryId())) {
            db.insert(Constants.TABLE_NAME, null, values);
        }

        db.close();
    }

    // Get all entries
    public ArrayList<Entry> getEntries() {

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
                entry.setEntryId(cursor.getLong(cursor.getColumnIndex(Constants.KEY_ID)));
                entry.setDate(cursor.getLong(cursor.getColumnIndex(Constants.DATE_NAME)));

                entryList.add(entry);

            } while (cursor.moveToNext());

        }

        cursor.close();
        dba.close();

        return entryList;
    }

    public void clearTable() {
        SQLiteDatabase db = this.getWritableDatabase();

        db.delete(Constants.TABLE_NAME, null, null);
    }

    public boolean exists(long entryId) {
        SQLiteDatabase dba = this.getReadableDatabase();

        Cursor cursor = dba.query(Constants.TABLE_NAME, new String[]{Constants.KEY_ID}, Constants.KEY_ID + " = " + Long.toString(entryId),
                null, null, null, null);

        return cursor.getCount() != 0;
    }
}
