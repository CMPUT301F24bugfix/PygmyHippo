package com.example.pygmyhippo;

/*
This Activity is meant for the Organizer to view the users who entered the waitlist on an event the Organizer made.
Will contain the functionality to filter the given list depending on the status of the entrant
Deals with user stories 02.06.01, 02.06.02, 02.06.03, and 02.02.01
Author: Kori Kozicki

Current Issues: Navigation to this activity
                No image handling
                Data is hardcoded, it's not passed by any other fragment yet
 */

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.pygmyhippo.common.Account;
import com.example.pygmyhippo.common.Entrant;
import com.example.pygmyhippo.databinding.OrganiserFragmentMyeventsBinding;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.Arrays;

public class oViewEntrantsFragment extends Fragment {
    private ArrayList<Entrant> entrantListData = new ArrayList<Entrant>();
    private ArrayAdapter<Entrant> entrantListAdapter;
    private ListView entrantListView;
    private Spinner status_spinner;
    private FirebaseFirestore db;
    private CollectionReference entrantsRef;
    private OrganiserFragmentMyeventsBinding binding;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.o_view_entrants, container, false);

        // Set up our database instance and reference to entrants
        db = FirebaseFirestore.getInstance();
        //entrantsRef = db.collection("Entrants");

        // Get the spinner view
        status_spinner = view.findViewById(R.id.o_entrant_list_spinner);

        // Make an adapter to connect to our spinner with our specified layouts
        ArrayAdapter<CharSequence> o_spinner_adapter = ArrayAdapter.createFromResource(
                view.getContext(),
                R.array.entrant_status,
                R.layout.e_p_role_dropdown
        );

        // Attatch the adapter to the spinner
        o_spinner_adapter.setDropDownViewResource(R.layout.e_p_role_dropdown);
        status_spinner.setAdapter(o_spinner_adapter);

        // Set up the list and adapter
        //FIXME: A sample array used to look at this fragment
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
                new ArrayList<>(Arrays.asList(Account.AccountRole.user, Account.AccountRole.organizer)),  // roles
                Account.AccountRole.user,  // currentRole (TODO: Change this if you want to test with user)
                null  // facilityProfile
                );
        entrant.setEntrantStatus(Entrant.EntrantStatus.waitlisted);
        entrantListData.add(entrant);

        // Initialize our ListView
        entrantListView = view.findViewById(R.id.o_entrant_listview);

        // Set up our array adapter and connect it to our listView
        entrantListAdapter = new com.example.pygmyhippo.oEntrantArrayAdapter(view.getContext(), entrantListData);
        entrantListView.setAdapter(entrantListAdapter);

        return view;
    }
}
