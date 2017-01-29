package com.example.android.pets.data;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.Nullable;
import com.example.android.pets.data.PetContract.PetEntry;

/**
 * Created by root on 1/29/17.
 */

public class PetProvider extends ContentProvider {

    public static final String LOG_TAG = PetProvider.class.getSimpleName();

    private static final int PETS = 100;
    private static final int PET_ID = 101;

    private static final UriMatcher sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

    static {

        sUriMatcher.addURI(PetContract.CONTENT_AUTHORITY, PetContract.PATH_PETS, PETS);
        sUriMatcher.addURI(PetContract.CONTENT_AUTHORITY, PetContract.PATH_PETS + "/#", PET_ID);

    }

    private PetDbHelper mDbHelper;

    @Nullable
    @Override
    public String getType(Uri uri) {
        return null;
    }

    @Override
    public boolean onCreate() {
        mDbHelper = new PetDbHelper(getContext());

        return true;
    }

    @Nullable
    @Override
    public Cursor query(Uri uri,
                        String[] projection, //columns we want
                        String selection, //WHERE something = ?
                        String[] selectionArgs, // ? = something
                        String sortOrder) {

        SQLiteDatabase database = mDbHelper.getReadableDatabase();

        Cursor cursor;

        int match = sUriMatcher.match(uri);

        switch (match) {

            case PETS:
                cursor = database.query(PetEntry.TABLE_NAME, projection, selection, selectionArgs,
                                        null, null, sortOrder);
                break;

            case PET_ID:
                selection = PetEntry._ID + "=?"; //WHERE clause (WHERE _id=?)
                selectionArgs = new String[] { String.valueOf(ContentUris.parseId(uri)) }; //substitutes "?" argument

                cursor = database.query(PetEntry.TABLE_NAME, projection, selection, selectionArgs,
                                        null, null, sortOrder);
                break;

            default:
                throw new IllegalArgumentException("Cannot query unknown URI " + uri);
        }
        return cursor;
    }

    @Nullable
    @Override
    public Uri insert(Uri uri, ContentValues values) {

        return insertPet(uri, values);
    }


    private Uri insertPet(Uri uri, ContentValues values) {

        Uri returnedUri = uri;
        long newRowId;
        SQLiteDatabase database = mDbHelper.getWritableDatabase();

        int match = sUriMatcher.match(uri);

        switch (match) {

            case PETS:
                newRowId = database.insert(PetEntry.TABLE_NAME, null, values);
                returnedUri = ContentUris.withAppendedId(uri, newRowId);
                break;

            default:
                throw new IllegalArgumentException("Cannot insert into Uri "+ uri);

        }

        return returnedUri;

    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        return 0;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        return 0;
    }
}
