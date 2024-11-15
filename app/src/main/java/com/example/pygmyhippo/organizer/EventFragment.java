package com.example.pygmyhippo.organizer;

/*
This Fragment will display one of the organiser's event they wish to view and give them the option to draw their lottery.
Purpose is to: Allow the organiser to update their event
                - Let the organiser see the entrants for that event
                - Let the organiser draw the lottery for that event
Contributors: Katharine, Kori
Issues: Doesn't have updatable fields yet
        - Need to separate database queries from code
 */

import com.example.pygmyhippo.common.Entrant;

import java.util.ArrayList;

import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.example.pygmyhippo.R;
import com.example.pygmyhippo.common.Account;
import com.example.pygmyhippo.common.Event;
import com.example.pygmyhippo.database.DBOnCompleteFlags;
import com.example.pygmyhippo.database.DBOnCompleteListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.squareup.picasso.Picasso;

import java.util.Random;

/**
 * This fragment will hold a single events
 * @author Katharine
 *          Kori added on to this for organiser
 * @version 2.0
 * No returns and no parameters
 */
public class EventFragment extends Fragment {

    private NavController navController;
    private Event event;
    private ArrayList<Entrant> entrants;
    private EventDB dbHandler;
    private String eventID;
    private Account signedInAccount;

    // populate single event page with hardcoded event information
    public Event hardcodeEvent() {
        entrants = new ArrayList<>();
        // Add hardcoded entrants
        entrants.add(new Entrant("5LCoIC4Ix46vPavSV1KX", Entrant.EntrantStatus.waitlisted));
        entrants.add(new Entrant("8Bd5McHSzrYcjH99yv8Y", Entrant.EntrantStatus.invited));
        entrants.add(new Entrant("SfSzvATHz9m9fj7vmWbp", Entrant.EntrantStatus.accepted));
        entrants.add(new Entrant("hupsArkwU6yvvSD1tKIe", Entrant.EntrantStatus.cancelled));

        return event = new Event(
                "Hippo Party",
                "IaMdwyQpHDh6GdZF025k",
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
        navController = Navigation.findNavController(view);

        // Set up navigation for the back button to return to last fragment
        FloatingActionButton backButton = view.findViewById(R.id.u_backButtonToQRView);
        backButton.setOnClickListener(view1 -> {
            Log.d("EventFragment", "Back button pressed");
            navController.popBackStack();
        });

        // Initialize the handler
        dbHandler = new EventDB();

        // Get all the textViews we want to populate
        TextView eventNameView = view.findViewById(R.id.u_eventNameView);
        TextView eventDateView = view.findViewById(R.id.u_eventDateView);
        TextView eventTimeView = view.findViewById(R.id.u_eventTimeView);
        TextView eventOrganizerView = view.findViewById(R.id.u_organizerNameView);
        TextView eventLocationView = view.findViewById(R.id.u_eventLocationView);
        TextView eventCostView = view.findViewById(R.id.u_eventCostView);
        TextView eventAboutDescriptionView = view.findViewById(R.id.u_aboutEventDescriptionView);
        ImageView eventPoster = view.findViewById(R.id.u_eventImageView);
        Button lotteryButton = view.findViewById(R.id.close_event_button);

        // Get the actual event data to populate this view
        dbHandler.getEvent(eventID, new DBOnCompleteListener<Event>() {
            @Override
            public void OnComplete(@NonNull ArrayList<Event> docs, int queryID, int flags) {
                if (flags == DBOnCompleteFlags.SINGLE_DOCUMENT.value) {
                    // Get the event to display for this fragment
                    event = docs.get(0);

                    // Set the text fields
                    eventNameView.setText(event.getEventTitle());
                    eventDateView.setText(event.getDate());
                    eventTimeView.setText(event.getTime());
                    eventOrganizerView.setText(event.getOrganiserID());
                    eventLocationView.setText(event.getLocation());
                    eventCostView.setText(event.getCost());
                    eventAboutDescriptionView.setText(event.getDescription());

                    // If the event is closed, then change the style of the draw button (and there are no available spots)
                    if (!event.hasAvailability()) {
                        if (event.getEventStatus().value.equals("cancelled")) {
                            lotteryButton.setBackgroundColor(0xFFA4A8C3);
                            lotteryButton.setText("Lottery closed");
                            lotteryButton.setTextColor(0xFF3A5983);
                            lotteryButton.setClickable(false);
                            lotteryButton.setTextSize(20);
                        }
                    } else if (event.hasAvailability()) {
                        if (event.getEventStatus().value.equals("cancelled")) {
                            // The lottery was drawn but there are available spots
                            // So layout the button for a redraw
                            lotteryButton.setText("Draw Replacements");
                        }
                    }

                    // Get the event poster from firebase
                    dbHandler.getImageDownloadUrl(event.getEventPoster(), new DBOnCompleteListener<Uri>() {
                        @Override
                        public void OnComplete(@NonNull ArrayList<Uri> docs, int queryID, int flags) {
                            // Author of this code segment is James
                            if (flags == DBOnCompleteFlags.SUCCESS.value) {
                                // Get the image and format it
                                Uri downloadUri = docs.get(0);
                                int imageSideLength = eventPoster.getWidth() / 2;
                                Picasso.get()
                                        .load(downloadUri)
                                        .resize(imageSideLength, imageSideLength)
                                        .centerCrop()
                                        .into(eventPoster);
                            } else {
                                // Event had no image, so it will stay as default image
                                Log.d("DB", String.format("No image found, setting default"));
                            }
                        }
                    });
                } else {
                    // Should only ever expect 1 document, otherwise there must be an error
                    handleDBError();

                    // TODO: Remove this in final product
                    // Or the sample event got deleted in the database. So default to the hardcoded event for testing
                    event = hardcodeEvent();
                    event.setEventWinnersCount(2);
                }
            }
        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.organiser_fragment_event, container, false);
        Button lotteryButton = view.findViewById(R.id.close_event_button);

        if (getArguments() != null) {
            signedInAccount = EventFragmentArgs.fromBundle(getArguments()).getSignedInAccount();
            eventID = EventFragmentArgs.fromBundle(getArguments()).getEventID();
        } else {
            eventID = "IaMdwyQpHDh6GdZF025k";
            signedInAccount = new Account();
        }

        // Add functionality to the lottery event button
        lotteryButton.setOnClickListener(new View.OnClickListener(){
        // Set up the listener for viewing entrants button
        Button viewEntrantsButton = view.findViewById(R.id.button_view_entrants);
        viewEntrantsButton.setOnClickListener(view1 -> {
            Bundle navArgs = new Bundle();

            // Pass the eventID and the current account to the next fragment
            navArgs.putString("eventID", eventID);
            navArgs.putParcelable("signedInAccount", signedInAccount);
            navController.navigate(R.id.view_entrants_fragment, navArgs);
        });

        // Add functionality to the closing event button
        closeEventButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                // Only do the draw if the event hasn't already been closed
                if (event.getEventStatus().value.equals("ongoing") && !event.getEntrants().isEmpty()) {
                    // Change the view of the button
                    lotteryButton.setBackgroundColor(0xFFA4A8C3);
                    lotteryButton.setText("Lottery closed");
                    lotteryButton.setClickable(false);
                    lotteryButton.setTextColor(0xFF3A5983);
                    lotteryButton.setTextSize(20);

                    // Update the status of the event to closed
                    event.setEventStatus(Event.EventStatus.cancelled);

                    // Draw the lottery winners and change their statuses
                    drawWinners(event);

                    // After the draw, update the event in the database
                    dbHandler.updateEvent(event, new DBOnCompleteListener<Event>() {
                        @Override
                        public void OnComplete(@NonNull ArrayList<Event> docs, int queryID, int flags) {
                            // Log when the data is updated or catch if there was an error
                            if (flags == DBOnCompleteFlags.SUCCESS.value) {
                                Log.d("DB", String.format("Successfully finished updating event with ID (%s).", event.getEventID()));
                            } else {
                                // If not the success flag, then there was an error
                                handleDBError();
                            }
                        }
                    });
                } else if (event.getEventStatus().value.equals("ongoing") && event.getEntrants().isEmpty()) {
                    // There are no entrants so the lottery should not be drawn
                    Toast.makeText(getContext(), "No entrants to run the lottery on!", Toast.LENGTH_SHORT).show();
                } else if (event.getEventStatus().value.equals("cancelled") && event.hasAvailability()) {
                    // There are available spots to do a redraw
                    // Change the view of the button
                    lotteryButton.setBackgroundColor(0xFFA4A8C3);
                    lotteryButton.setText("Lottery closed");
                    lotteryButton.setClickable(false);
                    lotteryButton.setTextColor(0xFF3A5983);
                    lotteryButton.setTextSize(20);

                    // Redraw the lottery to fill all the available spots
                    drawWinners(event);

                    // After the redraw, update the event in the database
                    dbHandler.updateEvent(event, new DBOnCompleteListener<Event>() {
                        @Override
                        public void OnComplete(@NonNull ArrayList<Event> docs, int queryID, int flags) {
                            // Log when the data is updated or catch if there was an error
                            if (flags == DBOnCompleteFlags.SUCCESS.value) {
                                Log.d("DB", String.format("Successfully finished updating event with ID (%s).", event.getEventID()));
                            } else {
                                // If not the success flag, then there was an error
                                handleDBError();
                            }
                        }
                    });
                }
            }
        });

        return view;
    }

    /**
     * This method will go through the event's entrants and randomly select a specific amount to be invited
     * It will change the statuses of the entrants in the event (accounts for full draw or redraw status)
     * @param event The event that's closing its lottery
     * @author Kori
     */
    public void drawWinners(Event event) {
        // Referenced Random class from https://www.geeksforgeeks.org/generating-random-numbers-in-java/
        // Accessed on Nov. 3, 2024

        Random rand = new Random();
        int winnerNumber = event.getCurrentWinners();

        // Loop for how many winners this event wants (using winner count to keep check on it)
        while (winnerNumber < event.getEventWinnersCount()) {
            // Only try to draw applicants if there are entrants in the waitlist
            if (event.getNumberWaitlisted() > 0) {
                // Draw a random index number between 0 and the size of the list
                int drawIndex = rand.nextInt(event.getEntrants().size());

                if (!event.getEntrants().get(drawIndex).getEntrantStatus().value.equals("waitlisted")) {
                    // This entrant does not have the status for a draw/redraw, so skip them
                    continue;
                }

                // If we get here, then the entrant hasn't already been invited
                event.getEntrants().get(drawIndex).setEntrantStatus(Entrant.EntrantStatus.invited);
                winnerNumber++;
            } else {
                // There are no entrants in the waitlist, so no need to draw anymore
                break;
            }

        }
    }

    /**
     * Method to display when a database error has occured
     */
    private void handleDBError() {
        Toast toast = Toast.makeText(getContext(), "DB Error!", Toast.LENGTH_SHORT);
        toast.show();
    }
}