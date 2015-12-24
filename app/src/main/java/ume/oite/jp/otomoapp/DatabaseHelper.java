package ume.oite.jp.otomoapp;

/**
 * Created by Ume on 2015/07/11.
 */

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper {

    public static final String DATABASE_NAME = "Database.db";
    public static final int DATABASE_VERSION = 1;

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    public void onCreate(SQLiteDatabase db) {

        String sql = "CREATE TABLE Sample ("
                + " _id INTEGER PRIMARY KEY AUTOINCREMENT,"
                + " Schedule TEXT,"
                + " Place TEXT,"
                + " BeginTime TEXT,"
                + " EndTime TEXT"
                + ");";
        db.execSQL(sql);
    }

    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    }
}
