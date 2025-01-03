package com.example.pygmyhippo.user;

/*
This Fragment will display a chosen event from a user's event list
Will be used by users and admins
Purposes:
        - If the user is chosen from the waitlist, let the user accept the invitation to register/sign up when chosen to participate in an event
        - If the user is chosen from the waitlist, let the user decline an invitation when chosen to participate in an event
        - If the user is not chosen from the waitlist, let the user have another chance to be chosen for the waitlist
Contributors: Katharine, Kori
Issues:
    - When re-navigating to this event after rejecting invitation ect the views aren't that well laid out
    - Time display doesn't work
 */

import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
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
import androidx.core.graphics.drawable.RoundedBitmapDrawableFactory;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;
import com.example.pygmyhippo.R;
import com.example.pygmyhippo.common.Account;
import com.example.pygmyhippo.common.Entrant;
import com.example.pygmyhippo.common.Event;
import com.example.pygmyhippo.database.AccountDB;
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

/**
 * This fragment will hold a single events from my list of events that I have already signed up for
 * @author Katharine
 * @version 1.0
 * No returns and no parameters
 */
public class ViewMyEventFragment extends Fragment implements DBOnCompleteListener<Event>{
    private UserFragmentViewMyeventBinding binding;
    private NavController navController;

    private Event event;
    private Entrant entrant;
    private String eventID;
    private Account signedInAccount;

    private AccountDB accountHandler;
    private EventDB dbHandler;
    private ImageStorage imageHandler;

    private TextView eventNameView, eventDateView, eventTimeView, eventOrganizerView,
            eventLocationView, eventCostView, eventAboutDescriptionView;
    private ImageView eventPoster;

    private TextView userWaitlistStatus, userStatusDescription;
    private Button leaveWaitlistButton;

    // INVITED WAITLIST
    private Button acceptWaitlistButton, declineWaitlistButton;

    // TODO: need to do something with getEvent (ie in user, by eventID passed...)
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        navController = Navigation.findNavController(view);

        // Initialize the handlers
        dbHandler = new EventDB();
        accountHandler = new AccountDB();
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

        // set buttons to invisible
        leaveWaitlistButton.setVisibility(View.GONE);
        acceptWaitlistButton.setVisibility(View.GONE);
        declineWaitlistButton.setVisibility(View.GONE);

        // Set up navigation for the back button to return to last fragment
        FloatingActionButton backButton = view.findViewById(R.id.u_backButtonToMyEvents);
        backButton.setOnClickListener(view1 -> {
            Log.d("ViewMyEventFragment", "Back button pressed");
            navController.popBackStack();
        });

