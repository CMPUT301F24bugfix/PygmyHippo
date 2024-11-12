package com.example.pygmyhippo.organizer;

/*
This Fragment is meant for the Organizer to view a single entrant from the list of entrants of their event
Will contain the functionality to filter the given list depending on the status of the entrant
Deals with user stories 02.06.04 currently
Author: Kori

Purposes:
    - Allow the organiser to view a single entrant
    - Let them see the entrant status and give them the option to cancel them if they are invited (updated in database)
    - Give them the option to draw a replacement winner on entrants with cancelled status
Issues:
    - No Image handling
    - Notifications haven't been dealt with yet
    - Replacement draw hasn't been dealt with yet
 */

import android.os.Bundle;
import android.util.Log;
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
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.example.pygmyhippo.common.Account;
import com.example.pygmyhippo.common.Entrant;
import com.example.pygmyhippo.common.Event;
import com.example.pygmyhippo.database.DBOnCompleteFlags;
import com.example.pygmyhippo.database.DBOnCompleteListener;
import com.example.pygmyhippo.databinding.OrganiserViewSingleEntrantBinding;

import java.util.ArrayList;

/**
 * Fragment to see a user profile from navigating from an organiser's list of entrants
 * TODO: Add image handling
 * TODO: Add functions for the other status conditions (accepted and such)
 * TODO: Notification sending can also be done when one of the buttons are pressed (just one possible idea)
 */
public class ViewSingleEntrantFragment extends Fragment {
    private Account account;
    private String status;
    private String eventID;
    private ViewEntrantDB dbHandler;
    private NavController navController;

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        navController = Navigation.findNavController(view);
    }

    /**
     * In on create View, the text fields are updated and on click listeners are set
     * @param inflater The LayoutInflater object that can be used to inflate
     * any views in the fragment,
     * @param container If non-null, this is the parent view that the fragment's
     * UI should be attached to.  The fragment should not add the view itself,
     * but this can be used to generate the LayoutParams of the view.
     * @param savedInstanceState If non-null, this fragment is being re-constructed
     * from a previous saved state as given here.
     *
     * @return the view
     */
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
            public void OnCompleteDB(@NonNull ArrayList<Account> docs, int queryID, int flags) {
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
                    dbHandler.getEvent(eventID, new DBOnCompleteListener<Event>() {
                        @Override
                        public void OnCompleteDB(@NonNull ArrayList<Event> docs, int queryID, int flags) {
                            if (flags == DBOnCompleteFlags.SINGLE_DOCUMENT.value) {
                                // Get the event for this list of entrants
                                Event event = docs.get(0);
                                ArrayList<Entrant> entrants = event.getEntrants();

                                // Find the entrant in the list and update their status
                                findAndUpdateEntrant(accountID, event, "cancelled");
                            } else {
                                // Should only ever expect 1 document, otherwise there must be an error
                                handleDBError();
                            }
                        }
                    });

                    // Make the button disappear after the click
                    statusButton.setClickable(false);
                    statusButton.setVisibility(View.GONE);
                }
            });
        }

        // Set up an onclick listener to go back to the list
        Bundle navArgs = new Bundle();
        navArgs.putString("eventID", eventID);
        backButton.setOnClickListener(view -> navController.popBackStack());

        return root;
    }

    /**
     * This method will find and update the status of the given entrant and reflect the changes to the database
     * @author Kori
     * @param accountID The ID of the entrant we want to update
     * @param event The event we want to update
     * @param newStatus The new status we want to give the entrant
     */
    public void findAndUpdateEntrant(String accountID, Event event, String newStatus) {
        // Loop through all the entrants until you find the given one with the accountID
        // Note that there should be no duplicates as accountID should be unique
        for (int index = 0; index < event.getEntrants().size(); index++) {
            if (event.getEntrants().get(index).getAccountID().equals(accountID)) {
                // Found the entrant, so update there status in the event
                event.getEntrants().get(index).setEntrantStatus(Entrant.EntrantStatus.valueOf(newStatus));
                break;
            }
        }

        // Now send this updated event to the database to update it
        dbHandler.updateEvent(event, new DBOnCompleteListener<Event>() {
            @Override
            public void OnCompleteDB(@NonNull ArrayList<Event> docs, int queryID, int flags) {
                // Log when the data is updated or catch if there was an error
                if (flags == DBOnCompleteFlags.SUCCESS.value) {
                    Log.d("DB", String.format("Successfully finished updating event with ID (%s).", eventID));
                } else {
                    // If not the success flag, then there was an error
                    handleDBError();
                }
            }
        });
    }

    /**
     * Displays DB Errors
     */
    private void handleDBError() {
        Toast toast = Toast.makeText(getContext(), "DB Error!", Toast.LENGTH_SHORT);
        toast.show();
    }
}
