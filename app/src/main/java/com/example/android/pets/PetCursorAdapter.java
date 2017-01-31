package com.example.android.pets;

import android.content.Context;
import android.database.Cursor;
import android.support.v4.widget.CursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.android.pets.data.PetContract.PetEntry;

import org.w3c.dom.Text;

/**
 * Created by root on 1/30/17.
 */

public class PetCursorAdapter extends CursorAdapter {


    public PetCursorAdapter(Context context, Cursor cursor) {
        super(context, cursor, 0);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
       return LayoutInflater.from(context).inflate(R.layout.list_item, parent, false);

    }




    @Override
    public void bindView(View view, Context context, Cursor cursor) {

        String name = cursor.getString(cursor.getColumnIndex(PetEntry.COLUMN_NAME_NAME));
        String breed = cursor.getString(cursor.getColumnIndex(PetEntry.COLUMN_NAME_BREED));

        TextView nameTextView = (TextView) view.findViewById(R.id.textView_petName);
        TextView breedTextView = (TextView) view.findViewById(R.id.textView_petBreed);

        nameTextView.setText(name);
        breedTextView.setText(breed);

    }
}
