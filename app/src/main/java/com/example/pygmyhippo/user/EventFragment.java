package com.example.pygmyhippo.user;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.example.pygmyhippo.R;
import com.example.pygmyhippo.common.Event;

/**
 * This fragment will hold a single events
 * @author Katharine
 * @version 1.0
 * No returns and no parameters
 */
public class EventFragment extends Fragment {

    // populate single event page with event information
    private Event event;

    public EventFragment() {
    }

    // this might be useful
    public EventFragment(Event event) {
        this.event = event;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.user_fragment_event, container, false);

        Button registerButton = view.findViewById(R.id.registerButton);

        // with a check, check to see if event has been registered for, if so, register (set text
        // to checkmark, add to lists and stuff, if event has not been register for, set text back
        // to register and remove from lists and stuff
        registerButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                // set text of register button to checkmark and disable the button
                // TODO: add actual database stuff here, where user is added into the events list
                registerButton.setText("✔");
                registerButton.setEnabled(false);
            }
        });
        return view;
    }
}