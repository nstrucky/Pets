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
import android.content.DialogInterface;
import android.content.Loader;
import android.database.Cursor;
import android.content.CursorLoader;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.android.pets.data.PetContract.PetEntry;

import static com.example.android.pets.data.PetContract.PetEntry.GENDER_FEMALE;
import static com.example.android.pets.data.PetContract.PetEntry.GENDER_MALE;
import static com.example.android.pets.data.PetContract.PetEntry.GENDER_UNKNOWN;

/**
 * Allows user to create a new pet or edit an existing one.
 */
public class EditorActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {


    private final String LOG_TAG = getClass().getSimpleName();
    private Uri mPetUri;
    private final int PET_LOADER = 1;
    private boolean mPetChanged = false;

    private EditText mNameEditText;
    private EditText mBreedEditText;
    private EditText mWeightEditText;
    private Spinner mGenderSpinner;

    /**
     * Gender of the pet. The possible values are:
     * 0 for unknown gender, 1 for male, 2 for female.
     */
    private int mGender = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editor);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        mPetUri = getIntent().getData();

        if (mPetUri != null) {
            setTitle(getString(R.string.edit_activity_title));
            Log.i(LOG_TAG, mPetUri.toString());
            getLoaderManager().initLoader(PET_LOADER, null, this);
        } else {
            setTitle(getString(R.string.add_activity_title));
        }

        // Find all relevant views that we will need to read user input from
        mNameEditText = (EditText) findViewById(R.id.edit_pet_name);
        mBreedEditText = (EditText) findViewById(R.id.edit_pet_breed);
        mWeightEditText = (EditText) findViewById(R.id.edit_pet_weight);
        mGenderSpinner = (Spinner) findViewById(R.id.spinner_gender);
        
        mNameEditText.setOnTouchListener(touchListener);
        mBreedEditText.setOnTouchListener(touchListener);
        mWeightEditText.setOnTouchListener(touchListener);
        mGenderSpinner.setOnTouchListener(touchListener);

        setupSpinner();
    }

    /**
     * Setup the dropdown spinner that allows the user to select the gender of the pet.
     */
    private void setupSpinner() {
        // Create adapter for spinner. The list options are from the String array it will use
        // the spinner will use the default layout
        ArrayAdapter genderSpinnerAdapter = ArrayAdapter.createFromResource(this,
                R.array.array_gender_options, android.R.layout.simple_spinner_item);

        // Specify dropdown layout style - simple list view with 1 item per line
        genderSpinnerAdapter.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);

        // Apply the adapter to the spinner
        mGenderSpinner.setAdapter(genderSpinnerAdapter);

        // Set the integer mSelected to the constant values
        mGenderSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selection = (String) parent.getItemAtPosition(position);
                if (!TextUtils.isEmpty(selection)) {
                    if (selection.equals(getString(R.string.gender_male))) {
                        mGender = GENDER_MALE; // Male
                    } else if (selection.equals(getString(R.string.gender_female))) {
                        mGender = GENDER_FEMALE; // Female
                    } else {
                        mGender = GENDER_UNKNOWN; // Unknown
                    }
                }
            }

            // Because AdapterView is an abstract class, onNothingSelected must be defined
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                mGender = 0; // Unknown
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu options from the res/menu/menu_editor.xml file.
        // This adds menu items to the app bar.
        getMenuInflater().inflate(R.menu.menu_editor, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // User clicked on a menu option in the app bar overflow menu
        switch (item.getItemId()) {
            // Respond to a click on the "Save" menu option
            case R.id.action_save:

                if (allFieldsFilled()) {
                    savePet();
                    finish();
                } else {
                    Toast.makeText(this, "Pet not saved.  Fill in all fields.", Toast.LENGTH_SHORT).show();
                }

                // Do nothing for now
                return true;
            // Respond to a click on the "Delete" menu option
            case R.id.action_delete:
                // Do nothing for now
                return true;
            // Respond to a click on the "Up" arrow button in the app bar
            case android.R.id.home:
                // Navigate back to parent activity (CatalogActivity)
                if (!mPetChanged) {
                    NavUtils.navigateUpFromSameTask(this);
                    return true;
                }

                DialogInterface.OnClickListener discardButtonListener =
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                              NavUtils.navigateUpFromSameTask(EditorActivity.this);
                            }
                        };

                showUnsavedChangesDialog(discardButtonListener);
                return true;

        }
        return super.onOptionsItemSelected(item);
    }



    private boolean allFieldsFilled() {

        if (TextUtils.isEmpty(mNameEditText.getText()) ||
                TextUtils.isEmpty(mBreedEditText.getText()) ||
                TextUtils.isEmpty(mWeightEditText.getText())) {
            return false;
        }

        return true;

    }

    private void savePet() {
        ContentValues values = new ContentValues();
        int petWeight = Integer.parseInt(mWeightEditText.getText().toString());
        values.put(PetEntry.COLUMN_NAME_NAME, mNameEditText.getText().toString());
        values.put(PetEntry.COLUMN_NAME_BREED, mBreedEditText.getText().toString());
        values.put(PetEntry.COLUMN_NAME_GENDER, mGender);
        values.put(PetEntry.COLUMN_NAME_WEIGHT, petWeight);

        try {

            if (mPetUri != null) {
                getContentResolver().update(mPetUri, values, null, null);
            } else {
                Uri newUri = getContentResolver().insert(PetEntry.CONTENT_URI, values);

                if (newUri == null) {
                    Toast.makeText(this, "Insertion failed...", Toast.LENGTH_SHORT).show();
                } else {
                    long newRowId = ContentUris.parseId(newUri);
                    Toast.makeText(this, "New pet saved with ID: "+newRowId, Toast.LENGTH_SHORT).show();
                }
            }

        } catch (IllegalArgumentException e) {
            Log.e(LOG_TAG, "Error updating", e);
            Toast.makeText(this, e.toString(), Toast.LENGTH_SHORT).show();
        }

    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {

        String[] projection = {
                PetEntry._ID,
                PetEntry.COLUMN_NAME_NAME,
                PetEntry.COLUMN_NAME_BREED,
                PetEntry.COLUMN_NAME_WEIGHT,
                PetEntry.COLUMN_NAME_GENDER
        };

        return new CursorLoader(getApplicationContext(),
                                                  mPetUri,
                                                  projection,
                                                  null,
                                                  null,
                                                  null);

    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor newCursor) {

        if (newCursor.moveToNext()) {
            String name = newCursor.getString(newCursor.getColumnIndex(PetEntry.COLUMN_NAME_NAME));
            String breed = newCursor.getString(newCursor.getColumnIndex(PetEntry.COLUMN_NAME_BREED));
            int weight = newCursor.getInt(newCursor.getColumnIndex(PetEntry.COLUMN_NAME_WEIGHT));
            int gender = newCursor.getInt(newCursor.getColumnIndex(PetEntry.COLUMN_NAME_GENDER));

            mNameEditText.setText(name);
            mBreedEditText.setText(breed);
            mWeightEditText.setText(String.valueOf(weight));

            switch (gender) {
                case GENDER_UNKNOWN:
                    mGenderSpinner.setSelection(0);
                    break;

                case GENDER_FEMALE:
                    mGenderSpinner.setSelection(2);
                    break;

                case GENDER_MALE:
                    mGenderSpinner.setSelection(1);
                    break;


            }


        }




    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {


    }
    
    
    private View.OnTouchListener touchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            mPetChanged = true;
            return false;
        }
    };


    @Override
    public void onBackPressed() {

        if (!mPetChanged) {
            super.onBackPressed();
            return;
        }

        DialogInterface.OnClickListener discardButtonClickListener =
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                       finish();
                    }
                };


        showUnsavedChangesDialog(discardButtonClickListener);

    }




    private void showUnsavedChangesDialog(DialogInterface.OnClickListener discardButtonClickListener) {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setMessage("There are unsaved changes, discard?");
        builder.setPositiveButton("Discard", discardButtonClickListener);
        builder.setNegativeButton("Keep Editing", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (dialog != null) {
//                    finish();
                    dialog.dismiss();
                }
            }
        });

        AlertDialog alertDialog = builder.create();
        alertDialog.show();

    }

}