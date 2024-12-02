package com.example.pygmyhippo.user;

/*
This fragment will display one of the events that a User can see after scanning its QR code
Will be used by users and admins
Citations:
        - ALL Location handling was researched on https://stackoverflow.com/questions/21085497/how-to-use-android-locationmanager-and-listener
          * Code referring to location is sourced from this cite and modified
          Accessed on 2024-11-17 and answered by nisarg parekh
Purposes:
        - Let the User view the details of the event
        - Let the User join the event if they wish
        - If they navigate back to this event, allow them the option to leave the event
        - Let Admin delete the event
Contributors: Katharine, Kori
Issues:
        - Needs testing
        - Needs to stop user from joining or leaving waitlist if the event is closed
        - Time display doesn't work
 */


import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;
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
import com.example.pygmyhippo.database.ImageStorage;
import com.example.pygmyhippo.database.EventDB;
import com.example.pygmyhippo.database.StorageOnCompleteListener;
import com.example.pygmyhippo.databinding.UserFragmentEventBinding;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

/**
 * This fragment will hold a single events from scanning the QR code
 * @author Katharine
 * @version 2.0
 * No returns and no parameters
 */
public class EventFragment extends Fragment implements DBOnCompleteListener<Event>, LocationListener {

    private UserFragmentEventBinding binding;
    private NavController navController;

    private Event event;

    // this is the current user who is trying to join the event
    private Entrant entrant;
    private ArrayList<Entrant> entrants;
    private Account signedInAccount;
    private boolean isAdmin;
    private String eventID;

    private EventDB DBhandler;
    private ImageStorage imageHandler;
    private AccountDB profileDBHandler;
    // For getting current location
    private LocationManager locationManager;

    private TextView eventNameView, eventDateView, eventTimeView, eventOrganizerView,
            eventLocationView, eventCostView, eventAboutDescriptionView;
    private Button registerButton, deleteEventButton, deleteQRCodeButton;
    private ConstraintLayout adminConstraint;
    private ImageView eventImageView;

