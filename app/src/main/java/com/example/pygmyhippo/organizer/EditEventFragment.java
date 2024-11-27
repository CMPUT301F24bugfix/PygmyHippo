package com.example.pygmyhippo.organizer;

/*
 * This fragment is for editing an event
 * Purposes:
 *      - Gives the organiser the ability to edit an event
 *  TODO: / Issues
 *   - hide the navigation when a keyboard pops up
 *   - set a standard size for post images
 *  Thinking about:
 *   - should the current progress of the event reset if the organiser switches screen
 *   - a button called "Clear Fields" at the top to clear event field
 *   - really similar idea can be applied to editing an event
 * */

import static androidx.navigation.Navigation.findNavController;

import android.app.DatePickerDialog;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
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
import com.example.pygmyhippo.databinding.OrganiserEditEventBinding;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Calendar;


/**
 * This fragment will hold the post event
 * @author Griffin
 * @version 1.1
 * No returns and no parameters
 */
public class EditEventFragment extends Fragment implements DBOnCompleteListener<Event>, StorageOnCompleteListener<Image> {

    private OrganiserEditEventBinding binding;
    private NavController navController;

    private EditText eventNameEdit, eventDateTimeEdit, eventPriceEdit, eventLocationEdit, eventDescriptionEdit, eventLimitEdit, eventWinnersEdit;
    private CheckBox eventGeolocation;
    private ImageButton eventImageBtn;
    private String imagePath = null;
    private EventDB handler;
    private ImageStorage ImageHandler;

    private Account signedInAccount;
    private Event updatedEvent;
    private Button editEventButton;


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

        binding = OrganiserEditEventBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        handler = new EventDB();
        ImageHandler = new ImageStorage();
        updatedEvent = new Event();

        // Get the current account and event that was passed to this fragment
        if (getArguments() != null) {
            signedInAccount = EditEventFragmentArgs.fromBundle(getArguments()).getSignedInAccount();
            updatedEvent.setEventID(EditEventFragmentArgs.fromBundle(getArguments()).getEventID());
        }

        return root;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        navController = findNavController(view);

        // Get all the fields we want to get event data from
        eventNameEdit = view.findViewById(R.id.o_editEvent_name_edit);
        eventDateTimeEdit = view.findViewById(R.id.o_editEvent_dataTime_edit);
        eventPriceEdit = view.findViewById(R.id.o_editEvent_price_edit);
        eventLocationEdit = view.findViewById(R.id.o_editEvent_location_edit);
        eventDescriptionEdit = view.findViewById(R.id.o_editEvent_description_edit);
        editEventButton = view.findViewById(R.id.o_editEvent_update_button);

        eventDateTimeEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final Calendar c = Calendar.getInstance();

                int year = c.get(Calendar.YEAR);
                int month = c.get(Calendar.MONTH);
                int day = c.get(Calendar.DAY_OF_MONTH);

