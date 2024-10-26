package com.example.pygmyhippo.user;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.example.pygmyhippo.R;
import com.example.pygmyhippo.common.Entrant;
import com.example.pygmyhippo.common.Event;

import java.util.ArrayList;

/**
 * This fragment will hold a single events
 * @author Katharine
 * @version 1.0
 * No returns and no parameters
 */
public class EventFragment extends Fragment {

    private Event event;
    // this is the current user who is trying to join the event
    private Entrant entrant;
    private ArrayList<Entrant> entrants;

    public EventFragment() {
    }

    // TODO: to be used for setting text fields in layout
    public EventFragment(Event event) {
        this.event = event;
    }

    // need user information somehow when trying to join this event, user information to be added as an entrant onto the event info list
    public EventFragment(Event event, Entrant entrant) {
        this.event = event;
        this.entrant = entrant;
    }

    // populate single event page with event information
    // Method to set up a hardcoded example event
    public Event hardcodeEvent() {
        entrants = new ArrayList<>();
        // Add any entrants if needed, e.g., entrants.add(new Entrant("Student1", "Guest"));

        // Proper instantiation of the Event object using the constructor
        return event = new Event(
                "Hippo Party",
                "1",
                "The Hippopotamus Society",
                entrants,
                "The Swamp",
                "2024-10-31",
                // TODO: there is a bit of an issue with aligning the time when it is shorter on the xml
                "4:00 PM MST - 4:00 AM MST",
                "Love hippos and a party? Love a party! Join a party!",
                "$150.00",
                "hippoparty.png",
                Event.EventStatus.ongoing
        );
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    // populates the view with information
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        View eventBackButtonView = view.findViewById(R.id.u_backButtonToQRView);
        TextView eventNameView = view.findViewById(R.id.u_eventNameView);
        TextView eventDateView = view.findViewById(R.id.u_eventDateView);
        TextView eventTimeView = view.findViewById(R.id.u_eventTimeView);
        TextView eventOrganizerView = view.findViewById(R.id.u_organizerNameView);
        TextView eventLocationView = view.findViewById(R.id.u_eventLocationView);
        TextView eventCostView = view.findViewById(R.id.u_eventCostView);
        TextView eventAboutTitleView = view.findViewById(R.id.u_aboutEventTitleView);
        TextView eventAboutDescriptionView = view.findViewById(R.id.u_aboutEventDescriptionView);
        View eventRegisterButtonView = view.findViewById(R.id.u_registerButtonView);

        // set with hardcoded values, set the view in the same wy form the event recieved from the constructor
        event = hardcodeEvent();

        eventNameView.setText(event.getEventTitle());
        eventDateView.setText(event.getDate());
        eventTimeView.setText(event.getTime());
        eventOrganizerView.setText(event.getOrganiserID());
        eventLocationView.setText(event.getLocation());
        eventCostView.setText(event.getCost());
        eventAboutDescriptionView.setText(event.getDescription());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.user_fragment_event, container, false);

        Button registerButton = view.findViewById(R.id.u_registerButtonView);

        // with a check, check to see if event has been registered for, if so, register (set text
        // to checkmark, add to lists and stuff, if event has not been register for, set text back
        // to register and remove from lists and stuff
        registerButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                // TODO: add actual database stuff here, where user is added into the events list
                // for now, this is hardcoding to figure out structure of entrant
                // this part, the entrant information would be passed to the event but hardcode
                entrant = new Entrant(
                        "123",
                        Entrant.EntrantStatus.invited
                );
                event.addEntrant(entrant);

                // set text of register button to checkmark
                registerButton.setText("✔");
            }
        });
        return view;
    }
}