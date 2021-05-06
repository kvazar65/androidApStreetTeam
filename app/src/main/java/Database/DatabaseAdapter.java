package Database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

import Map.locations.Place;

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
        String[] columns = new String[]{DatabaseHelper.COLUMN_ID, DatabaseHelper.COLUMN_LATITUDE,DatabaseHelper.COLUMN_LABEL,DatabaseHelper.COLUMN_INFO,DatabaseHelper.COLUMN_LONGITUDE};
        return database.query(DatabaseHelper.TABLE, columns, null, null, null, null, null);
    }

    public List<Place> getPlaces() {
        ArrayList<Place> places = new ArrayList<>();
        Cursor cursor = getAllEntries();
        while (cursor.moveToNext()) {
            int id = cursor.getInt(cursor.getColumnIndex(DatabaseHelper.COLUMN_ID));
            String label = cursor.getString(cursor.getColumnIndex(DatabaseHelper.COLUMN_LABEL));
            String info = cursor.getString(cursor.getColumnIndex(DatabaseHelper.COLUMN_INFO));
            float latitude = cursor.getFloat(cursor.getColumnIndex(DatabaseHelper.COLUMN_LATITUDE));
            float longitude = cursor.getFloat(cursor.getColumnIndex(DatabaseHelper.COLUMN_LONGITUDE));
            places.add(new Place(id, label, info, latitude, longitude));
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
            String label = cursor.getString(cursor.getColumnIndex(DatabaseHelper.COLUMN_LABEL));
            String info = cursor.getString(cursor.getColumnIndex(DatabaseHelper.COLUMN_INFO));
            float latitude = cursor.getFloat(cursor.getColumnIndex(DatabaseHelper.COLUMN_LATITUDE));
            float longitude = cursor.getFloat(cursor.getColumnIndex(DatabaseHelper.COLUMN_LONGITUDE));
            place = new Place(id, label,label, latitude, longitude);
        }
        cursor.close();
        return place;
    }

    public long insert(Place place) {

        ContentValues cv = new ContentValues();
        cv.put(DatabaseHelper.COLUMN_LABEL, place.getLabel());
        cv.put(DatabaseHelper.COLUMN_INFO, place.getInfo());
        cv.put(DatabaseHelper.COLUMN_LATITUDE, place.getLatitude());
        cv.put(DatabaseHelper.COLUMN_LONGITUDE, place.getLongitude());

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
        cv.put(DatabaseHelper.COLUMN_LABEL, place.getLabel());
        cv.put(DatabaseHelper.COLUMN_INFO, place.getInfo());
        cv.put(DatabaseHelper.COLUMN_LATITUDE, place.getLatitude());
        cv.put(DatabaseHelper.COLUMN_LONGITUDE, place.getLongitude());
        return database.update(DatabaseHelper.TABLE, cv, whereClause, null);
    }
}