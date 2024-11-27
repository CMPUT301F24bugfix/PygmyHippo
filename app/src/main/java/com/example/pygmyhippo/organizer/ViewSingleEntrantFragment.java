package com.example.pygmyhippo.organizer;

/*
This Fragment is meant for the Organizer to view a single entrant from the list of entrants of their event
Will contain the functionality to filter the given list depending on the status of the entrant
Deals with user stories 02.06.04 currently
Author: Kori

Purposes:
    - Allow the organiser to view a single entrant
    - Let them see the entrant status and give them the option to cancel them if they are invited (updated in database)
    - Give them the option to draw a replacement winner on entrants with cancelled status
Issues:
    - Notifications haven't been dealt with yet
 */

import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.example.pygmyhippo.common.Account;
import com.example.pygmyhippo.common.Entrant;
import com.example.pygmyhippo.common.Event;
import com.example.pygmyhippo.database.AccountDB;
import com.example.pygmyhippo.database.DBOnCompleteFlags;
import com.example.pygmyhippo.database.DBOnCompleteListener;
import com.example.pygmyhippo.database.EventDB;
import com.example.pygmyhippo.database.ImageStorage;
import com.example.pygmyhippo.database.StorageOnCompleteListener;
import com.example.pygmyhippo.databinding.OrganiserViewSingleEntrantBinding;
import com.squareup.picasso.Picasso;

import java.net.URISyntaxException;
import java.util.ArrayList;

/**
 * Fragment to see a user profile from navigating from an organiser's list of entrants
 * TODO: Add functions for the other status conditions (accepted and such)
 * TODO: Notification sending can also be done when one of the buttons are pressed (just one possible idea)
 */
public class ViewSingleEntrantFragment extends Fragment implements DBOnCompleteListener<Event>, StorageOnCompleteListener<Uri> {
    private Account account;
    private String accountID;
    private String status;

    private String eventID;
    private Event event;
    private Entrant entrant;

    private EventDB dbHandler;
    private AccountDB dbProfileHandler;
    private ImageStorage imageHandler;
    private NavController navController;

    private boolean mapDimensionsGotten = false;     // A flag

