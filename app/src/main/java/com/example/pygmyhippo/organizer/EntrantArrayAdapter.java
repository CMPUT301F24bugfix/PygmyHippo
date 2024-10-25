package com.example.pygmyhippo;

/*
The purpose of this class is to take the data from the entrants in the given filtered entrant list and display that
on the app listview
Author: Kori Kozicki

Outstanding Issues: Needs image functionality to be customizable, not from drawables
 */

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.pygmyhippo.common.Entrant;
import com.example.pygmyhippo.common.TESTEntrant;

import java.util.ArrayList;

/**
 * This Array Adapter is customized for the viewing entrants list
 * @author Kori
 * TODO:
 *  - Make profile image change based on the user profile
 *  - Change TESTEntrant to Entrant
 */
public class EntrantArrayAdapter extends ArrayAdapter<TESTEntrant> {

    public EntrantArrayAdapter(Context context, ArrayList<TESTEntrant> entrantListData) {
        super(context, 0, entrantListData);
    }

    /**
     * This Method will customize the entrant list depending on their profile values
     * @param position The index of the current entrant
     * @param convertView not sure
     * @param parent
     * @return view (The view of the list content)
     */
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

        // FIXME: Change when test entrant is deleted
        TESTEntrant entrant = getItem(position);

        // Get all the views from the entrants_content in our xml file
        TextView accountName = view.findViewById(R.id.o_entrant_name_view);
        TextView accountPhone = view.findViewById(R.id.o_entrant_phone_view);
        TextView accountEmail = view.findViewById(R.id.o_entrant_email_view);
        TextView accountStatus = view.findViewById(R.id.o_entrant_status_txt_view);

        // Replace the placeholder texts with the real values from this current Entrant account
        accountName.setText(entrant.getName());
        accountPhone.setText(entrant.getPhoneNumber());
        accountEmail.setText(entrant.getEmailAddress());
        accountStatus.setText("Status: " + entrant.getEntrantStatus().value);

        return view;
    }
}
