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

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.example.android.pets.data.PetContract.PetEntry;
import com.example.android.pets.data.PetDbHelper;

/**
 * Displays list of pets that were entered and stored in the app.
 */
public class CatalogActivity extends AppCompatActivity {



    private PetDbHelper helper;


    @Override
    protected void onStart() {
        displayDatabaseInfo();
        super.onStart();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_catalog);


        helper = new PetDbHelper(this);

        // Setup FAB to open EditorActivity
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(CatalogActivity.this, EditorActivity.class);
                startActivity(intent);
            }
        });

        displayDatabaseInfo();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu options from the res/menu/menu_catalog.xml file.
        // This adds menu items to the app bar.
        getMenuInflater().inflate(R.menu.menu_catalog, menu);
        return true;
    }

    private void insertPet() {

        SQLiteDatabase db = helper.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(PetEntry.COLUMN_NAME_NAME, "Jackson");
        values.put(PetEntry.COLUMN_NAME_GENDER, PetEntry.GENDER_MALE);
        values.put(PetEntry.COLUMN_NAME_BREED, "Terrier");

        values.put(PetEntry.COLUMN_NAME_WEIGHT, 35);

        long newRowID =  db.insert(PetEntry.TABLE_NAME, null, values);


    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // User clicked on a menu option in the app bar overflow menu
        switch (item.getItemId()) {
            // Respond to a click on the "Insert dummy data" menu option
            case R.id.action_insert_dummy_data:
                insertPet();
                displayDatabaseInfo();
                return true;
            // Respond to a click on the "Delete all entries" menu option
            case R.id.action_delete_all_entries:
                // Do nothing for now
                return true;
        }
        return super.onOptionsItemSelected(item);
    }






    private void displayDatabaseInfo() {
        // To access our database, we instantiate our subclass of SQLiteOpenHelper
        // and pass the context, which is the current activity.
//        PetDbHelper mDbHelper = new PetDbHelper(this);

        // Create and/or open a database to read from it
        SQLiteDatabase db = helper.getReadableDatabase();

        // Perform this raw SQL query "SELECT * FROM pets"
        // to get a Cursor that contains all rows from the pets table.
//        Cursor cursor = db.rawQuery("SELECT * FROM " + PetEntry.TABLE_NAME, null);

        String[] projection = {
                PetEntry._ID,
                PetEntry.COLUMN_NAME_NAME,
                PetEntry.COLUMN_NAME_BREED,
                PetEntry.COLUMN_NAME_GENDER,
                PetEntry.COLUMN_NAME_WEIGHT
        };

        Cursor cursor = db.query(PetEntry.TABLE_NAME,
                                 projection,
                                 null,
                                 null,
                                 null,
                                 null,
                                 null
        );

        int idIndex = cursor.getColumnIndex(PetEntry._ID);
        int nameIndex = cursor.getColumnIndex(PetEntry.COLUMN_NAME_NAME);
        int breedIndex = cursor.getColumnIndex(PetEntry.COLUMN_NAME_BREED);
        int genderIndex = cursor.getColumnIndex(PetEntry.COLUMN_NAME_GENDER);
        int weightIndex = cursor.getColumnIndex(PetEntry.COLUMN_NAME_WEIGHT);

        StringBuilder builder = new StringBuilder();

        while (cursor.moveToNext()) {

            int id = cursor.getInt(idIndex);
            String name = cursor.getString(nameIndex);
            String breed = cursor.getString(breedIndex);
            int gender = cursor.getInt(genderIndex);
            int weight = cursor.getInt(weightIndex);


            builder.append(String.format("%d - %s - %s - %d - %d%n", id, name, breed, gender, weight));


        }



        try {
            // Display the number of rows in the Cursor (which reflects the number of rows in the
            // pets table in the database).
            TextView displayView = (TextView) findViewById(R.id.text_view_pet);



            displayView.setText("Number of rows in pets database table: " + cursor.getCount() + "\n");
            displayView.append(builder.toString());
        } finally {
            // Always close the cursor when you're done reading from it. This releases all its
            // resources and makes it invalid.
            cursor.close();
        }
    }
}
