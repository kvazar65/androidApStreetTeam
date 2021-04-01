package com.example.scaledrone.app;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

public class DatabaseAdapter {

    private DatabaseHelper dbHelper;
    private SQLiteDatabase database;

    public DatabaseAdapter(Context context) {
        dbHelper = new DatabaseHelper(context.getApplicationContext());
    }

    public DatabaseAdapter open() {
        database = dbHelper.getWritableDatabase();
        return this;
    }

    public void close() {
        dbHelper.close();
    }

    private Cursor getAllEntries() {
        String[] columns = new String[]{DatabaseHelper.COLUMN_ID, DatabaseHelper.COLUMN_NAME,DatabaseHelper.COLUMN_LABEL ,DatabaseHelper.COLUMN_YEAR};
        return database.query(DatabaseHelper.TABLE, columns, null, null, null, null, null);
    }

    public List<Place> getUsers() {
        ArrayList<Place> places = new ArrayList<>();
        Cursor cursor = getAllEntries();
        while (cursor.moveToNext()) {
            int id = cursor.getInt(cursor.getColumnIndex(DatabaseHelper.COLUMN_ID));
            int name = cursor.getInt(cursor.getColumnIndex(DatabaseHelper.COLUMN_NAME));
            int label = cursor.getInt(cursor.getColumnIndex(DatabaseHelper.COLUMN_LABEL));
            int year = cursor.getInt(cursor.getColumnIndex(DatabaseHelper.COLUMN_YEAR));
            places.add(new Place(id, label, name, year));
        }
        cursor.close();
        return places;
    }

    public long getCount() {
        return DatabaseUtils.queryNumEntries(database, DatabaseHelper.TABLE);
    }

    public Place getUser(long id) {
        Place place = null;
        String query = String.format("SELECT * FROM %s WHERE %s=?", DatabaseHelper.TABLE, DatabaseHelper.COLUMN_ID);
        Cursor cursor = database.rawQuery(query, new String[]{String.valueOf(id)});
        if (cursor.moveToFirst()) {
            int name = cursor.getInt(cursor.getColumnIndex(DatabaseHelper.COLUMN_NAME));
            int label = cursor.getInt(cursor.getColumnIndex(DatabaseHelper.COLUMN_LABEL));
            int year = cursor.getInt(cursor.getColumnIndex(DatabaseHelper.COLUMN_YEAR));
            place = new Place(id, label, name, year);
        }
        cursor.close();
        return place;
    }

    public long insert(Place place) {

        ContentValues cv = new ContentValues();
        cv.put(DatabaseHelper.COLUMN_NAME, place.getName());
        cv.put(DatabaseHelper.COLUMN_YEAR, place.getYear());
        cv.put(DatabaseHelper.COLUMN_LABEL, place.getLabel());

        return database.insert(DatabaseHelper.TABLE, null, cv);
    }

    public long delete(long userId) {

        String whereClause = "_id = ?";
        String[] whereArgs = new String[]{String.valueOf(userId)};
        return database.delete(DatabaseHelper.TABLE, whereClause, whereArgs);
    }

    public long update(Place place) {

        String whereClause = DatabaseHelper.COLUMN_ID + "=" + String.valueOf(place.getId());
        ContentValues cv = new ContentValues();
        cv.put(DatabaseHelper.COLUMN_NAME, place.getName());
        cv.put(DatabaseHelper.COLUMN_YEAR, place.getYear());
        cv.put(DatabaseHelper.COLUMN_LABEL, place.getLabel());
        return database.update(DatabaseHelper.TABLE, cv, whereClause, null);
    }
}