    /*
        Code is from https://developer.android.com/develop/sensors-and-location/location/permissions#:~:text=ACCESS_FINE_LOCATION%20must%20be%20requested%20with,to%20only%20approximate%20location%20information.
        Accessed on 2024-11-17
        It sets up the permission launcher to ask for location permissions, and then launches it
         */
    ActivityResultLauncher<String> locationPermissionRequest =
            registerForActivityResult(new ActivityResultContracts
                            .RequestPermission(), isGranted -> {

                        if (isGranted) {
                            // Approximate location access granted, set that in the user's profile
                            Log.d("Profile", "Location permissions granted");
                            signedInAccount.setEnableGeolocation(true);
                        } else {
                            // No location access granted.
                            Log.d("Profile", "No location permissions granted");
                            Toast.makeText(getContext(), "Error joining waitlist: Must have geolocation enabled!", Toast.LENGTH_LONG).show();
                            signedInAccount.setEnableGeolocation(false);
                        }

                        // Regardless of choice, update the profile
                        profileDBHandler.updateProfile(signedInAccount, new DBOnCompleteListener<Account>() {
                            @Override
                            public void OnCompleteDB(@NonNull ArrayList<Account> docs, int queryID, int flags) {
                                Log.d("DB", "Profile has been updated");
                            }
                        });
                    }
            );

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            EventFragmentArgs args = EventFragmentArgs.fromBundle(getArguments());
            isAdmin = args.getIsAdmin();
            signedInAccount = args.getSignedInAccount();
            eventID = args.getEventID();
        }
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        navController = Navigation.findNavController(view);

        profileDBHandler = new AccountDB();
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

        DBhandler = new EventDB();
        imageHandler = new ImageStorage();
        locationManager = (LocationManager) requireContext().getSystemService(Context.LOCATION_SERVICE);

        // Get current user account
        setPermissions();

        // Get the eventID that was passed by scanning the QR code
        DBhandler.getEventByID(eventID, this);

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
            DBhandler.deleteQRHashData(event.getEventID(), this);
            deleteQRCodeButton.setBackgroundColor(0xFF808080);
            deleteQRCodeButton.setClickable(false);
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
                Log.d("EventFragment", "Database error in getting event");
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
        else if (queryID == 8){
            if (flags == DBOnCompleteFlags.SUCCESS.value) {
                Log.d("EventFragment", "Successfully deleted QR hashcode");
                Toast.makeText(getContext(), "Delete QR Hashcode", Toast.LENGTH_LONG).show();
            }
            else{
                Log.e("EventFragment", "Unsuccessfully to delete QR hashcode");
                Toast.makeText(getContext(), "Failed to Delete QR Hashcode", Toast.LENGTH_LONG).show();
                deleteQRCodeButton.setClickable(true);
                deleteQRCodeButton.setBackgroundColor(0xFF747DBF); // revert to original colour
            }
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

        if (!event.isValidHashcode()){
            deleteQRCodeButton.setBackgroundColor(0xFF808080);
            deleteQRCodeButton.setClickable(false);
        }


        // Get the organiser so we can display the facility profile stuff
        profileDBHandler.getAccountByID(event.getOrganiserID(), new DBOnCompleteListener<Account>() {
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

        // Get the poster for the event
        imageHandler.getImageDownloadUrl(event.getEventPoster(), new StorageOnCompleteListener<Uri>() {
            @Override
            public void OnCompleteStorage(@NonNull ArrayList<Uri> docs, int queryID, int flags) {
                // Author of this code segment is James
                if (flags == DBOnCompleteFlags.SUCCESS.value) {
                    // Get the image and format it
                    Uri downloadUri = docs.get(0);
                    Picasso.get()
                            .load(downloadUri)
                            .resize(eventImageView.getWidth(), eventImageView.getHeight())
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
    private void registerUser() {
        // if the even already has the user, remove the user upon clicking
        // TODO: rescanning the qr code doesn't make options actually change
        if (event.hasEntrant(entrant)) {
            registerButton.setBackgroundColor(0xFF35B35D);
            event.removeEntrant(entrant);
            DBhandler.updateEvent(event, this);       // Add the changes to the database
            registerButton.setText("Register");
        } else {
            // Check to see if there's a limit and if the event is still open
            if (event.getEventStatus().value.equals("ongoing")
                    && (event.getEventLimitCount() == -1 || event.getEntrants().size() < event.getEventLimitCount())) {

                // otherwise, check for enabled geolocation and add entrant accordingly
                if (event.getGeolocation()) {

                    AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                    builder.setTitle("WARNING!");
                    builder.setMessage("This event requires geolocation. Continue registering?");
                    builder.setCancelable(true);
                    builder.setPositiveButton("Yes", (DialogInterface.OnClickListener) (dialog, which) -> {
                        // Get the user's location
                        // Must check permission before getting location
                        if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                            // Request for permission if there is none
                            // Request will handle if the user granted permission or not
                            locationPermissionRequest.launch(Manifest.permission.ACCESS_COARSE_LOCATION);
                        } else {
                            // Do a double check on geolocation status here because its possible user will update the checkbox
                            // Without the permission actually getting changed. So reflect their desire not to share location
                            if (signedInAccount.isEnableGeolocation()) {
                                // Change the views of the buttons
                                registerButton.setBackgroundColor(0xFF808080);
                                registerButton.setText("✔");

                                // https://stackoverflow.com/questions/16898675/how-does-it-work-requestlocationupdates-locationrequest-listener
                                // Accessed on 2024-11-19, used to help understand when the listener is called
                                locationManager.requestLocationUpdates(locationManager.NETWORK_PROVIDER, 1000, 0, this);
                            }
                        }
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
            } else {
                Toast.makeText(getContext(), "This event is full or closed!", Toast.LENGTH_LONG).show();
            }
        }
    }

    /**
     * This method implements the LocationListener. Used to update the user's location when registering for an event
     * @param location The retrieved location
     */
    @Override
    public void onLocationChanged(Location location) {
        if (location != null) {
            double longitude = location.getLongitude();
            double latitude = location.getLatitude();
            Log.d("Location", String.format("latitude: %f, longitude: %f", latitude, longitude));

            // With the location retrieved, note that in the entrant class and update the event data
            entrant.setLatitude(latitude);
            entrant.setLongitude(longitude);
            event.addEntrant(entrant);
            DBhandler.updateEvent(event, this);       // Update the database
        } else {
            Log.d("Location", "Error location is null");
        }

        locationManager.removeUpdates(this);
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