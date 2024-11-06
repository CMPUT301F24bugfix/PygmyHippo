package com.example.pygmyhippo.user;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.example.pygmyhippo.R;
import com.example.pygmyhippo.common.Account;
import com.example.pygmyhippo.common.Entrant;
import com.example.pygmyhippo.common.Event;
import com.example.pygmyhippo.database.DBOnCompleteFlags;
import com.example.pygmyhippo.database.DBOnCompleteListener;
import com.example.pygmyhippo.databinding.UserFragmentEventBinding;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;

/**
 * This fragment will hold a single events
 * @author Katharine
 * @version 1.0
 * No returns and no parameters
 */
public class EventFragment extends Fragment implements DBOnCompleteListener<Event> {

    private UserFragmentEventBinding binding;
    private NavController navController;

    private Event event;

    // this is the current user who is trying to join the event
    private Entrant entrant;
    private ArrayList<Entrant> entrants;
    private Account signedInAccount;

    private EventDB handler;

    private TextView eventNameView, eventDateView, eventTimeView, eventOrganizerView,
            eventLocationView, eventCostView, eventAboutDescriptionView;
    private Button registerButton, deleteEventButton, deleteQRCodeButton;
    private ConstraintLayout adminConstraint;

    // TODO: pass entrant and even information using bundle...

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
                // TODO: there is a bit of an issue with aligning the time when it is shorter on the xml
                "4:00 PM MST - 4:00 AM MST",
                "Love hippos and a party? Love a party! Join a party!",
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
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = UserFragmentEventBinding.inflate(inflater, container, false);
        View view = binding.getRoot();

        // TextViews
        eventNameView = binding.uEventNameView;
        eventDateView = binding.uEventDateView;
        eventTimeView = binding.uEventTimeView;
        eventOrganizerView = binding.uOrganizerNameView;
        eventLocationView = binding.uEventLocationView;
        eventCostView = binding.uEventCostView;
        eventAboutDescriptionView = binding.uAboutEventDescriptionView;

        // Buttons
        registerButton = binding.uRegisterButton;
        deleteEventButton = binding.aDeleteEventButton;
        deleteQRCodeButton = binding.aDeleteQRCodeButton;

        // Container Layouts
        adminConstraint = binding.aActionsConstraint;

        handler = new EventDB();

        signedInAccount = EventFragmentArgs.fromBundle(getArguments()).getSignedInAccount();
        setPermissions();

        String navigationEventID = EventFragmentArgs.fromBundle(getArguments()).getEventID();
        getEvent(navigationEventID);

        // TODO: add actual database stuff here, where user is added into the events list
        // for now, this is hardcoding to figure out structure of entrant
        entrant = new Entrant(
                "123",
                Entrant.EntrantStatus.invited
        );

        FloatingActionButton backButton = view.findViewById(R.id.u_backButtonToQRView);
        backButton.setOnClickListener(view1 -> {
            Navigation.findNavController(view1).navigate(R.id.action_eventFragment_to_scanQRcodeFragment);
        });

        registerButton.setOnClickListener(buttonView -> {
            registerUser();
        });

        deleteEventButton.setOnClickListener(buttonView -> {
            handler.deleteEventByID(event.getEventID(), this);
        });

        deleteQRCodeButton.setOnClickListener(buttonView -> {
            Log.d("EventFragment", "Delete QR Code Button pressed");
            // TODO: Delete QR Code.
        });

        return view;
    }

    @Override
    public void OnComplete(@NonNull ArrayList<Event> docs, int queryID, int flags) {
        if (queryID == 0) {
            if (flags == DBOnCompleteFlags.SINGLE_DOCUMENT.value) {
                event = docs.get(0);
                populateTextFields();
            }
        } else if (queryID == 1) {
            if (flags == DBOnCompleteFlags.SUCCESS.value) {
                Log.d("EventFragment", "Successfully deleted event. Navigating back to all events");
                Bundle navArgs = new Bundle();
                navArgs.putParcelable("signedInAccount", signedInAccount);
                navController.navigate(R.id.admin_navigation_all_events, navArgs);
            } else {
                Log.d("EventFragment", "Error in deleting event.");
            }
        }
    }

    /**
     * Tries to query for event by eventID.
     *
     * If there is no event ID then mock data is loaded in.
     * @param eventID ID of the event to query for.
     */
    private void getEvent(@Nullable String eventID) {
        if (eventID == null) {
            Log.d("EventFragment", "No Event ID was passed via navigation to EventFragment, using mock data...");
            event = hardcodeEvent();
            populateTextFields();
        } else {
            Log.d("EventFragment", String.format("Non-null eventID, attempting to retrieve Event with ID %s", eventID));
            handler.getEventByID(eventID, this);
        }
    }

    /**
     * Updates text views in the fragment to reflect the same info in event.
     */
    private void populateTextFields() {
        eventNameView.setText(event.getEventTitle());
        eventDateView.setText(event.getDate());
        eventTimeView.setText(event.getTime());
        eventOrganizerView.setText(event.getOrganiserID());
        eventLocationView.setText(event.getLocation());
        eventCostView.setText(event.getCost());
        eventAboutDescriptionView.setText(event.getDescription());
    }

    /**
     * Register user as an entrant for the current event.
     *
     * Button is only visible when the signedInAccount is a user.
     */
    private void registerUser() {
        // if the even already has the user, remove the user upon clicking
        if (event.hasEntrant(entrant)) {
            registerButton.setBackgroundColor(0xFF35B35D);
            event.removeEntrant(entrant);
            registerButton.setText("Register");
        } else {
            // otherwise, check for enabled geolocation and add entrant accordingly
            if (event.getGeolocation()) {

                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setTitle("WARNING!");
                builder.setMessage("This event requires geolocation. Continue registering?");
                builder.setCancelable(true);
                builder.setPositiveButton("Yes", (DialogInterface.OnClickListener) (dialog, which) -> {
                    registerButton.setBackgroundColor(0xFF808080);
                    event.addEntrant(entrant);
                    registerButton.setText("✔");
                });
                builder.setNegativeButton("No", (DialogInterface.OnClickListener) (dialog, which) -> {
                    dialog.cancel();
                });
                AlertDialog geolocationWarning = builder.create();
                geolocationWarning.show();

            } else {
                registerButton.setBackgroundColor(0xFF808080);
                event.addEntrant(entrant);
                registerButton.setText("✔");
            }
        }
    }

    /**
     * Toggles visibility of certain buttons depending on role of signedInAccount.
     */
    private void setPermissions() {
        if (signedInAccount.getCurrentRole() == Account.AccountRole.admin) {
            Log.d("EventFragment", "Admin user detected. Setting Admin permissions");
            adminConstraint.setVisibility(View.VISIBLE);
            registerButton.setVisibility(View.GONE);
        } else {
            Log.d("EventFragment", "Non-Admin user detected. Setting non-Admin permissions");
            adminConstraint.setVisibility(View.GONE);
            registerButton.setVisibility(View.VISIBLE);
        }
    }
}