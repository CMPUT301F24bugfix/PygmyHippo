package com.example.pygmyhippo.organizer;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import com.example.pygmyhippo.R;
import com.example.pygmyhippo.common.Event;
import com.example.pygmyhippo.databinding.OrganiserPostEventBinding;

import java.util.ArrayList;

/**
 * This fragment will hold the post event
 * TODO:
 *  - attach event to database
 *  - navigation to qr code screen after event post
 *  - implement changing the image to a new image (this would require firestore connection)
 *  - hide the navigation when a keyboard pops up
 *  Thinking about:
 *   - should the current progress of the event reset if the organiser switches screen
 *   - a button called "Clear Fields" at the top to clear event feild
 *   - really similar idea can be applied to editing an event
 * @author Griffin
 * @version 1.1
 * No returns and no parameters
 */
public class PostEventFragment extends Fragment {
    /* The future fragment for the QR Code Scanner */

    private OrganiserPostEventBinding binding;

    private EditText eventNameEdit, eventDateTimeEdit, eventPriceEdit, eventLocationEdit, eventDescriptionEdit, eventLimitEdit, eventWinnersEdit;
    private CheckBox eventGeolocation;

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

        return root;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        eventNameEdit = view.findViewById(R.id.o_postEvent_name_edit);
        eventDateTimeEdit = view.findViewById(R.id.o_postEvent_dataTime_edit);
        eventPriceEdit = view.findViewById(R.id.o_postEvent_price_edit);
        eventLocationEdit = view.findViewById(R.id.o_postEvent_location_edit);
        eventDescriptionEdit = view.findViewById(R.id.o_postEvent_description_edit);
        eventLimitEdit = view.findViewById(R.id.o_postEvent_limit_edit);
        eventWinnersEdit = view.findViewById(R.id.o_postEvent_winners_edit);
        eventGeolocation = view.findViewById(R.id.o_postEvent_geolocation_check);

        Button postEventButton = view.findViewById(R.id.o_postEvent_post_button);
        postEventButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String eventName = eventNameEdit.getText().toString();
                String eventDateTime = eventDateTimeEdit.getText().toString();
                String eventPrice = eventPriceEdit.getText().toString();
                String eventLocation = eventLocationEdit.getText().toString();
                String eventDescription = eventDescriptionEdit.getText().toString();
                String eventLimit = eventLimitEdit.getText().toString();
                String eventWinners = eventWinnersEdit.getText().toString();
                Boolean eventGeolocaion = eventGeolocation.isChecked();
                if (eventName.isEmpty() || eventDateTime.isEmpty() || eventPrice.isEmpty() ||
                    eventLocation.isEmpty() || eventDescription.isEmpty() || eventWinners.isEmpty()) {
                    Toast.makeText(getContext(), "Required fields missing!", Toast.LENGTH_SHORT).show();
                } else {
                    Event myEvent = new Event();
                    myEvent.setOrganiserID("currentUserID"); // TODO: get the current organiser
                    myEvent.setLocation(eventLocation);
                    myEvent.setDate(eventDateTime);
                    myEvent.setDescription(eventDescription);
                    myEvent.setCost(eventPrice);
                    myEvent.setEventLimitCount(eventLimit.isEmpty() ? -1 : Integer.valueOf(eventLimit));
                    myEvent.setEventWinnersCount(Integer.valueOf(eventWinners));
                    myEvent.setEntrants(new ArrayList<>()); // no entrants of a newly created event
                    myEvent.setGeolocation(eventGeolocaion);
                    myEvent.setEventStatus(Event.EventStatus.ongoing); // default is ongoing
                    // TODO: connect to database
                    Toast.makeText(getContext(), "Event Created", Toast.LENGTH_SHORT).show();
                    clearEvent(); // this would be a navigation to a new view qr code
                }
            }
        });
    }

    /**
     * Clears the event in the poster
     * @author griffin
     */
    public void clearEvent(){
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
}