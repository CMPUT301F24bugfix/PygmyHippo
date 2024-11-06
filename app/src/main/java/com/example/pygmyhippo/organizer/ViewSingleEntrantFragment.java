package com.example.pygmyhippo.organizer;

/*
This Fragment is meant for the Organizer to view a single entrant from the list of entrants of their event
Will contain the functionality to filter the given list depending on the status of the entrant
Deals with user stories 02.06.04 currently
Author: Kori

Purposes:
    - Allow the organiser to view a single entrant
    - Let them see the entrant status and give them the option to cancel them if they are invited
    - Give them the option to draw a replacement winner on entrants with cancelled status
Issues:
    - No Image handling
    - Notifications haven't been dealt with yet
    - Replacement draw hasn't been dealt with yet
    - Navigating back to entrant list doesn't retain the data yet
 */

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.example.pygmyhippo.R;
import com.example.pygmyhippo.common.Account;
import com.example.pygmyhippo.common.Entrant;
import com.example.pygmyhippo.common.Event;
import com.example.pygmyhippo.database.DBOnCompleteFlags;
import com.example.pygmyhippo.database.DBOnCompleteListener;
import com.example.pygmyhippo.databinding.OrganiserViewSingleEntrantBinding;
import com.example.pygmyhippo.databinding.UserFragmentProfileBinding;

import java.util.ArrayList;

/**
 * Fragment to see a user profile from navigating from an organiser's list of entrants
 * TODO: Add image handling
 */
public class ViewSingleEntrantFragment extends Fragment implements DBOnCompleteListener{
    private Account account;
    private String status;
    private String eventID;
    private ViewEntrantDB dbHandler;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        OrganiserViewSingleEntrantBinding binding = OrganiserViewSingleEntrantBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        // Get the account and entrant status passed from the ViewEntrantsFragment
        String accountID = getArguments().getString("accountID");
        status = getArguments().getString("status");
        eventID = getArguments().getString("eventID");

        // Get the textviews and buttons
        // TODO: Would also get for image here
        ImageButton backButton = binding.entrantViewBackButton;
        TextView userNameView = binding.eUsername;
        TextView pronounsTextView = binding.entrantProunouns;
        TextView emailTextView = binding.entrantEmail;
        TextView phoneTextView = binding.entrantPhone;
        TextView statusTextView = binding.entrantStatus;
        Button statusButton = binding.eStatusButton;

        // Set the status indication
        statusTextView.setText(status);

        // Get the account from the database
        dbHandler = new ViewEntrantDB();
        dbHandler.getAccount(accountID, new DBOnCompleteListener<Account>() {
            @Override
            public void OnComplete(@NonNull ArrayList<Account> docs, int queryID, int flags) {
                if (flags == DBOnCompleteFlags.SINGLE_DOCUMENT.value) {
                    // Get the event for this list of entrants and initialize the list
                    account = docs.get(0);

                    // Set the text views once we get the data
                    userNameView.setText(account.getName());
                    pronounsTextView.setText(account.getPronouns());
                    emailTextView.setText(account.getEmailAddress());
                    phoneTextView.setText(account.getPhoneNumber());
                } else {
                    // Should only ever expect 1 document, otherwise there must be an error
                    handleDBError();
                }
            }
        });

        // Depending on entrant status, display and/or alter the status button
        // TODO: Add settings for other statuses and provide more functionalities than just cancel
        if (status.equals("invited")) {
            // If the entrant is invited, then display the button to allow cancelling them
            statusButton.setClickable(true);
            statusButton.setVisibility(View.VISIBLE);

            // Set the listener so it'll change the status of the entrant to cancelled
            statusButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    // First change the display manually
                    statusTextView.setText("cancelled");

                    // Get the event from the database and change the status of the entrant there
                    dbHandler.getEvent(eventID, this);
                }
            });
        }

        // Set up an onclick listener to go back to the list
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Navigation.findNavController(view).navigate(R.id.action_single_entrant_fragment_to_view_entrants_fragment);
            }
        });

        return root;
    }

    private void handleDBError() {
        Toast toast = Toast.makeText(getContext(), "DB Error!", Toast.LENGTH_SHORT);
        toast.show();
    }

    @Override
    public void OnComplete(@NonNull ArrayList<Event> docs, int queryID, int flags) {
        if (flags == DBOnCompleteFlags.SINGLE_DOCUMENT.value) {
            // Get the event for this list of entrants and initialize the list
            Event event = docs.get(0);

        } else {
            // Should only ever expect 1 document, otherwise there must be an error
            handleDBError();
        }
    }
}
