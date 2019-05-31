package com.example.android.pets;

import android.app.LoaderManager;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.BaseColumns;
import android.support.annotation.RequiresApi;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.CursorAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.example.android.pets.data.PetsContract;
import com.example.android.pets.data.PetsContract.PetsEntry;
import com.example.android.pets.data.PetsDbHelper;

/**
 * Displays list of pets that were entered and stored in the app.
 */
public class CatalogActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {
    private static final int URI_LOADER = 0;
    CursorAdapter mCursorAdapter
;
    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_catalog);

        // Setup FAB to open EditorActivity
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(CatalogActivity.this, EditorActivity.class);
                startActivity(intent);
            }
        });
        ListView petListView = (ListView)findViewById(R.id.list);



        View emptyView= findViewById(R.id.empty_view);
        petListView.setEmptyView(emptyView);
        mCursorAdapter = new PetsCursorAdapter(this,null);
        petListView.setAdapter(mCursorAdapter);
        getLoaderManager().initLoader(URI_LOADER,null,this);

        petListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
               Intent intent= new Intent(CatalogActivity.this,EditorActivity.class);

               Uri CurrentUri= ContentUris.withAppendedId(PetsContract.CONTENT_URI,id);
               Log.v(String.valueOf(this),"Uri Passed = "+CurrentUri);
               intent.setData(CurrentUri); // set the URi on the data field of the intent
               startActivity(intent);
            }
        });

       // displayDatabaseInfo();
        /*PetsDbHelper mDbhelper = new PetsDbHelper(this);
        SQLiteDatabase db= mDbhelper.getReadableDatabase();
   */ }

    /**
     * Temporary helper method to display information in the onscreen TextView about the state of
     * the pets database.
     */


    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
   /* @Override
    protected void onStart() {
        super.onStart();
        displayDatabaseInfo();
    }
    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    private void displayDatabaseInfo() {
            // To access our database, we instantiate our subclass of SQLiteOpenHelper
            // and pass the context, which is the current activity.
            PetsDbHelper mDbHelper = new PetsDbHelper(this);

            // Create and/or open a database to read from it
            SQLiteDatabase db = mDbHelper.getReadableDatabase();

            // Perform this raw SQL query "SELECT * FROM pets"
            // to get a Cursor that contains all rows from the pets table.
            String[] projection ={
                    BaseColumns._ID,
                    PetsEntry.COLUMN_PET_NAME,
                    PetsEntry.COLUMN_PET_BREED,
                    PetsEntry.COLUMN_PET_GENDER,
                    PetsEntry.COLUMN_PET_WEIGHT};
            *//*Cursor cursor  = db.query(
                    PetsEntry.TABLE_NAME,
                    null,
                    null,
                    null,
                    null,
                    null,
                    null

            );*//*
        Cursor cursor=getContentResolver().query(PetsContract.CONTENT_URI,projection,null,null,null,null );
        *//*TextView displayView = (TextView)findViewById(R.id.text_view_pet);
        try{
            //Create a header in the textview that look likee this.
            //id-name-breed-gender- weight
            //
            // IN the while loop below , iterate through the rows of the curdor and display
            //the information from each column inthis order.
            displayView.setText("The pets Table contain "+ cursor.getCount()+" pets.\n\n");
            displayView.append(PetsEntry._ID+" - "
            +PetsEntry.COLUMN_PET_NAME+" - "
            +PetsEntry.COLUMN_PET_BREED+" -"
            +PetsEntry.COLUMN_PET_GENDER+" - "
            +PetsEntry.COLUMN_PET_WEIGHT+"\n\n");

            // figure out the column index
            int idColumnIndex = cursor.getColumnIndex(PetsEntry._ID);
            int nameColumnIndex = cursor.getColumnIndex(PetsEntry.COLUMN_PET_NAME);
            int breedColumnIndex = cursor.getColumnIndex(PetsEntry.COLUMN_PET_BREED);
            int genderColumnIndex = cursor.getColumnIndex(PetsEntry.COLUMN_PET_GENDER);
            int weightColumnIndex = cursor.getColumnIndex(PetsEntry.COLUMN_PET_WEIGHT);

            //Iterate through all the returned rows in the cursor
            while(cursor.moveToNext())
            {
                int currentId = cursor.getInt(cursor.getColumnIndex(PetsEntry._ID));
                String currentName = cursor.getString(cursor.getColumnIndex(PetsEntry.COLUMN_PET_NAME));
                String currentBreed = cursor.getString(breedColumnIndex);
                String currentGender = cursor.getString(genderColumnIndex);
                int currentWeight = cursor.getInt(weightColumnIndex);

                displayView.append("\n"+cursor.getInt(cursor.getColumnIndex(PetsEntry._ID))+"-"
                        +currentName+"-"+currentBreed+"-"+currentGender+"-"+currentWeight
                );
            }

        }
        finally {cursor.close();

        }*//*



    }*/

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu options from the res/menu/menu_catalog.xml file.
        // This adds menu items to the app bar.
        getMenuInflater().inflate(R.menu.menu_catalog, menu);
        return true;
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // User clicked on a menu option in the app bar overflow menu
        switch (item.getItemId()) {
            // Respond to a click on the "Insert dummy data" menu option
            case R.id.action_insert_dummy_data:
                insertPet();
               // displayDatabaseInfo();
                return true;
            // Respond to a click on the "Delete all entries" menu option
            case R.id.action_delete_all_entries:
                deleteAllPets();
                //displayDatabaseInfo();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void deleteAllPets() {
        int rowDelleted=getContentResolver().delete(PetsContract.CONTENT_URI,null,null);
        Log.v(String.valueOf(this),"RowDeleted "+rowDelleted);
        onLoaderReset(null);
    }

    private void insertPet() {
        ContentValues values= new ContentValues();
        values.put(PetsEntry.COLUMN_PET_NAME,"Toto");
        values.put(PetsEntry.COLUMN_PET_BREED,"Terrier");
        values.put(PetsEntry.COLUMN_PET_GENDER,PetsEntry.GENDER_MALE);
        values.put(PetsEntry.COLUMN_PET_WEIGHT,7);

        Uri newUri= getContentResolver().insert(PetsContract.CONTENT_URI,values);

    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {

        String[] projection ={
                PetsEntry._ID,
                PetsEntry.COLUMN_PET_NAME,
                PetsEntry.COLUMN_PET_BREED,
               };
        return new CursorLoader(this,
                PetsContract.CONTENT_URI,
                projection,
                null,
                null,
                null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
            mCursorAdapter.swapCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mCursorAdapter.swapCursor(null);
    }
}