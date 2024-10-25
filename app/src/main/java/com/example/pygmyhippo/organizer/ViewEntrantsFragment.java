package com.example.pygmyhippo.organizer;

/*
This Fragment is meant for the Organizer to view the users who entered the waitlist on an event the Organizer made.
Will contain the functionality to filter the given list depending on the status of the entrant
Deals with user stories 02.06.01, 02.06.02, 02.06.03, and 02.02.01
Author: Kori Kozicki

Issues: Navigation to and from this activity
      No Image handling
      Data is hardcoded, so should work on data passing from fragments
      Need to connect to db and actually get the list filter working
      Get Event ID somewhere for querying the users
 */

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Spinner;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.example.pygmyhippo.R;
import com.example.pygmyhippo.common.Account;
import com.example.pygmyhippo.common.Entrant;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * This fragment allows the Organizer to view the entrants who signed up for their event
 * @author Kori
 * TODO:
 *  - Set up to DB
 *  - Get list filtering working
 *  - Set up so not hardcoded examples
 */
public class ViewEntrantsFragment extends Fragment {
    private ArrayList<Entrant> entrantListData = new ArrayList<Entrant>();
    private ArrayAdapter<Entrant> entrantListAdapter;
    private ListView entrantListView;
    private Spinner statusSpinner;
    private ImageButton backButton;

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

        //FIXME: A sample entrant/list used to look at this fragment
        Entrant entrant = new Entrant("id",
                "Cool Name",
                "she/her",
                "780 666 3333",
                "user@gmail.com",
                "DEVICEID",
                "Enter image here",
                "Edmonton AB",
                true,
                false,
                new ArrayList<>(Arrays.asList(Account.AccountRole.user, Account.AccountRole.organiser)),  // roles
                Account.AccountRole.user,  // currentRole
                null  // facilityProfile
        );
        entrant.setEntrantStatus(Entrant.EntrantStatus.waitlisted);
        entrantListData.add(entrant); // adding test entrant to list

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

        return view;
    }
}
