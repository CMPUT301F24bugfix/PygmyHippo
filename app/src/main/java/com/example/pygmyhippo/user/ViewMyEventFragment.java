package com.example.pygmyhippo.user;

/*
This Fragment will display a chosen event from a user's event list
Will be used by users and admins
Purposes:
        - If the user is chosen from the waitlist, let the user accept the invitation to register/sign up when chosen to participate in an event
        - If the user is chosen from the waitlist, let the user decline an invitation when chosen to participate in an event
        - If the user is not chosen from the waitlist, let the user have another chance to be chosen for the waitlist
Contributors: Katharine, Kori
 */

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
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.fragment.app.Fragment;

import com.example.pygmyhippo.R;
import com.example.pygmyhippo.common.Account;
import com.example.pygmyhippo.common.Entrant;
import com.example.pygmyhippo.common.Event;
import com.example.pygmyhippo.database.DBOnCompleteFlags;
import com.example.pygmyhippo.database.DBOnCompleteListener;
import com.example.pygmyhippo.database.EventDB;
import com.example.pygmyhippo.database.ImageStorage;
import com.example.pygmyhippo.database.StorageOnCompleteListener;
import com.example.pygmyhippo.databinding.UserFragmentEventBinding;
import com.example.pygmyhippo.organizer.EventFragmentArgs;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class ViewMyEventFragment extends Fragment implements DBOnCompleteListener<Event>{
    private UserFragmentEventBinding binding;
    private NavController navController;

    private Event event;

    // this is the current user who is trying to join the event
    private Entrant entrant;
    private ArrayList<Entrant> entrants;
    private Account signedInAccount;
    private String eventID;

    private EventDB dbHandler;
    private ImageStorage imageHandler;

    private TextView eventNameView, eventDateView, eventTimeView, eventOrganizerView,
            eventLocationView, eventCostView, eventAboutDescriptionView;
    private Button leaveWaitlistButton;
    private ConstraintLayout adminConstraint;
    private ImageView eventPoster;


    // populate single event page with hardcoded event information
    public Event hardcodeEvent() {
        entrants = new ArrayList<>();

        return event = new Event(
                "Hippo Party",
                "1",
                "The Hippopotamus Society",
                entrants,
                "The Swamp",
                "2024-10-31",
                "4:00 PM MST - 4:00 AM MST",
                "Love hippos and a party? Love a party! Join a party!",
                "$150.00",
                "hippoparty.png",
                Event.EventStatus.ongoing,
                true
        );
    }

    // TODO: need to do something with getEvent (ie in user, by eventID passed...)
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        navController = Navigation.findNavController(view);

        // Initialize the handlers
        dbHandler = new EventDB();
        imageHandler = new ImageStorage();

        // Get the actual event data to populate this view
        dbHandler.getEventByID(eventID, this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = UserFragmentEventBinding.inflate(inflater, container, false);
        View view = binding.getRoot();

        if (getArguments() != null) {
            signedInAccount = com.example.pygmyhippo.organizer.EventFragmentArgs.fromBundle(getArguments()).getSignedInAccount();
            eventID = EventFragmentArgs.fromBundle(getArguments()).getEventID();
        } else {
            eventID = "IaMdwyQpHDh6GdZF025k";
            signedInAccount = new Account();
        }

        // VIEWS
        eventNameView = view.findViewById(R.id.u_myEventNameView);
        eventDateView = view.findViewById(R.id.u_myEventDateView);
        eventTimeView = view.findViewById(R.id.u_myEventTimeView);
        eventOrganizerView = view.findViewById(R.id.u_myOrganizerNameView);
        eventLocationView = view.findViewById(R.id.u_myEventLocationView);
        eventCostView = view.findViewById(R.id.u_myEventCostView);
        eventAboutDescriptionView = view.findViewById(R.id.u_aboutMyEventDescriptionView);
        eventPoster = view.findViewById(R.id.u_myEventImageView);

        // BUTTONS
        leaveWaitlistButton = view.findViewById(R.id.u_leaveWaitlistButton);

        // Set up navigation for the back button to return to last fragment
        FloatingActionButton backButton = view.findViewById(R.id.u_backButtonToMyEvents);
        backButton.setOnClickListener(view1 -> {
            Log.d("ViewMyEventFragment", "Back button pressed");
            navController.popBackStack();
        });

        // TODO: do stuff here for buttons... and stuff
        String clickedEventID = MyEventsFragmentArgs.fromBundle(getArguments()).getEventID();
        getEvent(clickedEventID);

        return view;
    }

    /**
     * Tries to query for event by eventID.
     * eventID should never be null...
     *
     * @param eventID ID of the event to query for.
     */
    private void getEvent(@Nullable String eventID) {
        Log.d("EventFragment", String.format("Non-null eventID, attempting to retrieve Event with ID %s", eventID));
        dbHandler.getEventByID(eventID, this);
    }

    /**
     * Updates text views and buttons in the fragment to reflect the same info in event.
     */
    // TODO: this is probably where to do the button and text changing for population
    // check the entrant status...
    private void populateAllFields() {
        eventNameView.setText(event.getEventTitle());
        eventDateView.setText(event.getDate());
        eventTimeView.setText(event.getTime());
        eventOrganizerView.setText(event.getOrganiserID());
        eventLocationView.setText(event.getLocation());
        eventCostView.setText(event.getCost());
        eventAboutDescriptionView.setText(event.getDescription());

        // Get the event poster from firebase
        imageHandler.getImageDownloadUrl(event.getEventPoster(), new StorageOnCompleteListener<Uri>() {
            @Override
            public void OnCompleteStorage(@NonNull ArrayList<Uri> docs, int queryID, int flags) {
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
    }

    @Override
    public void OnCompleteDB(@NonNull ArrayList<Event> docs, int queryID, int flags) {
        if (queryID == 0) {
            if (flags == DBOnCompleteFlags.SINGLE_DOCUMENT.value) {
                // Get the event from the database and populate the fragment
                event = docs.get(0);
                populateAllFields();
            }
        } else if (queryID == 2) {
            // Log when the data is updated or catch if there was an error
            if (flags == DBOnCompleteFlags.SUCCESS.value) {
                Log.d("DB", "Successfully finished updating event with ID");
            } else {
                // If not the success flag, then there was an error
                Log.d("EventFragment", "Error in updating event.");
            }
        }
    }

    public void leavePendingWaitlist() {

    }

    public void wonWaitlistSelection() {

    }
}
