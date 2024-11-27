package com.example.pygmyhippo.organizer;

/*
 * This fragment is for posting an event
 * Purposes:
 *      - Gives the organiser the ability to post their event
 *  TODO: / Issues
 *   - hide the navigation when a keyboard pops up
 *   - set a standard size for post images
 *  Thinking about:
 *   - should the current progress of the event reset if the organiser switches screen
 *   - a button called "Clear Fields" at the top to clear event field
 *   - really similar idea can be applied to editing an event
 * */

import static androidx.navigation.Navigation.findNavController;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.PickVisualMediaRequest;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;

import com.example.pygmyhippo.R;
import com.example.pygmyhippo.common.Account;
import com.example.pygmyhippo.common.Event;
import com.example.pygmyhippo.common.Image;
import com.example.pygmyhippo.database.DBOnCompleteFlags;
import com.example.pygmyhippo.database.DBOnCompleteListener;
import com.example.pygmyhippo.database.EventDB;
import com.example.pygmyhippo.database.ImageStorage;
import com.example.pygmyhippo.database.StorageOnCompleteListener;
import com.example.pygmyhippo.databinding.OrganiserPostEventBinding;

import java.util.ArrayList;


/**
 * This fragment will hold the post event
 * @author Griffin
 * @version 1.1
 * No returns and no parameters
 */
public class PostEventFragment extends Fragment implements DBOnCompleteListener<Event>, StorageOnCompleteListener<Image> {

    private OrganiserPostEventBinding binding;
    private NavController navController;

    private EditText eventNameEdit, eventDateTimeEdit, eventPriceEdit, eventLocationEdit, eventDescriptionEdit, eventLimitEdit, eventWinnersEdit;
    private CheckBox eventGeolocation;
    private ImageButton eventImageBtn;
    private String imagePath = "";
    private EventDB handler;
    private ImageStorage ImageHandler;

    private Account signedInAccount;


    /**
     * Creates the view
     * @author Griffin
     * @param inflater The LayoutInflater object that can be used to inflate
     * any views in the fragment,
     * @param container If non-null, this is the parent view that the fragment's
     * UI should be attached to.  The fragment should not add the view itself,
     * but this can be used to generate the LayoutParams of the view.
     * @param savedInstanceState If non-null, this fragment is being re-constructed
     * from a previous saved state as given here.
     *
     * @return
     */
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        binding = OrganiserPostEventBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        handler = new EventDB();
        ImageHandler = new ImageStorage();

        // Get the current account that was passed to this fragment
        if (getArguments() != null) {
            signedInAccount = PostEventFragmentArgs.fromBundle(getArguments()).getSignedInAccount();
        }

        return root;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        navController = findNavController(view);

        // Get all the fields we want to get event data from
        eventNameEdit = view.findViewById(R.id.o_postEvent_name_edit);
        eventDateTimeEdit = view.findViewById(R.id.o_postEvent_dataTime_edit);
        eventPriceEdit = view.findViewById(R.id.o_postEvent_price_edit);
        eventLocationEdit = view.findViewById(R.id.o_postEvent_location_edit);
        eventDescriptionEdit = view.findViewById(R.id.o_postEvent_description_edit);
        eventLimitEdit = view.findViewById(R.id.o_postEvent_limit_edit);
        eventWinnersEdit = view.findViewById(R.id.o_postEvent_winners_edit);
        eventGeolocation = view.findViewById(R.id.o_postEvent_geolocation_check);
        Button postEventButton = view.findViewById(R.id.o_postEvent_post_button);

        Event myEvent = new Event();

        postEventButton.setOnClickListener(v -> {
            // Get the inputs from the textViews
            String eventName = eventNameEdit.getText().toString();
            String eventDateTime = eventDateTimeEdit.getText().toString();
            String eventPrice = eventPriceEdit.getText().toString();
            String eventLocation = eventLocationEdit.getText().toString();
            String eventDescription = eventDescriptionEdit.getText().toString();
            String eventLimit = eventLimitEdit.getText().toString();
            String eventWinners = eventWinnersEdit.getText().toString();
            Boolean eventGeolocaion = eventGeolocation.isChecked();
            if (
                    eventName.isEmpty() ||
                    eventDateTime.isEmpty() ||
                    eventPrice.isEmpty() ||
                    eventLocation.isEmpty() ||
                    eventDescription.isEmpty() ||
                    eventWinners.isEmpty())
            {
                // Toast alerts organiser that event is missing feilds
                Toast.makeText(getContext(), "Required fields missing!", Toast.LENGTH_SHORT).show();
            } else {
                myEvent.setEventTitle(eventName);
                myEvent.setOrganiserID(signedInAccount.getAccountID());
                myEvent.setLocation(eventLocation);
                myEvent.setDate(eventDateTime);
                myEvent.setDescription(eventDescription);
                myEvent.setCost(eventPrice);
                myEvent.setEventPoster(imagePath);
                myEvent.setEventLimitCount(eventLimit.isEmpty() ? -1 : Integer.valueOf(eventLimit));
                myEvent.setEventWinnersCount(Integer.valueOf(eventWinners));
                myEvent.setEntrants(new ArrayList<>()); // no entrants of a newly created event
                myEvent.setGeolocation(eventGeolocaion);
                myEvent.setEventStatus(Event.EventStatus.ongoing); // default is ongoing
                handler.addEvent(myEvent, this); // TODO: Get organiser ID and pass it to addEvent.??? (solved above)
            }
        });

