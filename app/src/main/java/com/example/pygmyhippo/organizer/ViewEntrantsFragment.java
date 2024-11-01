package com.example.pygmyhippo.organizer;

/*
This Fragment is meant for the Organizer to view the users who entered the waitlist on an event the Organizer made.
Will contain the functionality to filter the given list depending on the status of the entrant
Deals with user stories 02.06.01, 02.06.02, 02.06.03, and 02.02.01
Author: Kori Kozicki

Issues: Navigation to and from this activity
      No Image handling
      Entrant ID is hardcoded, need to make the fragment that leads to this one
 */

import static android.content.ContentValues.TAG;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Spinner;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.example.pygmyhippo.R;
import com.example.pygmyhippo.common.Entrant;
import com.example.pygmyhippo.database.DBConnector;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

/**
 * This fragment allows the Organizer to view the entrants who signed up for their event
 * @author Kori
 * TODO:
 *  - Add image handling from database
 *  - Set up so not hardcoded event ID
 *  - Maybe consider using entrant list from event instead
 */
public class ViewEntrantsFragment extends Fragment {
    private ArrayList<Entrant> entrantListData = new ArrayList<Entrant>();
    private ArrayAdapter<Entrant> entrantListAdapter;
    private ListView entrantListView;
    private Spinner statusSpinner;
    private ImageButton backButton;
    private DBConnector dbConnector = new DBConnector();
    private CollectionReference entrantsRef;

