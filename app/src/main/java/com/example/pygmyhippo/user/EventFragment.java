package com.example.pygmyhippo.user;

/*
This Fragment will display one of the events that a User can see after scanning its QR code
Will be used by users and admins
Purposes:
        - Let the User view the details of the event
        - Let the User join the event if they wish
        - If they navigate back to this event, allow them the option to leave the event
        - Let Admin delete the event
Contributors: Katharine
Issues:
        - Needs testing
        - Needs to stop user from joining or leaving waitlist if the event is closed
 */

import android.app.AlertDialog;
import android.content.DialogInterface;
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
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.example.pygmyhippo.R;
import com.example.pygmyhippo.common.Account;
import com.example.pygmyhippo.common.AppNavController;
import com.example.pygmyhippo.common.Entrant;
import com.example.pygmyhippo.common.Event;
import com.example.pygmyhippo.database.DBOnCompleteFlags;
import com.example.pygmyhippo.database.DBOnCompleteListener;
import com.example.pygmyhippo.database.EventDB;
import com.example.pygmyhippo.database.ImageStorage;
import com.example.pygmyhippo.database.StorageOnCompleteListener;
import com.example.pygmyhippo.databinding.UserFragmentEventBinding;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

/**
 * This fragment will hold a single events
 * @author Katharine
 * @version 1.0
 * No returns and no parameters
 */
public class EventFragment extends Fragment implements DBOnCompleteListener<Event> {

    private UserFragmentEventBinding binding;
    private AppNavController navController;

    private Event event;

    // this is the current user who is trying to join the event
    private Entrant entrant;
    private ArrayList<Entrant> entrants;
    private Account signedInAccount;
    private boolean useNavigation, useFirebase, isAdmin;
    private String eventID;

    private EventDB DBhandler;
    private ImageStorage imageHandler;

