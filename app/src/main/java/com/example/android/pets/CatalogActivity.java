/*
 * Copyright (C) 2016 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.example.android.pets;

import android.app.LoaderManager;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.example.android.pets.data.PetContract.PetEntry;

/**
 * Displays list of pets that were entered and stored in the app.
 */
public class CatalogActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {



    private static final int PET_LOADER = 0;
    private ListView mPetListView;
    PetCursorAdapter cursorAdapter;


    @Override
    protected void onStart() {
//        getLoaderManager().initLoader(1, null, this);
        super.onStart();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_catalog);

        cursorAdapter = new PetCursorAdapter(this, null);
        mPetListView = (ListView) findViewById(R.id.listView_pets);

        View emptyView = findViewById(R.id.empty_view);
        mPetListView.setEmptyView(emptyView);

        getLoaderManager().initLoader(PET_LOADER, null, this);
        mPetListView.setAdapter(cursorAdapter);

        mPetListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                Uri uri = ContentUris.withAppendedId(PetEntry.CONTENT_URI, id);
                Intent intent = new Intent(CatalogActivity.this, EditorActivity.class);
                intent.setData(uri);
                startActivity(intent);
            }
        });



        // Setup FAB to open EditorActivity
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(CatalogActivity.this, EditorActivity.class);
                startActivity(intent);
            }
        });



    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu options from the res/menu/menu_catalog.xml file.
        // This adds menu items to the app bar.
        getMenuInflater().inflate(R.menu.menu_catalog, menu);
        return true;
    }

    private void insertPet() {

        ContentValues values = new ContentValues();
        values.put(PetEntry.COLUMN_NAME_NAME, "Jackson");
        values.put(PetEntry.COLUMN_NAME_GENDER, PetEntry.GENDER_MALE);
        values.put(PetEntry.COLUMN_NAME_BREED, "Terrier");

        values.put(PetEntry.COLUMN_NAME_WEIGHT, 35);

        Uri uri = getContentResolver().insert(PetEntry.CONTENT_URI, values);

        long newRowId = ContentUris.parseId(uri);
        Toast.makeText(this, "Dummy inserted with id " + newRowId, Toast.LENGTH_SHORT).show();
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // User clicked on a menu option in the app bar overflow menu
        switch (item.getItemId()) {
            case R.id.action_insert_dummy_data:
                insertPet();
                return true;
            case R.id.action_delete_all_entries:


                // Do nothing for now
                return true;
        }
        return super.onOptionsItemSelected(item);
    }



    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {

        //Specifies columns to be retrieved from table
        String[] projection = {
                PetEntry._ID,
                PetEntry.COLUMN_NAME_NAME,
                PetEntry.COLUMN_NAME_BREED
        };

       return new CursorLoader(getApplicationContext(),//need this so the appropriate ContentResolver and ContentProvider methods are called
                                                        //aka the PetProvider query method in this case.
                         PetEntry.CONTENT_URI, //actually gets passed to the query method's Uri parameter
                         projection,
                         null,
                         null,
                         null);

    }


    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor newCursor) {

        //newCursor is from the PetProvider's query method
        cursorAdapter.swapCursor(newCursor);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        cursorAdapter.swapCursor(null);

    }
}