                DatePickerDialog datePicker = new DatePickerDialog(
                        getActivity(),
                        new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                                eventDateTimeEdit.setText(day + "/" + (month + 1) + "/" + year);
                            }
                        },
                        year, month, day);

                datePicker.show();
            }
        });

        editEventButton.setOnClickListener(v -> {
            // Get the inputs from the textViews
            String eventName = eventNameEdit.getText().toString();
            String eventDateTime = eventDateTimeEdit.getText().toString();
            String eventPrice = eventPriceEdit.getText().toString();
            String eventLocation = eventLocationEdit.getText().toString();
            String eventDescription = eventDescriptionEdit.getText().toString();
            if (
                    eventName.isEmpty() ||
                            eventDateTime.isEmpty() ||
                            eventPrice.isEmpty() ||
                            eventLocation.isEmpty() ||
                            eventDescription.isEmpty())
            {
                // Toast alerts organiser that event is missing feilds
                Toast.makeText(getContext(), "Required fields missing!", Toast.LENGTH_SHORT).show();
            }
            else {
                updatedEvent.setEventTitle(eventName);
                updatedEvent.setLocation(eventLocation);
                updatedEvent.setDate(eventDateTime);
                updatedEvent.setDescription(eventDescription);
                updatedEvent.setCost(eventPrice);
                handler.updateEvent(updatedEvent, this);
            }
        });

        // image picking section of onview created
        eventImageBtn = view.findViewById(R.id.o_editEvent_addImage);

        // this code was taken from Jens upload avatar profile code
        eventImageBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pickMedia.launch(new PickVisualMediaRequest.Builder()
                        .setMediaType(ActivityResultContracts.PickVisualMedia.ImageOnly.INSTANCE)
                        .build());
            }
        });

        // code for back button
        FloatingActionButton backButton = view.findViewById(R.id.o_editEvent_backBtn);
        backButton.setOnClickListener(view1 -> {
            Log.d("EventFragment", "Back button pressed");
            Bundle navArgs = new Bundle();
            navArgs.putString("eventID", updatedEvent.getEventID());
            navArgs.putParcelable("signedInAccount", signedInAccount);
            navController.navigate(R.id.organiser_eventFragment, navArgs);
        });

        handler.getEventByID(updatedEvent.getEventID(), this);
    }

    /**
     * Allows the user to select an image for the event
     * @author griffin
     */
    private final ActivityResultLauncher<PickVisualMediaRequest> pickMedia =
            registerForActivityResult(new ActivityResultContracts.PickVisualMedia(), uri -> {
                if (uri != null) {
                    // center cropped can be changed if we want to scale the picture differently
                    Toast.makeText(getContext(), "Image uploading to database", Toast.LENGTH_LONG).show();
                    ImageHandler.uploadEventImageToFirebase(uri, this);
                    editEventButton.setClickable(false);
                    // set update event clickable till image is uploaded
                }
                else{
                    // sets image path to null if no image is selected
                    updatedEvent.setEventPoster(null);
                }
            });


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
        if (queryID == 1){ // get event by ID
            if (flags == DBOnCompleteFlags.SINGLE_DOCUMENT.value){
                updatedEvent = docs.get(0);

                // updating the event details
                eventNameEdit.setText(updatedEvent.getEventTitle());
                eventDateTimeEdit.setText(updatedEvent.getDate());
                eventPriceEdit.setText((updatedEvent.getCost()));
                eventLocationEdit.setText(updatedEvent.getLocation());
                eventDescriptionEdit.setText(updatedEvent.getDescription());

                // updating the image details
                ImageHandler.getImageDownloadUrl(updatedEvent.getEventPoster(), new StorageOnCompleteListener<Uri>() {
                    @Override
                    public void OnCompleteStorage(@NonNull ArrayList<Uri> docs, int queryID, int flags) {
                        // Author of this code segment is James
                        if (flags == DBOnCompleteFlags.SUCCESS.value) {
                            // Get the image and format it
                            Uri downloadUri = docs.get(0);
                            Picasso.get()
                                    .load(downloadUri)
                                    .resize(eventImageBtn.getWidth(), eventImageBtn.getHeight())
                                    .centerCrop()
                                    .into(eventImageBtn);
                        } else {
                            // Event had no image, so it will stay as default image
                            Log.d("DB", String.format("No image found, setting default"));
                        }
                    }
                });
            }
            else {
                Log.e("Edit Event", "Unable to pull the event from database");
                Toast.makeText(getContext(), "Database Error", Toast.LENGTH_LONG).show();
            }
        }
        else if (queryID == 2){ // update event
            if (flags == DBOnCompleteFlags.SUCCESS.value) {
                navController.popBackStack();
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
        editEventButton.setClickable(true);
        if (queryID == 0){ // post image and link to event
            if (flags == DBOnCompleteFlags.SUCCESS.value) {
                Log.i("Post Event", "Image upload successful");
                Toast.makeText(getContext(), "Image uploaded", Toast.LENGTH_LONG).show();
                Image newImage = docs.get(0);
                Picasso.get()
                        .load(newImage.getUrl())
                        .resize(eventImageBtn.getWidth(), eventImageBtn.getHeight())
                        .centerCrop()
                        .into(eventImageBtn);
                updatedEvent.setEventPoster(newImage.getUrl()); // set the image path from the URL
            }
            else {
                Log.e("Post Event", "Image upload unsuccessful");
                Toast.makeText(getContext(), "Image failed to upload", Toast.LENGTH_LONG).show();
            }
        }
    }
}