    private FrameLayout mapView;
    private FrameLayout mapMarker;
    private int mapSideLength;
    private Button statusButton;
    private TextView locationTextView, statusTextView, userNameView, pronounsTextView, emailTextView, phoneTextView;
    private ImageView profileImage;
    private Integer falseEasting = 180;             // Used in the map projection

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        navController = Navigation.findNavController(view);
    }

    /**
     * In on create View, the text fields are updated and on click listeners are set
     * @param inflater The LayoutInflater object that can be used to inflate
     * any views in the fragment,
     * @param container If non-null, this is the parent view that the fragment's
     * UI should be attached to.  The fragment should not add the view itself,
     * but this can be used to generate the LayoutParams of the view.
     * @param savedInstanceState If non-null, this fragment is being re-constructed
     * from a previous saved state as given here.
     *
     * @return the view
     */
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        OrganiserViewSingleEntrantBinding binding = OrganiserViewSingleEntrantBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        // Get the account and entrant status passed from the ViewEntrantsFragment
        accountID = getArguments().getString("accountID");
        status = getArguments().getString("status");
        eventID = getArguments().getString("eventID");

        // Initialize the handlers
        dbHandler = new EventDB();
        dbProfileHandler = new AccountDB();
        imageHandler = new ImageStorage();

        // Get the textviews and buttons
        profileImage = binding.EProfileProfileImg;
        ImageButton backButton = binding.entrantViewBackButton;
        userNameView = binding.eUsername;
        pronounsTextView = binding.entrantProunouns;
        emailTextView = binding.entrantEmail;
        phoneTextView = binding.entrantPhone;
        statusTextView = binding.entrantStatus;
        statusButton = binding.eStatusButton;
        locationTextView = binding.ELocationLabel;
        mapView = binding.eMap;
        mapMarker = binding.mapMarker;

        // Set the status indication
        statusTextView.setText(status);

        // Set up an onclick listener to go back to the list
        Bundle navArgs = new Bundle();
        navArgs.putString("eventID", eventID);
        backButton.setOnClickListener(view -> navController.popBackStack());

        // Only start filling fields once the map layout is initialized (or else dimensions are zero)
        // Referenced from https://stackoverflow.com/questions/43633485/why-we-use-viewtreeobserveraddongloballayoutlistener
        // Accessed on 2024-11-19
        mapView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                if (!mapDimensionsGotten) {         // Only want to utilize this listener once
                    // Get the layout fields
                    mapSideLength = mapView.getWidth();

                    // Get the event
                    dbHandler.getEventByID(eventID, ViewSingleEntrantFragment.this::OnCompleteDB);

                    // Set the flag if the values aren't zero
                    if (mapSideLength != 0) {
                        mapDimensionsGotten = true;
                    }
                }
            }
        });

        return root;
    }

    /**
     * If a user has no uploaded image, then the avatar will be made
     * and set the image view to the URI
     * @author Jennifer
     * @param name the name the user has entered
     * @return void
     * @version 1.0
     */
    public void generateAvatar (String name) throws URISyntaxException {
        if (name.isEmpty()) name = "null";
        String url = "https://api.multiavatar.com/";
        Uri avatarURI = Uri.parse(url+name+".png");

        // Retrieve the generated profile picture
        Picasso.get()
                .load(avatarURI)
                .into(profileImage);
    }

    /**
     * This method will populate the account fields and initiate the map if geolocation is on
     * @author Kori
     */
    public void populateAccountFields() {
        // Get the account from the database
        dbProfileHandler.getAccountByID(accountID, new DBOnCompleteListener<Account>() {
            @Override
            public void OnCompleteDB(@NonNull ArrayList<Account> docs, int queryID, int flags) {
                if (flags == DBOnCompleteFlags.SINGLE_DOCUMENT.value) {
                    // Get the account and corresponding entrant class
                    account = docs.get(0);

                    // Set all the views once we get the data
                    userNameView.setText(account.getName());
                    pronounsTextView.setText(account.getPronouns());
                    emailTextView.setText(account.getEmailAddress());
                    phoneTextView.setText(account.getPhoneNumber());

                    // Set up the image if the account has
                    if (!account.getProfilePicture().isEmpty()) {
                        imageHandler.getImageDownloadUrl(account.getProfilePicture(), ViewSingleEntrantFragment.this::OnCompleteStorage);
                    } else {
                        // The account hasn't uploaded an image so generate the avatar
                        try {
                            generateAvatar(account.getName());
                        } catch (URISyntaxException e) {
                            throw new RuntimeException(e);
                        }
                    }

                    // Check if geolocation is set, if it is display the map and location
                    if (account.isEnableGeolocation() && event.getEnableGeolocation()) {
                        Log.d("Location", "Account has geolocation, begin map formatting");

                        formatMap(mapSideLength);        // Display map
                        displayLocation(entrant.getLatitude(), entrant.getLongitude());
                    }
                } else {
                    // Should only ever expect 1 document, otherwise there must be an error
                    handleDBError();
                }
            }
        });
    }

    /**
     * This method will set up the on click listener for the status button depending on the entrant's status
     * @author Kori
     */
    public void setUpStatusButton() {
        Log.d("Debug", "Setting up status button");

        // Depending on entrant status, display and/or alter the status button
        // TODO: Add settings for other statuses and provide more functionalities than just cancel
        if (status.equals("invited")) {
            // If the entrant is invited, then display the button to allow cancelling them
            statusButton.setClickable(true);
            statusButton.setVisibility(View.VISIBLE);

            // Set the listener so it'll change the status of the entrant to cancelled
            statusButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    // First change the display manually
                    statusTextView.setText("cancelled");

                    // Change the entrant status using the event
                    findAndUpdateEntrant(accountID, event, "cancelled");

                    // Make the button disappear after the click
                    statusButton.setClickable(false);
                    statusButton.setVisibility(View.GONE);
                }
            });
        }

        // Populate the account views once the button has been set up
        populateAccountFields();
    }

    /**
     * This method will find and update the status of the given entrant and reflect the changes to the database
     * @author Kori
     * @param accountID The ID of the entrant we want to update
     * @param event The event we want to update
     * @param newStatus The new status we want to give the entrant
     */
    public void findAndUpdateEntrant(String accountID, Event event, String newStatus) {
        // Found the entrant, so update there status in the event
        entrant = event.getEntrant(accountID);
        entrant.setEntrantStatus(Entrant.EntrantStatus.valueOf(newStatus));

        // Now send this updated event to the database to update it
        dbHandler.updateEvent(event, this);
    }

    /**
     * This method will set the height of the map depending on its width
     * After it will make the map visible
     * @author Kori
     */
    public void formatMap(int mapSideLength) {
        Log.d("Location", String.format("Displaying SideLength: %d", mapSideLength));

        // Get the layout params of the map and set the height
        ConstraintLayout.LayoutParams mapLayoutParams = (ConstraintLayout.LayoutParams) mapView.getLayoutParams();
        mapLayoutParams.height = mapSideLength;

        // Apply the new height to the map
        mapView.setLayoutParams(mapLayoutParams);

        // Make the map visible and its label
        mapView.setVisibility(View.VISIBLE);
        locationTextView.setVisibility(View.VISIBLE);
    }

    /**
     * This method will use Mercator projection to find the pixel coordinates on our map, given latitude and longitude
     * The formula for the conversion is sourced from https://medium.com/@suverov.dmitriy/how-to-convert-latitude-and-longitude-coordinates-into-pixel-offsets-8461093cb9f5
     *      - accessed on 2024-11-19
     * @param latitude The given latitude location in degrees
     * @param longitude The given longitude value in degrees
     */
    public void displayLocation(double latitude, double longitude) {
        Log.d("Location", String.format("Displaying longitude: %f latitude: %f", longitude, latitude));

        // First get the radius of our map
        double radius = mapSideLength / (Math.PI * 2);

        // Convert longitude to its respective x-coordinate given the formula from the cite
        // False Easting is used to parameterize the x coordinate to go off the top edge
        double xCoordinate = degreesToRadians(longitude + falseEasting) * radius;

        // Calculate the vertical offset given the formula from the cite mentioned
        // Vertical offset will allow us to use the top left corner as reference
        double latitudeRadians = degreesToRadians(latitude);
        double verticalOffsetFromEquator = radius * Math.log(Math.tan((Math.PI / 4) + (latitudeRadians / 2)));

        Log.d("Location", String.format("Vertical offset = %f", verticalOffsetFromEquator));

        // Convert the latitude to its y-coordinate given the formula from the cite
        double yCoordinate = ((float) mapSideLength / 2) - verticalOffsetFromEquator;

        // Now format the marker on the map to these coordinates accounting for its center (want the bottom tip on the point)
        // Layout params was gotten on 2024-11-19 on the cite:
        // https://stackoverflow.com/questions/12728255/in-android-how-do-i-set-margins-in-dp-programmatically
        FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) mapMarker.getLayoutParams();

        xCoordinate = xCoordinate - ((float) mapMarker.getWidth() / 2);
        yCoordinate = yCoordinate - mapMarker.getHeight();

        Log.d("Location", String.format("x-coordinate: %f y-coordinate: %f", xCoordinate, yCoordinate));

        // Set the final margins
        layoutParams.setMargins((int) xCoordinate, (int) yCoordinate, 0, 0);
        mapMarker.setLayoutParams(layoutParams);
    }

    /**
     * Displays DB Errors
     */
    private void handleDBError() {
        Toast toast = Toast.makeText(getContext(), "DB Error!", Toast.LENGTH_SHORT);
        toast.show();
    }

    /**
     * This method is sourced from https://medium.com/@suverov.dmitriy/how-to-convert-latitude-and-longitude-coordinates-into-pixel-offsets-8461093cb9f5
     * accessed on 2024-11-19. The method converts degrees to radians so we can project onto our map
     * @param degrees The value we want to convert to radian
     * @return The radian equivalent
     */
    public double degreesToRadians(double degrees) {
        return (degrees * Math.PI) / 180;
    }

    /**
     * The callback when data is retrieved
     * @param docs - Documents retrieved from DB (if it was a get query).
     * @param queryID - ID of query completed.
     * @param flags - Flags to indicate query status/set how to process query result.
     */
    @Override
    public void OnCompleteDB(@NonNull ArrayList<Event> docs, int queryID, int flags) {
        if (queryID == 1) {
            if (flags == DBOnCompleteFlags.SINGLE_DOCUMENT.value) {
                // Get the event from the query and initialize the entrant
                event = docs.get(0);
                entrant = event.getEntrant(accountID);

                Log.d("DB", String.format("Successfully got entrant with ID (%s).", entrant.getAccountID()));

                // Continue setting up the event with the event
                setUpStatusButton();
            }
        } else if (queryID == 2) {
            // Log when the data is updated or catch if there was an error
            if (flags == DBOnCompleteFlags.SUCCESS.value) {
                Log.d("DB", String.format("Successfully finished updating event with ID (%s).", eventID));
            } else {
                // If not the success flag, then there was an error
                handleDBError();
            }
        }
    }

    /**
     * The callback that handles image queries
     * @param docs - Documents retrieved from DB (if it was a get query).
     * @param queryID - ID of query completed.
     * @param flags - Flags to indicate query status/set how to process query result.
     */
    @Override
    public void OnCompleteStorage(@NonNull ArrayList<Uri> docs, int queryID, int flags) {
        if (queryID == 1) {
            // Author of this code segment is James
            if (flags == DBOnCompleteFlags.SUCCESS.value) {
                // Get the image and format it
                Uri downloadUri = docs.get(0);
                int imageSideLength = profileImage.getWidth() / 2;
                Picasso.get()
                        .load(downloadUri)
                        .resize(imageSideLength, imageSideLength)
                        .centerCrop()
                        .into(profileImage);
            } else {
                // There was some error so fallback on avatar
                handleDBError();

                try {
                    generateAvatar(account.getName());
                } catch (URISyntaxException e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }
}
