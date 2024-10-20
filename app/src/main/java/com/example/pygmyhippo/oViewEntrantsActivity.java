package com.example.pygmyhippo;

/*
This Activity is meant for the Organizer to view the users who entered the waitlist on an event the Organizer made.
Will contain the functionality to filter the given list depending on the status of the entrant
Deals with user stories 02.06.01, 02.06.02, 02.06.03, and 02.02.01
Author: Kori Kozicki

Current Issues: Navigation to this activity and source of user list
 */

import android.os.Bundle;
import android.widget.ArrayAdapter;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;

public class oViewEntrantsActivity extends AppCompatActivity {
    private ArrayList<Account> entrantListData;
    private ArrayAdapter<Account> entrantListAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.o_view_entrants);

    }
}
