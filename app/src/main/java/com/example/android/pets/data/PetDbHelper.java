package com.example.android.pets.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import com.example.android.pets.data.PetContract.PetEntry;
/**
 * Created by root on 1/25/17.
 */

public class PetDbHelper extends SQLiteOpenHelper {


    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "Pets.db";
    private static final String SQL_CREATE_ENTRIES = "CREATE TABLE " +
            PetEntry.TABLE_NAME + " (" + PetEntry._ID + " INTEGER PRIMARY KEY, " +
            PetEntry.COLUMN_NAME_NAME + " TEXT," +
            PetEntry.COLUMN_NAME_BREED + " TEXT," +
            PetEntry.COLUMN_NAME_GENDER + " INTEGER," +
            PetEntry.COLUMN_NAME_WEIGHT + " INTEGER)";

    private static final String SQL_DELETE_ENTRIES = "DROP TABLE IF EXISTS " + PetEntry.TABLE_NAME;

    public PetDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }


    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(SQL_DELETE_ENTRIES);
        onCreate(db);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE_ENTRIES);

    }
}
