package Database;

import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteDatabase;
import android.content.Context;

public class DatabaseHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "places12.db"; // название бд
    private static final int SCHEMA = 1; // версия базы данных
    static final String TABLE = "places"; // название таблицы в бд
    // названия столбцов
    public static final String COLUMN_ID = "_id";
    public static final String COLUMN_LABEL = "label";
    public static final String COLUMN_LATITUDE = "latitude"; //широта
    public static final String COLUMN_LONGITUDE = "longotude"; //долгота

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, SCHEMA);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        db.execSQL("CREATE TABLE " + TABLE + " (" + COLUMN_ID
                + " INTEGER PRIMARY KEY AUTOINCREMENT,"+ COLUMN_LABEL + " TEXT, " + COLUMN_LATITUDE
                + " INTEGER, " + COLUMN_LONGITUDE + " INTEGER);");
        // добавление начальных данных
        db.execSQL("INSERT INTO "+ TABLE + " ("+ COLUMN_LABEL + ", " + COLUMN_LATITUDE
                + ", " + COLUMN_LONGITUDE + " ) VALUES ('Pluzhnik fitness', 55.588545, 37.600649) ;");
        // добавление начальных данных
        db.execSQL("INSERT INTO "+ TABLE + " ("+ COLUMN_LABEL + ", " + COLUMN_LATITUDE
                + ", " + COLUMN_LONGITUDE + " ) VALUES ('Paris-life', 55.600389, 37.598995) ;");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion,  int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS "+TABLE);
        onCreate(db);
    }
}