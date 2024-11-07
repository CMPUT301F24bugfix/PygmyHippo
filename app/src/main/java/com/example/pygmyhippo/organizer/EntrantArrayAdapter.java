package com.example.pygmyhippo.organizer;

/*
The purpose of this class is to take the data from the entrants in the given filtered entrant list and display that
on the app listview
Author: Kori Kozicki

Outstanding Issues: Needs image functionality to be customizable, not from drawables
 */

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.pygmyhippo.R;
import com.example.pygmyhippo.common.Account;
import com.example.pygmyhippo.common.Entrant;
import com.example.pygmyhippo.database.DBOnCompleteFlags;
import com.example.pygmyhippo.database.DBOnCompleteListener;

import java.util.ArrayList;

/**
 * This Array Adapter is customized for the viewing entrants list
 * @author Kori
 * TODO:
 *  - Make profile image change based on the user profile
 */
public class EntrantArrayAdapter extends ArrayAdapter<Entrant> {
    private ViewEntrantDB dbHandler;
    private Account account;

    public EntrantArrayAdapter(Context context, ArrayList<Entrant> entrantListData) {
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

        Entrant entrant = getItem(position);
        Log.d("DB", String.format("Finding %s Account", entrant.getAccountID()));

        // Get all the views from the entrants_content in our xml file
        TextView accountName = view.findViewById(R.id.o_entrant_name_view);
        TextView accountPhone = view.findViewById(R.id.o_entrant_phone_view);
        TextView accountEmail = view.findViewById(R.id.o_entrant_email_view);
        TextView accountStatus = view.findViewById(R.id.o_entrant_status_txt_view);
        TextView accountLocation = view.findViewById(R.id.o_entrant_location_txt_view);

        // Set placeholders while we wait for the data to be retrieved
        accountName.setText("Loading...");
        accountPhone.setText("Loading...");
        accountEmail.setText("Loading...");
        accountLocation.setText("Loading...");
        accountStatus.setText("Status: " + entrant.getEntrantStatus().value);

        // From the database, get the entrant's account info
        dbHandler = new ViewEntrantDB();
        dbHandler.getAccount(entrant.getAccountID(), new DBOnCompleteListener<Account>() {
            @Override
            public void OnComplete(@NonNull ArrayList<Account> docs, int queryID, int flags) {
                switch (queryID) {
                    case 1: // getAccount()
                        if (flags == DBOnCompleteFlags.SINGLE_DOCUMENT.value) {
                            // Get the account for this entrant
                            account = docs.get(0);

                            // Replace the placeholder texts with the real values from this current Entrant account
                            accountName.setText(account.getName());
                            accountPhone.setText(account.getPhoneNumber());
                            accountEmail.setText(account.getEmailAddress());
                            accountLocation.setText(account.getLocation());
                            Log.d("DB", String.format("Account name:  (%s)", account.getName()));
                            Log.d("DB", String.format("Account phone:  (%s)", account.getPhoneNumber()));
                            Log.d("DB", String.format("Account email:  (%s)", account.getEmailAddress()));
                        } else {
                            // Should only ever expect 1 document, otherwise there must be an error
                            handleDBError();
                        }
                        break;
                    default:
                        Log.i("DB", String.format("Unknown query ID (%d)", queryID));
                        handleDBError();
                }
                }
        });
        return view;
    }

    /**
     * This method displays if there was an error in the database
     */
    private void handleDBError() {
        Toast toast = Toast.makeText(getContext(), "DB Error!", Toast.LENGTH_SHORT);
        toast.show();
    }
}
