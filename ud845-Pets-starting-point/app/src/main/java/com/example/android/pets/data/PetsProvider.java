package com.example.android.pets.data;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.Selection;
import android.util.Log;
import android.widget.Toast;

public class PetsProvider extends ContentProvider {
    private PetsDbHelper mDbHelper;
    private static final UriMatcher sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
    private static final int PETS = 100;
    private static final int PETS_ID = 101;

    static {
        // The calls to addURI() go here, for all of the content URI patterns that the provider
        // should recognize. All paths added to the UriMatcher have a corresponding code to return
        // when a match is found.
        sUriMatcher.addURI(PetsContract.CONTENT_AUTHORITY, PetsContract.PATH_PETS, PETS);
        sUriMatcher.addURI(PetsContract.CONTENT_AUTHORITY,PetsContract.PATH_PETS+"/#",PETS_ID);
    }

    @Override
    public boolean onCreate() {
        mDbHelper = new PetsDbHelper(getContext());
        return false;
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] projection, @Nullable String selection, @Nullable String[] selectionArgs, @Nullable String sortOrder) {
        SQLiteDatabase database = mDbHelper.getReadableDatabase();

        Cursor cursor;
        Log.v(String.valueOf(this),"uri="+uri);
        int match = sUriMatcher.match(uri);

        switch (match) {
            case PETS:
                cursor = database.query(PetsContract.PetsEntry.TABLE_NAME, projection, selection, selectionArgs, null, null, null);
                break;
            case PETS_ID:
                selection = PetsContract.PetsEntry._ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                cursor = database.query(PetsContract.PetsEntry.TABLE_NAME, projection, selection, selectionArgs, null, null, null);
                break;
                default:
                throw new IllegalArgumentException("unknown Uri " + uri);
        }
        // Set the notification URI on the Cursor
        // So we know that content URI the Cursor was created for
        // If the data at this URI changes ,then we know we need to update the Cursor.
        cursor.setNotificationUri(getContext().getContentResolver(),uri);
        return cursor;
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        int match = sUriMatcher.match(uri);
        switch (match) {
            case PETS:
                return PetsContract.PetsEntry.CONTENT_LIST_TYPE;
            case PETS_ID:
                return PetsContract.PetsEntry.CONTENT_ITEM_TYPE;
            default:
                throw new IllegalArgumentException("unknown URI " + uri);
        }


    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues values) {

        int match = sUriMatcher.match(uri);
        switch (match) {
            case PETS:
                return insertPets(uri, values);
            default:
                throw new IllegalArgumentException("insertion not possible " + uri);
        }
    }

    private Uri insertPets(Uri uri, ContentValues values) {
        String name = values.getAsString(PetsContract.PetsEntry.COLUMN_PET_NAME);
        if (name == null) {
            throw new IllegalArgumentException("Name of the pet can't be null");
        }
        int weight = values.getAsInteger(PetsContract.PetsEntry.COLUMN_PET_WEIGHT);
        if (weight < 0) {
            throw new IllegalArgumentException("Weight can't be negative");
        }
        SQLiteDatabase database = mDbHelper.getWritableDatabase();
        long row = database.insert(PetsContract.PetsEntry.TABLE_NAME, null, values);
        if (row == -1) {//Toast.makeText(getContext(),"Pet Saved",Toast.LENGTH_SHORT).show();
            Log.e(String.valueOf(this), "Failed to insert " + uri);
        }

        //Notify all Listeners that the data has changed for the pet content URI
        getContext().getContentResolver().notifyChange(uri,null);
        return ContentUris.withAppendedId(uri, row);
    }


    @Override
    public int delete(@NonNull Uri uri, @Nullable String selection, @Nullable String[] selectionArgs) {
        SQLiteDatabase database = mDbHelper.getWritableDatabase();

        final int match = sUriMatcher.match(uri);
        switch (match) {
            case PETS:
                // Delete all rows that match the selection and selection args
                return database.delete(PetsContract.PetsEntry.TABLE_NAME, selection, selectionArgs);
            case PETS_ID:
                // Delete a single row given by the ID in the URI
                selection = PetsContract.PetsEntry._ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                int rowsDeleted = database.delete(PetsContract.PetsEntry.TABLE_NAME, selection, selectionArgs);
                if(rowsDeleted!=0)
                    getContext().getContentResolver().notifyChange(uri,null);
                return rowsDeleted;
                default:
                throw new IllegalArgumentException("Deletion is not supported for " + uri);
        }

    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues values, @Nullable String selection, @Nullable String[] selectionArgs) {
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case PETS:
                return updatePet(uri, values, selection, selectionArgs);

            case PETS_ID:
                selection = PetsContract.PetsEntry._ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                return updatePet(uri, values, selection, selectionArgs);
            default:
                throw new IllegalArgumentException("Update is not possible for " + uri);

        }


    }

    private int updatePet(Uri uri, ContentValues values, String selection, String[] selectionArgs) {

        SQLiteDatabase database = mDbHelper.getWritableDatabase();
        int rowUpdated= database.update(PetsContract.PetsEntry.TABLE_NAME, values, selection, selectionArgs);
        if(rowUpdated!=0)
            getContext().getContentResolver().notifyChange(uri,null);

        return rowUpdated;

    }
}