    private TextView eventNameView, eventDateView, eventTimeView, eventOrganizerView,
            eventLocationView, eventCostView, eventAboutDescriptionView;
    private Button registerButton, deleteEventButton, deleteQRCodeButton;
    private ConstraintLayout adminConstraint;
    private ImageView eventImageView;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            EventFragmentArgs args = EventFragmentArgs.fromBundle(getArguments());
            signedInAccount = args.getSignedInAccount();
            useNavigation = args.getUseNavigation();
            useFirebase = args.getUseFirebase();
            eventID = args.getEventID();
            isAdmin = args.getIsAdmin();
        }
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        navController = new AppNavController(useFirebase, Navigation.findNavController(view));
        DBhandler = new EventDB(useFirebase);
        imageHandler = new ImageStorage(useFirebase);
        getEvent(eventID);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
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

        // ImageView
        eventImageView = binding.uEventImageView;

        // Buttons
        registerButton = binding.uRegisterButton;
        deleteEventButton = binding.aDeleteEventButton;
        deleteQRCodeButton = binding.aDeleteQRCodeButton;

        // Container Layouts
        adminConstraint = binding.aActionsConstraint;


        // Get current user account

        setPermissions();


        // Make the entrant equivalent using the account info (for if they join the waitlist)
        entrant = new Entrant(
                signedInAccount.getAccountID(),
                Entrant.EntrantStatus.waitlisted
        );

        // Set up navigation back to last fragment
        FloatingActionButton backButton = view.findViewById(R.id.u_backButtonToQRView);
        backButton.setOnClickListener(view1 -> {
            navController.popBackStack();
        });

        registerButton.setOnClickListener(buttonView -> {
            registerUser();
        });

        // This button is for admin to delete the event
        deleteEventButton.setOnClickListener(buttonView -> {
            DBhandler.deleteEventByID(event.getEventID(), this);
        });

        // For admin to delete the event's QR code
        deleteQRCodeButton.setOnClickListener(buttonView -> {
            Log.d("EventFragment", "Delete QR Code Button pressed");
            // TODO: Delete QR Code.
        });

        return view;
    }

    @Override
    public void OnCompleteDB(@NonNull ArrayList<Event> docs, int queryID, int flags) {
        if (queryID == 1) {
            if (flags == DBOnCompleteFlags.SINGLE_DOCUMENT.value) {
                // Get the event from the database and populate the fragment
                event = docs.get(0);
                populateTextFields();
            }
            else {
                // TODO: popback here
                // TODO: toast for bad dabatas
                Toast badEvent = Toast.makeText(getActivity(), "No such event exists", Toast.LENGTH_SHORT);
                badEvent.show();
                // should lead back to the qr code scanner
                navController.popBackStack();
            }
        } else if (queryID == 4) {
            if (flags == DBOnCompleteFlags.SUCCESS.value) {
                Log.d("EventFragment", "Successfully deleted event. Navigating back to all events");
                Bundle navArgs = new Bundle();
                navArgs.putParcelable("signedInAccount", signedInAccount);
                navController.navigate(R.id.admin_navigation_all_events, navArgs);
            } else {
                Log.d("EventFragment", "Error in deleting event.");
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

    /**
     * Tries to query for event by eventID.
     *
     * If there is no event ID then mock data is loaded in.
     * @param eventID ID of the event to query for.
     */
    private void getEvent(@Nullable String eventID) {
        if (eventID == null) {
            Log.d("EventFragment", "No Event ID was passed via navigation to EventFragment, go back...");
            navController.popBackStack();
        } else {
            Log.d("EventFragment", String.format("Non-null eventID, attempting to retrieve Event with ID %s", eventID));
            DBhandler.getEventByID(eventID, this);
        }
    }

    /**
     * Updates text views and buttons in the fragment to reflect the same info in event.
     */
    private void populateTextFields() {
        eventNameView.setText(event.getEventTitle());
        eventDateView.setText(event.getDate());
        eventTimeView.setText(event.getTime());
        eventOrganizerView.setText(event.getOrganiserID());
        eventLocationView.setText(event.getLocation());
        eventCostView.setText(event.getCost());
        eventAboutDescriptionView.setText(event.getDescription());

        // If the event already has the entrant, then we want to change the looks of the button
        if (event.hasEntrant(entrant)) {
            registerButton.setBackgroundColor(0xFF808080);
            registerButton.setText("✔");
        }

        // Get the poster for the event
        imageHandler.getImageDownloadUrl(event.getEventPoster(), new StorageOnCompleteListener<Uri>() {
            @Override
            public void OnCompleteStorage(@NonNull ArrayList<Uri> docs, int queryID, int flags) {
                // Author of this code segment is James
                if (flags == DBOnCompleteFlags.SUCCESS.value) {
                    // Get the image and format it
                    Uri downloadUri = docs.get(0);
                    int imageSideLength = eventImageView.getWidth() / 2;
                    Picasso.get()
                            .load(downloadUri)
                            .resize(imageSideLength, imageSideLength)
                            .centerCrop()
                            .into(eventImageView);
                } else {
                    // Event had no image, so it will stay as default image
                    Log.d("Storage", String.format("No image found, setting default"));
                }
            }
        });
    }

    /**
     * Register user as an entrant for the current event.
     *
     * Button is only visible when the signedInAccount is a user.
     */
    // TODO: check to see if number of entrants have been exceeded (can limit number of people joining waitlist)
    private void registerUser() {
        // if the even already has the user, remove the user upon clicking
        // TODO: rescanning the qr code doesn't make options actually change
        if (event.hasEntrant(entrant)) {
            registerButton.setBackgroundColor(0xFF35B35D);
            event.removeEntrant(entrant);
            DBhandler.updateEvent(event, this);       // Add the changes to the database
            registerButton.setText("Register");
        } else {
            // otherwise, check for enabled geolocation and add entrant accordingly
            if (event.getGeolocation()) {

                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setTitle("WARNING!");
                builder.setMessage("This event requires geolocation. Continue registering?");
                builder.setCancelable(true);
                builder.setPositiveButton("Yes", (DialogInterface.OnClickListener) (dialog, which) -> {
                    // Add the user if they wish to continue (and update button looks)
                    registerButton.setBackgroundColor(0xFF808080);
                    event.addEntrant(entrant);
                    DBhandler.updateEvent(event, this);       // Update the database
                    registerButton.setText("✔");
                });
                builder.setNegativeButton("No", (DialogInterface.OnClickListener) (dialog, which) -> {
                    // Don't add the user to the waitlist
                    dialog.cancel();
                });
                AlertDialog geolocationWarning = builder.create();
                geolocationWarning.show();

            } else {
                // If no geolocation, then the user will just get added
                registerButton.setBackgroundColor(0xFF808080);
                event.addEntrant(entrant);
                DBhandler.updateEvent(event, this);       // Update the database
                registerButton.setText("✔");
            }
        }
    }

    /**
     * Toggles visibility of certain buttons depending on role of signedInAccount.
     */
    private void setPermissions() {
        if (isAdmin) {
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