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

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.app.NavUtils;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.android.pets.data.PetsContract;
import com.example.android.pets.data.PetsContract.PetsEntry;
import com.example.android.pets.data.PetsDbHelper;

/**
 * Allows user to create a new pet or edit an existing one.
 */
public class EditorActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor>{

    /** EditText field to enter the pet's name */
    private EditText mNameEditText;

    /** EditText field to enter the pet's breed */
    private EditText mBreedEditText;

    /** EditText field to enter the pet's weight */
    private EditText mWeightEditText;

    /** EditText field to enter the pet's gender */
    private Spinner mGenderSpinner;

    /**
     * Gender of the pet. The possible values are:
     * 0 for unknown gender, 1 for male, 2 for female.
     */
    private int mGender = 0;
    private Uri currentPetUri;
    private static final int EXISTING_PET_LOADER=0;
    private boolean mPetHasChanged = false;
    //OnTouchListener that listens for any user touches on a view,implying that they are modifing
    //the view and we change the mPetHasChanged boolean to true
    private View.OnTouchListener mTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            mPetHasChanged = true;
            return false;
        }
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editor);
        Intent intent = getIntent();
        currentPetUri= intent.getData();
        if(currentPetUri==null)
            setTitle("Add a pet");
        else {

            setTitle(R.string.editor_activity_titile_edit_pet);
            getSupportLoaderManager().initLoader(EXISTING_PET_LOADER,null,this);
        }
        // Find all relevant views that we will need to read user input from
        mNameEditText = (EditText) findViewById(R.id.edit_pet_name);
        mBreedEditText = (EditText) findViewById(R.id.edit_pet_breed);
        mWeightEditText = (EditText) findViewById(R.id.edit_pet_weight);
        mGenderSpinner = (Spinner) findViewById(R.id.spinner_gender);

        mNameEditText.setOnTouchListener(mTouchListener);
        mBreedEditText.setOnTouchListener(mTouchListener);
        mWeightEditText.setOnTouchListener(mTouchListener);
        mGenderSpinner.setOnTouchListener(mTouchListener);
        setupSpinner();
    }

    private void showUnsavedChnagesDialog(DialogInterface.OnClickListener discardButtonClickListener){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.unsaved_changes_dialog_msg);
        builder.setPositiveButton(R.string.discard,discardButtonClickListener);
        builder.setNegativeButton(R.string.keep_editing, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //User Cliccked the "Keep editing" button ,so dismiss the dialog
                //and continue editing the pet
                if (dialog!= null)
                    dialog.dismiss();
            }
        });

        //Create and show the AlertDialog
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }
    @Override
    public void onBackPressed(){
       //If the pet Hasn't changed,continue with handling back button pressed
        if(!mPetHasChanged)
        {
            super.onBackPressed();
            return;
        }
        //Otherwise if there are unsaved changed ,setup a dialog to warn the user
        //Create a click Listener to handle the rhe user confirming rhat changes should be discarded.
        DialogInterface.OnClickListener discardButtonClickListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                finish();//Userr clicked "Discard " vutton , close the current activity
            }
        };
        showUnsavedChnagesDialog(discardButtonClickListener);

    }
    private void SavePets() {

            String petName = mNameEditText.getText().toString().trim();
            String petBreed = mBreedEditText.getText().toString().trim();
            String petWeightString = mWeightEditText.getText().toString().trim();

        if(currentPetUri==null&& TextUtils.isEmpty(petName)&&TextUtils.isEmpty(petBreed)&&TextUtils.isEmpty(petWeightString)&&mGender==PetsEntry.GENDER_UNKNOWN)
        {return;}

        int petWeight= Integer.parseInt(petWeightString);
            PetsDbHelper mDbHelper = new PetsDbHelper(this);

            SQLiteDatabase db = mDbHelper.getWritableDatabase();
            ContentValues values = new ContentValues();
            values.put(PetsEntry.COLUMN_PET_NAME, petName);
            values.put(PetsEntry.COLUMN_PET_BREED, petBreed);

            values.put(PetsEntry.COLUMN_PET_GENDER, mGender);
        if ((( TextUtils.isEmpty(mWeightEditText.getText().toString().trim())))) {
            values.put(PetsEntry.COLUMN_PET_WEIGHT,0);
        }
        values.put(PetsEntry.COLUMN_PET_WEIGHT, petWeight);

            if(currentPetUri!=null){
                 int newUri1=getContentResolver().update(currentPetUri,values,null,null);
                 if(newUri1!=-1)
                     Toast.makeText(this,"Pet Updated",Toast.LENGTH_SHORT).show();
                 else
                     Toast.makeText(this,"Pet not Updated",Toast.LENGTH_SHORT).show();
            }
            else {
                Uri newUri = getContentResolver().insert(PetsContract.CONTENT_URI, values);
                if (newUri == null) {
                    Toast.makeText(this, R.string.editor_activity_pet_insert_failed, Toast.LENGTH_SHORT).show();
                } else
                    Toast.makeText(this, R.string.editor_activity_pet_insert_success, Toast.LENGTH_SHORT).show();
            }


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
                        mGender = PetsEntry.GENDER_MALE; // Male
                    } else if (selection.equals(getString(R.string.gender_female))) {
                        mGender = PetsEntry.GENDER_FEMALE; // Female
                    } else {
                        mGender = PetsEntry.GENDER_UNKNOWN; // Unknown
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
        if (currentPetUri==null)
        {
            //This is the new pet,so change the app bar to say "add a pet"
            setTitle(getString(R.string.editor_activity_title_new_pet));
            //Invalidate the option menu , so the Deletemenu option can be hidden
            //(It doesn't make sense to delete the pet that hasn't been created yet
            //)
            invalidateOptionsMenu();
        }
        else
        {
            setTitle(getString(R.string.editor_activity_titile_edit_pet));
        }
        return true;
    }
    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
        // If this is a new pet, hide the "Delete" menu item.
        if (currentPetUri == null) {
            MenuItem menuItem = menu.findItem(R.id.action_delete);
            menuItem.setVisible(false);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // User clicked on a menu option in the app bar overflow menu
        switch (item.getItemId()) {
            // Respond to a click on the "Save" menu option
            case R.id.action_save:
                SavePets();
                finish();

                return true;
            // Respond to a click on the "Delete" menu option
            case R.id.action_delete:
                showDeleteConfirmationDialog();
                return true;
            // Respond to a click on the "Up" arrow button in the app bar
            case android.R.id.home:
                // Navigate back to parent activity (CatalogActivity)
                if(!mPetHasChanged){
                    NavUtils.navigateUpFromSameTask(this);
                    return true;
                }
                else{
                    DialogInterface.OnClickListener discardButtonClickListener =
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    // User clicked "Discard" button, navigate to parent activity.
                                    NavUtils.navigateUpFromSameTask(EditorActivity.this);
                                }
                            };

                    // Show a dialog that notifies the user they have unsaved changes
                    showUnsavedChnagesDialog(discardButtonClickListener);
                    return true;

                }

        }
        return super.onOptionsItemSelected(item);
    }


    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        String[] projection={
                PetsEntry._ID,
                PetsEntry.COLUMN_PET_NAME,
                PetsEntry.COLUMN_PET_BREED,
                PetsEntry.COLUMN_PET_GENDER,
                PetsEntry.COLUMN_PET_WEIGHT
        };


        return new CursorLoader(
                this,
                currentPetUri,
                projection,
                null,
                null,
                null
        );
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        if(cursor == null||cursor.getCount()<1)
        {
            return;

        }

        if(cursor.moveToFirst())
        {
            int nameColumnIndex = cursor.getColumnIndex(PetsEntry.COLUMN_PET_NAME);
            int breedColumnIndex = cursor.getColumnIndex(PetsEntry.COLUMN_PET_BREED);
            int genderColumnIndex = cursor.getColumnIndex(PetsEntry.COLUMN_PET_GENDER);
            int weightColumnIndex = cursor.getColumnIndex(PetsEntry.COLUMN_PET_WEIGHT);

            // Extract out the value from the Cursor for the given column index
            String name = cursor.getString(nameColumnIndex);
            String breed = cursor.getString(breedColumnIndex);
            int gender = cursor.getInt(genderColumnIndex);
            int weight = cursor.getInt(weightColumnIndex);

            // Update the views on the screen with the values from the database
            mNameEditText.setText(name);
            mBreedEditText.setText(breed);
            mWeightEditText.setText(Integer.toString(weight));

            // Gender is a dropdown spinner, so map the constant value from the database
            // into one of the dropdown options (0 is Unknown, 1 is Male, 2 is Female).
            // Then call setSelection() so that option is displayed on screen as the current selection.
            switch (gender) {
                case PetsEntry.GENDER_MALE:   mGenderSpinner.setSelection(1);
                    break;
                case PetsEntry.GENDER_FEMALE:
                    mGenderSpinner.setSelection(2);
                    break;
                default:
                    mGenderSpinner.setSelection(0);
                    break;
            }
        }

    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

        mNameEditText.setText("");
        mBreedEditText.setText("");
        mWeightEditText.setText("");
        mGenderSpinner.setSelection(0);

    }
    private void showDeleteConfirmationDialog() {
        // Create an AlertDialog.Builder and set the message, and click listeners
        // for the postivie and negative buttons on the dialog.
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.delete_dialog_msg);
        builder.setPositiveButton(R.string.delete, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User clicked the "Delete" button, so delete the pet.
                deletePet();
            }
        });
        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User clicked the "Cancel" button, so dismiss the dialog
                // and continue editing the pet.
                if (dialog != null) {
                    dialog.dismiss();
                }
            }
        });

        // Create and show the AlertDialog
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    /**
     * Perform the deletion of the pet in the database.
     */
    private void deletePet() {
        // TODO: Implement this method

        int rowDeleted=getContentResolver().delete(currentPetUri,null,null);
        if (rowDeleted==0)
            Toast.makeText(this,R.string.editor_delete_pet_failed,Toast.LENGTH_SHORT).show();
        else
            Toast.makeText(this,R.string.editor_delete_pet_successful,Toast.LENGTH_SHORT).show();

        finish();


    }
}