    /**
     * OnCreateView sets up the interactables on viewEntrants page and deals with the list data
     * @param inflater The LayoutInflater object that can be used to inflate
     * any views in the fragment,
     * @param container If non-null, this is the parent view that the fragment's
     * UI should be attached to.  The fragment should not add the view itself,
     * but this can be used to generate the LayoutParams of the view.
     * @param savedInstanceState If non-null, this fragment is being re-constructed
     * from a previous saved state as given here.
     * @author Kori
     * @return view
     */
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.o_view_entrants, container, false);

        // FIXME: This is a test eventID for now. Later the event will be passed to this fragment
        //      And the list will be updated from the event object
        String eventID = "event1";

        // Get the spinner view
        statusSpinner = view.findViewById(R.id.o_entrant_list_spinner);

        // Make an adapter to connect to our spinner with our specified layouts
        ArrayAdapter<CharSequence> o_spinner_adapter = ArrayAdapter.createFromResource(
                view.getContext(),
                R.array.entrant_status,
                R.layout.e_p_role_dropdown
        );

        // Attatch the adapter to the spinner
        o_spinner_adapter.setDropDownViewResource(R.layout.e_p_role_dropdown);
        statusSpinner.setAdapter(o_spinner_adapter);

        // Initialize our ListView
        entrantListView = view.findViewById(R.id.o_entrant_listview);

        // Set up our array adapter and connect it to our listView
        entrantListAdapter = new com.example.pygmyhippo.EntrantArrayAdapter(view.getContext(), entrantListData);
        entrantListView.setAdapter(entrantListAdapter);

        // Get and set up navigation for the back button
        backButton = view.findViewById(R.id.o_entrant_view_back_button);
        backButton.setOnClickListener(view1 -> {
            //FIXME: Navigate back for now, but eventually should navigate to an event view
            Navigation.findNavController(view1).navigate(R.id.action_ViewEntrantsFragment_to_navigation_home);
        });

        // Connect to the database and get a collection reference to Entrants
        dbConnector.DBConnect();
        entrantsRef = dbConnector.getDB().collection("Entrants");

        // Update the list from the database initially
        setEntrantWaitList(entrantListData, eventID);

        // Set up on click listeners for the spinner to filter the list
        statusSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                if (position == 0) {
                    // Waitlist was selected
                    setEntrantWaitList(entrantListData, eventID);
                } else if (position == 1) {
                    // Invited was selected
                    setEntrantInvited(entrantListData, eventID);
                } else if (position == 2) {
                    // Cancelled was selected
                    setEntrantCancelled(entrantListData, eventID);
                } else if (position == 3) {
                    // Accepted was selected
                    setEntrantAccepted(entrantListData, eventID);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                // Do nothing
            }
        });

        return view;
    }

    /**
     * This method will take our list and update it with waitlisted entrants
     * @author Kori
     * @param entrantListData Or data list we want to update
     * @param eventID The id of the event the entrants belong to
     */
    public void setEntrantWaitList(ArrayList<Entrant> entrantListData, String eventID) {
        // Learned how to make these queries from https://firebase.google.com/docs/firestore/query-data/queries
        // Accessed on Oct 27th, 2024
        entrantsRef.whereEqualTo("eventID", eventID).whereEqualTo("entrantStatus", "waitlisted")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            // Clear the list first
                            entrantListData.clear();

                            // Go through each matching document
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                // Convert the document data into an Entrant object
                                Entrant entrant = new Entrant((String) document.get("eventID"), (String) document.get("accountID"), (String) document.get("name"), (String) document.get("emailAddress"), (String) document.get("phoneNumber"));
                                entrant.setEntrantStatus(Entrant.EntrantStatus.valueOf((String) document.get("entrantStatus")));
                                // Add that entrant to the list we want to display
                                entrantListData.add(entrant);

                                Log.d(TAG, document.getId() + " => " + document.getData());
                            }
                            // Notify array of changes
                            entrantListAdapter.notifyDataSetChanged();
                        } else {
                            Log.d(TAG, "Error getting documents: ", task.getException());
                        }
                    }
                });
    }

    /**
     * This method will take our list and update it with invited entrants
     * @author Kori
     * @param entrantListData Or data list we want to update
     * @param eventID The id of the event the entrants belong to
     */
    public void setEntrantInvited(ArrayList<Entrant> entrantListData, String eventID) {
        entrantsRef.whereEqualTo("eventID", eventID).whereEqualTo("entrantStatus", "invited")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            // Clear the list first
                            entrantListData.clear();

                            // Go through each matching document
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                // Convert the document data into an Entrant object
                                Entrant entrant = new Entrant((String) document.get("eventID"), (String) document.get("accountID"), (String) document.get("name"), (String) document.get("emailAddress"), (String) document.get("phoneNumber"));
                                entrant.setEntrantStatus(Entrant.EntrantStatus.valueOf((String) document.get("entrantStatus")));
                                // Add that entrant to the list we want to display
                                entrantListData.add(entrant);

                                Log.d(TAG, document.getId() + " => " + document.getData());
                            }
                            // Notify array of changes
                            entrantListAdapter.notifyDataSetChanged();
                        } else {
                            Log.d(TAG, "Error getting documents: ", task.getException());
                        }
                    }
                });
    }

    /**
     * This method will take our list and update it with cancelled entrants
     * @author Kori
     * @param entrantListData Or data list we want to update
     * @param eventID The id of the event the entrants belong to
     */
    public void setEntrantCancelled(ArrayList<Entrant> entrantListData, String eventID) {
        entrantsRef.whereEqualTo("eventID", eventID).whereEqualTo("entrantStatus", "cancelled")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            // Clear the list first
                            entrantListData.clear();

                            // Go through each matching document
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                // Convert the document data into an Entrant object
                                Entrant entrant = new Entrant((String) document.get("eventID"), (String) document.get("accountID"), (String) document.get("name"), (String) document.get("emailAddress"), (String) document.get("phoneNumber"));
                                entrant.setEntrantStatus(Entrant.EntrantStatus.valueOf((String) document.get("entrantStatus")));
                                // Add that entrant to the list we want to display
                                entrantListData.add(entrant);

                                Log.d(TAG, document.getId() + " => " + document.getData());
                            }
                            // Notify array of changes
                            entrantListAdapter.notifyDataSetChanged();
                        } else {
                            Log.d(TAG, "Error getting documents: ", task.getException());
                        }
                    }
                });
    }

    /**
     * This method will take our list and update it with accepted entrants
     * @author Kori
     * @param entrantListData Or data list we want to update
     * @param eventID The id of the event the entrants belong to
     */
    public void setEntrantAccepted(ArrayList<Entrant> entrantListData, String eventID) {
        entrantsRef.whereEqualTo("eventID", eventID).whereEqualTo("entrantStatus", "accepted")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            // Clear the list first
                            entrantListData.clear();

                            // Go through each matching document
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                // Convert the document data into an Entrant object
                                Entrant entrant = new Entrant((String) document.get("eventID"), (String) document.get("accountID"), (String) document.get("name"), (String) document.get("emailAddress"), (String) document.get("phoneNumber"));
                                entrant.setEntrantStatus(Entrant.EntrantStatus.valueOf((String) document.get("entrantStatus")));
                                // Add that entrant to the list we want to display
                                entrantListData.add(entrant);

                                Log.d(TAG, document.getId() + " => " + document.getData());
                            }
                            // Notify array of changes
                            entrantListAdapter.notifyDataSetChanged();
                        } else {
                            Log.d(TAG, "Error getting documents: ", task.getException());
                        }
                    }
                });
    }
}
