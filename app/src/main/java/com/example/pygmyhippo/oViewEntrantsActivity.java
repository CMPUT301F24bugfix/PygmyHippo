package com.example.pygmyhippo;

/*
This Activity is meant for the Organizer to view the users who entered the waitlist on an event the Organizer made.
Will contain the functionality to filter the given list depending on the status of the entrant
Deals with user stories 02.06.01, 02.06.02, 02.06.03, and 02.02.01
Author: Kori Kozicki

Current Issues: Navigation to this activity and source of user list
                No image handling
                Still needs status change shown
                Data is hardcoded, it's not passed by any other fragment yet
 */

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Spinner;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import java.util.ArrayList;

public class oViewEntrantsActivity extends Fragment {
    private ArrayList<Entrant> entrantListData = new ArrayList<Entrant>();
    private ArrayAdapter<Entrant> entrantListAdapter;
    private ListView entrantListView;
    private Spinner status_spinner;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.o_view_entrants, container, false);

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
                false);
        entrant.setEntrantStatus(Entrant.EntrantStatus.waitlisted);
        entrantListData.add(entrant);
        Entrant entrant2 = new Entrant("id",
                "Bob Builder",
                "she/her",
                "780 666 3333",
                "user@gmail.com",
                "DEVICEID",
                "Enter image here",
                "Edmonton AB",
                true,
                false);
        entrant2.setEntrantStatus(Entrant.EntrantStatus.invited);
        entrantListData.add(entrant2);
        Entrant entrant3 = new Entrant("id",
                "Harry Potter",
                "she/her",
                "780 666 3333",
                "user@gmail.com",
                "DEVICEID",
                "Enter image here",
                "Edmonton AB",
                true,
                false);
        entrant3.setEntrantStatus(Entrant.EntrantStatus.accepted);
        entrantListData.add(entrant3);
        Entrant entrant4 = new Entrant("id",
                "Canceled user",
                "she/her",
                "780 666 3333",
                "user@gmail.com",
                "DEVICEID",
                "Enter image here",
                "Edmonton AB",
                true,
                false);
        entrant4.setEntrantStatus(Entrant.EntrantStatus.cancelled);
        entrantListData.add(entrant4);


        // Initialize our ListView
        entrantListView = view.findViewById(R.id.o_entrant_listview);

        // Set up our array adapter and connect it to our listView
        entrantListAdapter = new oEntrantArrayAdapter(view.getContext(), entrantListData);
        entrantListView.setAdapter(entrantListAdapter);

        return view;
    }
}