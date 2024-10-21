package com.example.pygmyhippo;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.ArrayList;

/*
The purpose of this class is to take the data from the users in the given filtered list and display that
on the app listview

Outstanding Issues: Needs work everywhere, implement the list and filtering features. Needs picture functionality
                    Need to finish account class to utilize getters and setters
 */
public class oEntrantArrayAdapter extends ArrayAdapter<Entrant> {
    /*
    Methods:
        - An overridden getView method used to format the info displayed per list entry
     */

    public oEntrantArrayAdapter(Context context, ArrayList<Entrant> entrantListData) {
        super(context, 0, entrantListData);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        /*
        This method overrides the getView method so that we can customize it to display multiple textViews
         */
        View view;

        if (convertView == null) {
            view = LayoutInflater.from(getContext()).inflate(R.layout.o_view_entrants_content, parent, false);
        } else {
            view = convertView;
        }

        // Get the current Account we want to display
        Entrant entrant = getItem(position);

        // Get all the views from the entrants_content in our xml file
        TextView accountName = view.findViewById(R.id.o_entrant_name_view);
        TextView accountPhone = view.findViewById(R.id.o_entrant_phone_view);
        TextView accountEmail = view.findViewById(R.id.o_entrant_email_view);
        TextView accountStatus = view.findViewById(R.id.o_entrant_status_txt_view);

        // Replace the placeholder texts with the real values from this current Account
        accountName.setText(entrant.getName());
        accountPhone.setText(entrant.getPhoneNumber());
        accountEmail.setText(entrant.getEmailAddress());

        return view;
    }
}