        return view;
    }

    /**
     * Updates text views and buttons in the fragment to reflect the same info in event.
     */
    private void populateAllFields() {
        eventNameView.setText(event.getEventTitle());
        eventDateView.setText(event.getDate());
        eventTimeView.setText(event.getTime());
        eventOrganizerView.setText(event.getOrganiserID());
        eventLocationView.setText(event.getLocation());
        eventCostView.setText(event.getCost());
        eventAboutDescriptionView.setText(event.getDescription());

        if (signedInAccount != null && event.getEntrants() != null) {
            // Get the corresponding entrant class to the account and event
            entrant = event.getEntrants()
                    .stream()
                    .filter(e -> e.getAccountID().equals(signedInAccount.getAccountID()))
                    .findFirst()
                    .orElse(null);
        }

        // If the entrant is already accepted then display that
        if (entrant.getEntrantStatus().equals(Entrant.EntrantStatus.accepted)) {
            userWaitlistStatus.setText("ACCEPTED!");
            userStatusDescription.setText("You are officially accepted into this event!");
        } else if (entrant.getEntrantStatus().equals(Entrant.EntrantStatus.cancelled)) {
            userWaitlistStatus.setText("CANCELLED");
            userStatusDescription.setText("The organiser has revoked your invite");
        }
        else if (entrant.getEntrantStatus().equals(Entrant.EntrantStatus.lost)){
            lostWaitlistSelection();
        }

        // Get the organiser so we can display the facility profile stuff
        accountHandler.getAccountByID(event.getOrganiserID(), new DBOnCompleteListener<Account>() {
            @Override
            public void OnCompleteDB(@NonNull ArrayList<Account> docs, int queryID, int flags) {
                if (flags == DBOnCompleteFlags.SINGLE_DOCUMENT.value) {
                    // Get the organiser account
                    Account organiserAccount = docs.get(0);
                    eventOrganizerView.setText(organiserAccount.getFacilityProfile().getName());

                    // Convert the facility image url to a drawable
                    // https://stackoverflow.com/questions/56754123/how-can-i-get-drawable-from-glide-actually-i-want-to-return-drawable-from-glide
                    // Accessed on 2024-11-29
                    String imagePath = organiserAccount.getFacilityProfile().getFacilityPicture();
                    if (!imagePath.isEmpty()) {
                        Glide.with(getContext())
                                .asBitmap()
                                .load(imagePath)
                                .into(new CustomTarget<Bitmap>() {
                                    @Override
                                    public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                                        // https://stackoverflow.com/questions/2415619/how-to-convert-a-bitmap-to-drawable-in-android
                                        // 2024-11-29
                                        Drawable facilityDrawable = RoundedBitmapDrawableFactory.create(getResources(), resource);

                                        // Set the bounds of the drawable
                                        facilityDrawable.setBounds(0, 0, 100, 100);

                                        // Set the drawable
                                        eventOrganizerView.setCompoundDrawables(facilityDrawable, null, null, null);
                                    }

                                    @Override
                                    public void onLoadCleared(@Nullable Drawable placeholder) {
                                    }
                                });
                    }
                }
            }
        });

        // Get the event poster from firebase
        imageHandler.getImageDownloadUrl(event.getEventPoster(), new StorageOnCompleteListener<Uri>() {
            @Override
            public void OnCompleteStorage(@NonNull ArrayList<Uri> docs, int queryID, int flags) {
                // Author of this code segment is James
                if (flags == DBOnCompleteFlags.SUCCESS.value) {
                    // Get the image and format it
                    Uri downloadUri = docs.get(0);
                    Picasso.get()
                            .load(downloadUri)
                            .resize(eventPoster.getWidth(), eventPoster.getHeight())
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

                if (event.getEventStatus().value.equals("ongoing")) {
                    pendingWaitlist();
                } else if (event.getEntrants().stream().anyMatch(e -> e.getAccountID().equals(entrant.getAccountID()) && e.getEntrantStatus() == Entrant.EntrantStatus.invited)) {
                    wonWaitlistSelection();
                // this is the case where the user did not get picked and the event is no longer ongoing
                } else if (event.getEntrants().stream().anyMatch(e -> e.getAccountID().equals(entrant.getAccountID()) && e.getEntrantStatus() == Entrant.EntrantStatus.lost)) {
                    lostWaitlistSelection();
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

    /**
     * If the event is still open, the user is able to leave the waitlist.
     */
    public void pendingWaitlist() {
        userWaitlistStatus.setText("PENDING");
        userStatusDescription.setVisibility(View.VISIBLE);
        userStatusDescription.setText("The waitlist is still open. Do you want to leave this event?");
        leaveWaitlistButton.setVisibility(View.VISIBLE);

        leaveWaitlistButton.setOnClickListener(view -> {
            // Remove them from the waitlist and update everything
            event.removeEntrant(entrant);
            dbHandler.updateEvent(event, this);
            leaveWaitlistButton.setVisibility(View.GONE);
            userWaitlistStatus.setText("UNAVAILABLE");
            userStatusDescription.setText("You have left this event. Sign up again using a QR code.");
        });
    }


    /**
     * If the user has been invited to the event, the user will have the option to accept or decline the offer.
     */
    public void wonWaitlistSelection() {
        // populate the views, make sure to make buttons visible,
        // when button is clicked, either set user status to accepted or just remove the user entirely from the waitlist
        userWaitlistStatus.setText("WON");
        userStatusDescription.setVisibility(View.VISIBLE);
        userStatusDescription.setText("You have been accepted from the waitlist to attend this event. Do you accept or decline this invitation?");
        acceptWaitlistButton.setText("Accept");
        declineWaitlistButton.setText("Decline");
        acceptWaitlistButton.setVisibility(View.VISIBLE);
        declineWaitlistButton.setVisibility(View.VISIBLE);

        acceptWaitlistButton.setOnClickListener(view -> {
            // Set the entrant status to accepted and update the database
            // https://hellokoding.com/query-an-arraylist-in-java/
            event.getEntrants()
                    .stream()
                    .filter(e -> (e.getAccountID().equals(entrant.getAccountID())))
                    .findFirst()
                    .ifPresent(e -> e.setEntrantStatus(Entrant.EntrantStatus.accepted));
            dbHandler.updateEvent(event, this);
            // Update the views
            userWaitlistStatus.setText("ACCEPTED");
            userStatusDescription.setText("You are officially accepted into this event!");
            acceptWaitlistButton.setVisibility(View.GONE);
            declineWaitlistButton.setVisibility(View.GONE);
        });

        declineWaitlistButton.setOnClickListener(view -> {
            // Entrant rejected the invite
            event.getEntrants()
                    .stream()
                    .filter(e -> (e.getAccountID().equals(entrant.getAccountID())))
                    .findFirst()
                    .ifPresent(e -> e.setEntrantStatus(Entrant.EntrantStatus.rejected));
            dbHandler.updateEvent(event, this);
            // Update the views
            userWaitlistStatus.setText("DECLINED");
            userStatusDescription.setText("You have declined to join the event :(");
            acceptWaitlistButton.setVisibility(View.GONE);
            declineWaitlistButton.setVisibility(View.GONE);
        });
    }

    public void lostWaitlistSelection() {
        userWaitlistStatus.setText("LOST");
        userStatusDescription.setVisibility(View.VISIBLE);
        userStatusDescription.setText("You have lost the lottery to attend this event. You will be notified if you win a lottery redraw. Do you want to stay on the waitlist?");
        declineWaitlistButton.setText("No");
        declineWaitlistButton.setVisibility(View.VISIBLE);

        // Entrant decides not to wait for redraw
        declineWaitlistButton.setOnClickListener(view -> {
            event.removeEntrant(entrant);
            dbHandler.updateEvent(event, this);
            userStatusDescription.setText("You have declined to join the event :(");
            acceptWaitlistButton.setVisibility(View.GONE);
            declineWaitlistButton.setVisibility(View.GONE);
        });
    }
}
