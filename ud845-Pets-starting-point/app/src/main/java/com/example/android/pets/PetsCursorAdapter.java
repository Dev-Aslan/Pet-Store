package com.example.android.pets;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;

import com.example.android.pets.data.PetsContract;

public class PetsCursorAdapter extends CursorAdapter {
    public PetsCursorAdapter(Context context, Cursor c) {
        super(context, c);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return LayoutInflater.from(context).inflate(R.layout.list_item,parent,false);
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        TextView petsName=(TextView) view.findViewById(R.id.name);
        TextView petssummary = (TextView) view.findViewById(R.id.summary);
        String name= cursor.getString(cursor.getColumnIndex(PetsContract.PetsEntry.COLUMN_PET_NAME));
        String summary = cursor.getString(cursor.getColumnIndex(PetsContract.PetsEntry.COLUMN_PET_BREED));
        petsName.setText(name);
        petssummary.setText(summary);

    }
}
