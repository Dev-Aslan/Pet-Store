package com.example.android.pets.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.support.annotation.Nullable;

public class PetsDbHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME="shelter.db";
    private static final int DATABASE_VERSION=1;
    public PetsDbHelper(@Nullable Context context) {
        super(context, DATABASE_NAME,null,DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String SQL_CREATE_PETS_TABLE="CREATE TABLE "+ PetsContract.PetsEntry.TABLE_NAME+"("
                + PetsContract.PetsEntry._ID+" INTEGER PRIMARY KEY AUTOINCREMENT,"
                + PetsContract.PetsEntry.COLUMN_PET_NAME+" TEXT NOT NULL,"
                + PetsContract.PetsEntry.COLUMN_PET_BREED+" TEXT,"
                + PetsContract.PetsEntry.COLUMN_PET_GENDER+" TEXT NOT NULL,"
                + PetsContract.PetsEntry.COLUMN_PET_WEIGHT+" INT NOT NULL DEFAULT 0);";
        db.execSQL(SQL_CREATE_PETS_TABLE);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
