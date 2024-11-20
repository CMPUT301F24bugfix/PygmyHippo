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
import com.example.pygmyhippo.databinding.UserFragmentViewMyeventBinding;
import com.example.pygmyhippo.organizer.EventFragmentArgs;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class ViewMyEventFragment extends Fragment implements DBOnCompleteListener<Event>{
    private UserFragmentViewMyeventBinding binding;
    private NavController navController;

    private Event event;

    // this is the current user who is trying to join the event
    private Entrant entrant;
    private ArrayList<Entrant> entrants;
    private String eventID;
    private Account signedInAccount;

    private EventDB dbHandler;
    private ImageStorage imageHandler;

    private TextView eventNameView, eventDateView, eventTimeView, eventOrganizerView,
            eventLocationView, eventCostView, eventAboutDescriptionView;
    private ConstraintLayout adminConstraint;
    private ImageView eventPoster;

    private TextView userWaitlistStatus, userStatusDescription;
    private Button leaveWaitlistButton;

    // ACCEPTED WAITLIST
    private Button acceptWaitlistButton, declineWaitlistButton;

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
        binding = UserFragmentViewMyeventBinding.inflate(inflater, container, false);
        View view = binding.getRoot();

        if (getArguments() != null) {
            signedInAccount = EventFragmentArgs.fromBundle(getArguments()).getSignedInAccount();
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
        userWaitlistStatus = view.findViewById(R.id.u_userStatus);
        userStatusDescription = view.findViewById(R.id.u_userStatusDescription);

        // BUTTONS
        leaveWaitlistButton = view.findViewById(R.id.u_leaveWaitlistButton);

        // ACCEPTED FROM WAITLIST BUTTONS
        acceptWaitlistButton = view.findViewById(R.id.u_acceptWaitlistButton);
        declineWaitlistButton = view.findViewById(R.id.u_declineWaitlistButton);

        // TODO: should set buttons to invisible

        // TODO: fix back button
        // Set up navigation for the back button to return to last fragment
        FloatingActionButton backButton = view.findViewById(R.id.u_backButtonToMyEvents);
        backButton.setOnClickListener(view1 -> {
            Log.d("ViewMyEventFragment", "Back button pressed");
            navController.popBackStack();
        });

        // TODO: do stuff here for buttons... and stuff


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

        if (signedInAccount != null && event.getEntrants() != null) {
            entrant = event.getEntrants()
                    .stream()
                    .filter(e -> e.getAccountID().equals(signedInAccount.getAccountID()))
                    .findFirst()
                    .orElse(null);
        }

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
        // get event by id from database
        if (queryID == 1) {
            if (flags == DBOnCompleteFlags.SINGLE_DOCUMENT.value) {
                // Get the event from the database and populate the fragment
                event = docs.get(0);
                populateAllFields();

                // based on user status in waitlist
                // TODO: figure out if this is the event clicked
                if (event.getEventStatus().value.equals("ongoing")) {
                    pendingWaitlist();
                } else if (event.getEntrants().stream().anyMatch(e -> e.getAccountID().equals(entrant.getAccountID()) && e.getEntrantStatus() == Entrant.EntrantStatus.invited)) {
                    wonWaitlistSelection();
                }

            }
        // update event database
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

    public void pendingWaitlist() {
        userWaitlistStatus.setText("PENDING");
        userStatusDescription.setVisibility(View.VISIBLE);
        userStatusDescription.setText("The waitlist is still open/your status for this event is pending. Do you want to leave this event?");
        leaveWaitlistButton.setVisibility(View.VISIBLE);

        leaveWaitlistButton.setOnClickListener(view -> {
            event.removeEntrant(entrant);
            dbHandler.updateEvent(event, this);
            leaveWaitlistButton.setVisibility(View.GONE);
            userWaitlistStatus.setText("UNAVAILABLE");
            userStatusDescription.setText("You have left this event. Sign up again using a QR code.");
        });
    }


    public void wonWaitlistSelection() {
        // populate the views, make sure to make buttons visible,
        // when button is clicked, either set user status to accepted or just remove the user entirely from the waitlist
        userWaitlistStatus.setText("WON");
        userStatusDescription.setVisibility(View.VISIBLE);
        userStatusDescription.setText("You have been accepted from the waitlist to attend this event. Do you accept or decline this invitation?");
        acceptWaitlistButton.setVisibility(View.VISIBLE);
        declineWaitlistButton.setVisibility(View.VISIBLE);

        // https://hellokoding.com/query-an-arraylist-in-java/
        acceptWaitlistButton.setOnClickListener(view -> {

            event.getEntrants()
                    .stream()
                    .filter(e -> (e.getAccountID().equals(entrant.getAccountID())))
                    .findFirst()
                    .ifPresent(e -> e.setEntrantStatus(Entrant.EntrantStatus.accepted));
            dbHandler.updateEvent(event, this);

            acceptWaitlistButton.setVisibility(View.GONE);
            declineWaitlistButton.setVisibility(View.GONE);
            userStatusDescription.setText("You are officially accepted into this event!");
        });

        // TODO: add decline waitlist button funtionality here
        // use removeEntrant from list and update DB


    }
}
