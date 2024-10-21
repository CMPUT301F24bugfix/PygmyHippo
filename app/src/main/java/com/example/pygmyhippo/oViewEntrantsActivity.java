package com.example.pygmyhippo;

/*
This Activity is meant for the Organizer to view the users who entered the waitlist on an event the Organizer made.
Will contain the functionality to filter the given list depending on the status of the entrant
Deals with user stories 02.06.01, 02.06.02, 02.06.03, and 02.02.01
Author: Kori Kozicki

Current Issues: Navigation to this activity and source of user list
 */

import android.content.Context;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;

public class oViewEntrantsActivity extends AppCompatActivity {
    private ArrayList<Account> entrantListData;
    private ArrayAdapter<Account> entrantListAdapter;
    private Spinner status_spinner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.o_view_entrants);

        status_spinner = findViewById(R.id.o_entrant_list_spinner);

        ArrayAdapter<CharSequence> o_spinner_adapter = ArrayAdapter.createFromResource(
                this,
                R.array.entrant_status,
                R.layout.o_spinner
        );

        o_spinner_adapter.setDropDownViewResource(R.layout.o_spinner);
        status_spinner.setAdapter(o_spinner_adapter);

    }
}
