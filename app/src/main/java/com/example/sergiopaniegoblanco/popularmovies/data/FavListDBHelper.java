package com.example.sergiopaniegoblanco.popularmovies.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import static com.example.sergiopaniegoblanco.popularmovies.data.FavListContract.*;

/**
 * Created by sergiopaniegoblanco on 27/02/2017.
 */

public class FavListDBHelper extends SQLiteOpenHelper {
    // The database name
    private static final String DATABASE_NAME = "favmovies.db";

    // If you change the database schema, you must increment the database version
    private static final int DATABASE_VERSION = 2;
    private static FavListDBHelper sInstance;

    // Constructor
    public FavListDBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }
    public static synchronized FavListDBHelper getInstance(Context context) {

        // Use the application context, which will ensure that you
        // don't accidentally leak an Activity's context.
        // See this article for more information: http://bit.ly/6LRzfx
        if (sInstance == null) {
            sInstance = new FavListDBHelper(context.getApplicationContext());
        }
        return sInstance;
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {

        // Create a table to hold waitlist data
        final String SQL_CREATE_WAITLIST_TABLE = "CREATE TABLE " + FavlistEntry.TABLE_NAME + " (" +
                FavlistEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                FavlistEntry.COLUMN_MOVIE_NAME + " TEXT NOT NULL," +
                FavlistEntry.COLUMN_MOVIE_POSTER + " TEXT NOT NULL, " +
                FavlistEntry.COLUMN_MOVIE_JSON + " TEXT NOT NULL" +
                "); ";

        sqLiteDatabase.execSQL(SQL_CREATE_WAITLIST_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        // For now simply drop the table and create a new one. This means if you change the
        // DATABASE_VERSION the table will be dropped.
        // In a production app, this method might be modified to ALTER the table
        // instead of dropping it, so that existing data is not deleted.
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + FavlistEntry.TABLE_NAME);
        onCreate(sqLiteDatabase);
    }
}
