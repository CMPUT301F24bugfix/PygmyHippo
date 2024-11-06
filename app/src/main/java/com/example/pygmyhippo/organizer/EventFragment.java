package com.example.pygmyhippo.organizer;

/*
This Fragment will display one of the organiser's event they wish to view and give them the option to draw their lottery.
Purpose is to: Allow the organiser to update their event
                - Let the organiser see the entrants for that event
                - Let the organiser draw the lottery for that event
Contributors: Katharine, Kori
Issues: Doesn't have updatable fields yet
        - Isn't connected to database yet
 */

import static android.content.ContentValues.TAG;

import com.example.pygmyhippo.common.Entrant;

import java.util.ArrayList;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.navigation.Navigation;

import com.example.pygmyhippo.common.Entrant;
import com.example.pygmyhippo.common.Event;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.navigation.Navigation;

import com.example.pygmyhippo.common.Entrant;
import com.example.pygmyhippo.common.Event;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.pygmyhippo.R;
import java.util.Random;

/**
 * This fragment will hold a single events
 * @author Katharine
 *          Kori added on to this for organiser
 * @version 1.0
 * No returns and no parameters
 */
public class EventFragment extends Fragment {

    private Event event;
    private ArrayList<Entrant> entrants;

    // TODO: pass entrant and even information using bundle...

    // populate single event page with hardcoded event information
    public Event hardcodeEvent() {
        entrants = new ArrayList<>();

        return event = new Event(
                "Hippo Party",
                "event1",
                "The Hippopotamus Society",
                entrants,
                "The Swamp",
                "2024-10-31",
                // TODO: there is a bit of an issue with aligning the time when it is shorter on the xml
                "4:00 PM MST - 4:00 AM MST",
                "Love hippos and a party? Love a party! Join a party! We have lots of really cool hippos I'm sure you'd love to meet! There will be food, games, and all sorts of activities you could imagine! It's almost worth the price to see Moo Deng and his buddies!",
                "$150.00",
                "hippoparty.png",
                Event.EventStatus.ongoing,
                true
        );
    }

    // populates the view with information
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        FloatingActionButton backButton = view.findViewById(R.id.u_backButtonToQRView);
        backButton.setOnClickListener(view1 -> {
            Navigation.findNavController(view1).navigate(R.id.action_event_fragment_to_organiser_myEvents_page);
        });

        // Set up the listener for viewing entrants button
        Button viewEntrantsButton = view.findViewById(R.id.button_view_entrants);
        viewEntrantsButton.setOnClickListener(view1 -> {
            Navigation.findNavController(view1).navigate(R.id.action_event_fragment_to_view_entrants_fragment);
        });

        TextView eventNameView = view.findViewById(R.id.u_eventNameView);
        TextView eventDateView = view.findViewById(R.id.u_eventDateView);
        TextView eventTimeView = view.findViewById(R.id.u_eventTimeView);
        TextView eventOrganizerView = view.findViewById(R.id.u_organizerNameView);
        TextView eventLocationView = view.findViewById(R.id.u_eventLocationView);
        TextView eventCostView = view.findViewById(R.id.u_eventCostView);
        TextView eventAboutDescriptionView = view.findViewById(R.id.u_aboutEventDescriptionView);
        Button closeEventButton = view.findViewById(R.id.close_event_button);

        // set with hardcoded values, set the view in the same wy form the event received from the constructor
        // TODO: database event info to be done here
        event = hardcodeEvent();
        event.setEventWinnersCount(5);

        eventNameView.setText(event.getEventTitle());
        eventDateView.setText(event.getDate());
        eventTimeView.setText(event.getTime());
        eventOrganizerView.setText(event.getOrganiserID());
        eventLocationView.setText(event.getLocation());
        eventCostView.setText(event.getCost());
        eventAboutDescriptionView.setText(event.getDescription());

        // If the event is closed, then change the style of the draw button
        if (event.getEventStatus().value.equals("cancelled")) {
            closeEventButton.setBackgroundColor(0xFFA4A8C3);
            closeEventButton.setText("Lottery closed");
            closeEventButton.setTextColor(0xFF3A5983);
            closeEventButton.setTextSize(20);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.organiser_fragment_event, container, false);
        Button closeEventButton = view.findViewById(R.id.close_event_button);

        closeEventButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                // Only do the draw if the event hasn't already been closed
                if (event.getEventStatus().value.equals("ongoing") && !event.getEntrants().isEmpty()) {
                    // Change the view of the button
                    closeEventButton.setBackgroundColor(0xFFA4A8C3);
                    closeEventButton.setText("Lottery closed");
                    closeEventButton.setClickable(false);
                    closeEventButton.setTextColor(0xFF3A5983);
                    closeEventButton.setTextSize(20);

                    // Update the status of the event to closed
                    event.setEventStatus(Event.EventStatus.cancelled);

                    // Draw the lottery winners and change their statuses
                    drawWinners(event);
                } else if (event.getEventStatus().value.equals("ongoing") && event.getEntrants().isEmpty()) {
                    // There are no entrants so the lottery should not be drawn
                    Toast.makeText(getContext(), "No entrants to run the lottery on!", Toast.LENGTH_SHORT).show();
                }
            }
        });

        return view;
    }

    /**
     * This method will go through the event's entrants and randomly select a specific amount to be invited
     * @param event
     * @author Kori
     * TODO: Update the event in the database when entrant status gets changed
     */
    public void drawWinners(Event event) {
        // Referenced Random class from https://www.geeksforgeeks.org/generating-random-numbers-in-java/
        // Accessed on Nov. 3, 2024

        Random rand = new Random();
        int winnerNumber = 0;
        ArrayList<Entrant> entrants = event.getEntrants();

        // If the entrant list is smaller than the winner size, then just set them all as winners
        if (entrants.size() <= event.getEventWinnersCount()) {
            // Use the winner number as an index
            while (winnerNumber < entrants.size()) {
                // Update all the entrants statuses
                //TODO: Notifications would probably be sent from here
                entrants.get(winnerNumber).setEntrantStatus(Entrant.EntrantStatus.invited);
                winnerNumber++;
            }
        } else {
            // Loop for how many winners this event wants (using winner count to keep check on it)
            while (winnerNumber < event.getEventWinnersCount()) {
                // Draw a random index number between 0 and the size of the list
                int drawIndex = rand.nextInt(entrants.size());

                if (entrants.get(drawIndex).getEntrantStatus().value.equals("invited")) {
                    // This entrant was already invited by the rng, so restart iteration
                    continue;
                }

                // If we get here, then the entrant hasn't already been invited
                entrants.get(drawIndex).setEntrantStatus(Entrant.EntrantStatus.invited);
                winnerNumber++;
            }
        }
    }
}