        // image picking section of onview created
        eventImageBtn = view.findViewById(R.id.o_postEvent_addImage);

        // this code was taken from Jens upload avatar profile code
        eventImageBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pickMedia.launch(new PickVisualMediaRequest.Builder()
                        .setMediaType(ActivityResultContracts.PickVisualMedia.ImageOnly.INSTANCE)
                        .build());
            }
        });
    }

    /**
     * Allows the user to select an image for the event
     * @author griffin
     */
    private final ActivityResultLauncher<PickVisualMediaRequest> pickMedia =
            registerForActivityResult(new ActivityResultContracts.PickVisualMedia(), uri -> {
                if (uri != null) {
                    eventImageBtn.setImageURI(uri);
                    eventImageBtn.setScaleType(ImageView.ScaleType.CENTER_CROP);
                    // center cropped can be changed if we want to scale the picture differently
                    ImageHandler.uploadEventImageToFirebase(uri, this);
                }
                else{
                    // sets image path to null if no image is selected
                    imagePath = "";
                }
            });

    /**
     * Clears the event in the poster
     * @author griffin
     */
    public void clearEditTextFields(){
        eventNameEdit.setText("");
        eventDateTimeEdit.setText("");
        eventPriceEdit.setText("");
        eventLocationEdit.setText("");
        eventDescriptionEdit.setText("");
        eventLimitEdit.setText("");
        eventWinnersEdit.setText("");
        eventGeolocation.setChecked(false);
    }

    /**
     * Destroys view
     */
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    /**
     * Callback called when view entrant DB queries complete.
     * @param docs - Documents retrieved from DB (if it was a get query).
     * @param queryID - ID of query completed.
     * @param flags - Flags to indicate query status/set how to process query result.
     */
    @Override
    public void OnCompleteDB(@NonNull ArrayList<Event> docs, int queryID, int flags) {
        if (queryID == 0) {
            if (flags == DBOnCompleteFlags.SUCCESS.value) {
                Event newEvent = docs.get(0);
                if(newEvent.getHashcode() == 0){
                    boolean hashcodeValid = newEvent.tryGenerateHashcode();
                    if(hashcodeValid) {
                        Log.i("Post Event", String.format("Event generated with hashcode", newEvent.getHashcode()));
                        handler.updateEvent(newEvent, this);
                    }
                    else {
                        Log.e("Post Event", "Hashcode generation unsuccessful");
                        Toast.makeText(getContext(), "Event Failed to Create", Toast.LENGTH_LONG).show();
                    }
                }
            } else {
                Log.e("Post Event", "Hashcode generation unsuccessful");
                Toast.makeText(getContext(), "Event Failed to Create", Toast.LENGTH_LONG).show();
            }
        }
        else if (queryID == 2){
            if (flags == DBOnCompleteFlags.SUCCESS.value) {
                clearEditTextFields();
                Event newEvent = docs.get(0);
                Bundle navArgs = new Bundle();
                navArgs.putString("eventID", newEvent.getEventID());
                navArgs.putParcelable("signedInAccount", signedInAccount);
                navController.navigate(R.id.view_eventqr_fragment, navArgs);
            }
            else {
                Log.e("Post Event", "Hashcode generation unsuccessful");
                Toast.makeText(getContext(), "Event Failed to Create", Toast.LENGTH_LONG).show();
            }
        }
    }

    /**
     * Callback called when view entrant Storage queries complete.
     * @param docs - Documents retrieved from DB (if it was a get query).
     * @param queryID - ID of query completed.
     * @param flags - Flags to indicate query status/set how to process query result.
     */
    @Override
    public void OnCompleteStorage(@NonNull ArrayList<Image> docs, int queryID, int flags) {
          if (queryID == 0){ // posts image and link to event
            if (flags == DBOnCompleteFlags.SUCCESS.value) {
                Log.i("Post Event", "Image upload successful");
                Toast.makeText(getContext(), "Image uploaded", Toast.LENGTH_LONG).show();
                Image newImage = docs.get(0);
                imagePath = newImage.getUrl(); // set the image path from the URL
            }
            else {
                Log.e("Post Event", "Image upload unsuccessful");
                Toast.makeText(getContext(), "Image failed to upload", Toast.LENGTH_LONG).show();
            }
        }
    }
}
