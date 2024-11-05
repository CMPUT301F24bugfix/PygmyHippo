package com.example.pygmyhippo.organizer;

/*
This Fragment is meant for the Organizer to view the users who entered the waitlist on an event the Organizer made.
Will contain the functionality to filter the given list depending on the status of the entrant
Deals with user stories 02.06.01, 02.06.02, 02.06.03, and 02.02.01
Author: Kori

Issues:
      No Image handling
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
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.example.pygmyhippo.R;
import com.example.pygmyhippo.common.Account;
import com.example.pygmyhippo.common.Entrant;
import com.example.pygmyhippo.common.Event;
import com.example.pygmyhippo.database.DBConnector;
import com.example.pygmyhippo.database.DBOnCompleteFlags;
import com.example.pygmyhippo.database.DBOnCompleteListener;
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
 */
public class ViewEntrantsFragment extends Fragment implements DBOnCompleteListener<Event> {
    private ArrayList<Entrant> entrantListData = new ArrayList<Entrant>();
    private ArrayAdapter<Entrant> entrantListAdapter;
    private ListView entrantListView;
    private Spinner statusSpinner;
    private ImageButton backButton;
    private String eventID;
    private Event event = new Event();
    private ViewEntrantDB dbHandler;

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

        // Get the event ID that was passed from the last fragment
        eventID = getArguments().getString("eventID");

        // Get the event from the database
        dbHandler = new ViewEntrantDB();
        dbHandler.getEvent(eventID, this);

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

        return view;
    }

    /**
     * on View created we want to set up most of our click listeners (especially for the spinner)
     * @param view The View returned by {@link #onCreateView(LayoutInflater, ViewGroup, Bundle)}.
     * @param savedInstanceState If non-null, this fragment is being re-constructed
     * from a previous saved state as given here.
     */
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Initialize our ListView
        entrantListView = view.findViewById(R.id.o_entrant_listview);

        // Set up our array adapter and connect it to our listView
        entrantListAdapter = new com.example.pygmyhippo.organizer.EntrantArrayAdapter(view.getContext(), entrantListData);
        entrantListView.setAdapter(entrantListAdapter);

        // Get and set up navigation for the back button
        backButton = view.findViewById(R.id.o_entrant_view_back_button);
        backButton.setOnClickListener(view1 -> {
            Navigation.findNavController(view1).navigate(R.id.action_view_entrants_fragment_to_event_fragment);
        });

        // Set up on click listeners for the spinner to filter the list
        statusSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                if (position == 0) {
                    // Waitlist was selected
                    ArrayList<Entrant> filterList = setEntrantWaitList(event.getEntrants());

                    // Set the new data and notify the adapter
                    entrantListAdapter.clear();
                    entrantListAdapter.addAll(filterList);
                    entrantListAdapter.notifyDataSetChanged();
                } else if (position == 1) {
                    // Invited was selected
                    ArrayList<Entrant> filterList = setEntrantInvited(event.getEntrants());

                    // Set the new data and notify the adapter
                    entrantListAdapter.clear();
                    entrantListAdapter.addAll(filterList);
                    entrantListAdapter.notifyDataSetChanged();
                } else if (position == 2) {
                    // Cancelled was selected
                    ArrayList<Entrant> filterList = setEntrantCancelled(event.getEntrants());

                    // Set the new data and notify the adapter
                    entrantListAdapter.clear();
                    entrantListAdapter.addAll(filterList);
                    entrantListAdapter.notifyDataSetChanged();
                } else if (position == 3) {
                    // Accepted was selected
                    ArrayList<Entrant> filterList = setEntrantAccepted(event.getEntrants());

                    // Set the new data and notify the adapter
                    entrantListAdapter.clear();
                    entrantListAdapter.addAll(filterList);
                    entrantListAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                // Do nothing
            }
        });
    }

    /**
     * Callback called when view entrant DB queries complete.
     * @param docs - Documents retrieved from DB (if it was a get query).
     * @param queryID - ID of query completed.
     * @param flags - Flags to indicate query status/set how to process query result.
     */
    @Override
    public void OnComplete(@NonNull ArrayList<Event> docs, int queryID, int flags) {
        switch (queryID) {
            case 0: // getEvent()
                if (flags == DBOnCompleteFlags.SINGLE_DOCUMENT.value) {
                    // Get the event for this list of entrants and initialize the list
                    event = docs.get(0);
                    entrantListData = event.getEntrants();

                    // Initially set the filter to waitlist
                    ArrayList<Entrant> filterList = setEntrantWaitList(entrantListData);

                    // Set the new data and notify the adapter
                    entrantListAdapter.clear();
                    entrantListAdapter.addAll(filterList);
                    entrantListAdapter.notifyDataSetChanged();
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

    private void handleDBError() {
        Toast toast = Toast.makeText(getContext(), "DB Error!", Toast.LENGTH_SHORT);
        toast.show();
    }

    /**
     * This method takes the entrant list and returns the data list to contain only those with waitlist status
     * @author Kori
     * @param allEntrants
     *          The complete unfiltered list of entrants
     * @return filterList
     */
    public ArrayList<Entrant> setEntrantWaitList(ArrayList<Entrant> allEntrants) {
        Integer index = 0;

        // Make a new list that will hold our results
        ArrayList<Entrant> filterList = new ArrayList<>();

        while (index < allEntrants.size()) {
            // Check if the current entrant status is waitlisted
            if (allEntrants.get(index).getEntrantStatus().value.equals("waitlisted")) {
                // Add them to the list to display
                filterList.add(allEntrants.get(index));
            }

            index++;
        }

        // Return the filter list
        return filterList;
    }

    /**
     * This method takes the entrant list and returns the data list to contain only those with invited status
     * @author Kori
     * @param allEntrants
     * @return filterList
     */
    public ArrayList<Entrant> setEntrantInvited(ArrayList<Entrant> allEntrants) {
        Integer index = 0;

        // Make a new list that will hold our results
        ArrayList<Entrant> filterList = new ArrayList<>();

        while (index < allEntrants.size()) {
            // Check if the current entrant status is waitlisted
            if (allEntrants.get(index).getEntrantStatus().value.equals("invited")) {
                // Add them to the list to display
                filterList.add(allEntrants.get(index));
            }

            index++;
        }

        return filterList;
    }

    /**
     * This method takes the entrant list and returns the data list to contain only those with cancelled status
     * @author Kori
     * @param allEntrants
     * @return filterList
     */
    public ArrayList<Entrant> setEntrantCancelled(ArrayList<Entrant> allEntrants) {
        Integer index = 0;

        // Make a new list that will hold our results
        ArrayList<Entrant> filterList = new ArrayList<>();

        while (index < allEntrants.size()) {
            // Check if the current entrant status is waitlisted
            if (allEntrants.get(index).getEntrantStatus().value.equals("cancelled")) {
                // Add them to the list to display
                filterList.add(allEntrants.get(index));
            }

            index++;
        }

        return filterList;
    }

    /**
     * This method takes the entrant list and returns the data list to contain only those with accepted status
     * @author Kori
     * @param allEntrants
     * @return filterList
     */
    public ArrayList<Entrant> setEntrantAccepted(ArrayList<Entrant> allEntrants) {
        Integer index = 0;

        // Make a new list that will hold our results
        ArrayList<Entrant> filterList = new ArrayList<>();

        while (index < allEntrants.size()) {
            // Check if the current entrant status is waitlisted
            if (allEntrants.get(index).getEntrantStatus().value.equals("accepted")) {
                // Add them to the list to display
                filterList.add(allEntrants.get(index));
            }

            index++;
        }

        return filterList;
    }
}
