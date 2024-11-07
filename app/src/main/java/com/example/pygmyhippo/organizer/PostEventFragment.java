package com.example.pygmyhippo.organizer;

import static androidx.navigation.Navigation.findNavController;

import android.net.Uri;
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
import com.example.pygmyhippo.common.Event;
import com.example.pygmyhippo.database.DBOnCompleteFlags;
import com.example.pygmyhippo.database.DBOnCompleteListener;
import com.example.pygmyhippo.databinding.OrganiserPostEventBinding;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.UUID;
/*
* This fragment is for posting an event
*  TODO:
 *  - hide the navigation when a keyboard pops up
 *  - set a standard size for post images
 *  - request that the database generated an id for the event
 *  - generate a hash id for the event
 *  Thinking about:
 *   - should the current progress of the event reset if the organiser switches screen
 *   - a button called "Clear Fields" at the top to clear event feild
 *   - really similar idea can be applied to editing an event
 * */



/**
 * This fragment will hold the post event
 * @author Griffin
 * @version 1.1
 * No returns and no parameters
 */
public class PostEventFragment extends Fragment implements DBOnCompleteListener<Event>{

    private OrganiserPostEventBinding binding;
    private NavController navController;

    private EditText eventNameEdit, eventDateTimeEdit, eventPriceEdit, eventLocationEdit, eventDescriptionEdit, eventLimitEdit, eventWinnersEdit;
    private CheckBox eventGeolocation;
    private ImageButton eventImageBtn;
    private String imagePath = null;
    private EventDB handler;
    private ImageStorage ImageHandler;


    /**
     * Creates the view
     * @author Griffin
     * @param inflater not sure
     * @param container not sure
     * @param savedInstanceState  not surre
     * @return root not sure
     */
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        binding = OrganiserPostEventBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        handler = new EventDB();
        ImageHandler = new ImageStorage();

        return root;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        navController = findNavController(view);

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
                myEvent.setOrganiserID("currentUserID"); // TODO: get the current organiser
                myEvent.setLocation(eventLocation);
                myEvent.setDate(eventDateTime);
                myEvent.setDescription(eventDescription);
                myEvent.setCost(eventPrice);
                myEvent.setEventPoster(imagePath == null? "" : imagePath);
                myEvent.setEventLimitCount(eventLimit.isEmpty() ? -1 : Integer.valueOf(eventLimit));
                myEvent.setEventWinnersCount(Integer.valueOf(eventWinners));
                myEvent.setEntrants(new ArrayList<>()); // no entrants of a newly created event
                myEvent.setGeolocation(eventGeolocaion);
                myEvent.setEventStatus(Event.EventStatus.ongoing); // default is ongoing
                handler.addEvent(myEvent, this); // TODO: Get organiser ID and pass it to addEvent.
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
                    // TODO: add image upload to database
                    ImageHandler.uploadImageToFirebase(uri, this);
                }
                else{
                    // sets image path to null if no image is selected
                    imagePath = null;
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

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    @Override
    public void OnComplete(@NonNull ArrayList<Event> docs, int queryID, int flags) {
        if (queryID == 0) {
            if (flags == DBOnCompleteFlags.SUCCESS.value) {
                clearEditTextFields();
                Event newEvent = docs.get(0);
                // create a bundle to send an event to the qr code widget
                // the bundle must have a id for the qr code to generate
                // event should be added to data base then it should be
                Bundle eventBundle = new Bundle();
                eventBundle.putString("my_event_id", newEvent.getEventID());
                navController.navigate(R.id.action_organiser_postEvent_page_to_view_eventqr_fragment, eventBundle);
            } else {
                Toast.makeText(getContext(), "Event Failed to Create", Toast.LENGTH_LONG).show();
            }
        }
        else if (queryID == 3){
            if (flags == DBOnCompleteFlags.SUCCESS.value) {
                clearEditTextFields();
                Event newEvent = docs.get(0);
                imagePath = newEvent.getEventPoster();
            }
            else {
                Toast.makeText(getContext(), "Image failed to upload", Toast.LENGTH_LONG).show();
            }
        }
    }
}
