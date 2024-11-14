package com.example.pygmyhippo.organizer;

/*
This file is used as the array adapter for the my events page.
Purposes:
    - To format the list entries of events in the list
Issues:
    - None
 */

import android.content.Context;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.pygmyhippo.R;
import com.example.pygmyhippo.common.Account;
import com.example.pygmyhippo.common.Entrant;
import com.example.pygmyhippo.common.Event;
import com.example.pygmyhippo.database.DBOnCompleteFlags;
import com.example.pygmyhippo.database.DBOnCompleteListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

/**
 * This class acts as the adapter for event list entries
 * @author Kori
 */
public class EventArrayAdapter extends ArrayAdapter<Event> {
    private EventDB dbHandler;
    private Event event;

    public EventArrayAdapter(Context context, ArrayList<Event> eventListData) {
        super(context, 0, eventListData);
    }

    /**
     * This Method will customize the event list depending on their profil value
     * @param position position in the array
     * @param convertView
     * @param parent
     * @return the view to display one entrant
     */
    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        /*
        This method overrides the getView method so that we can customize it to display multiple textViews
         */
        View view;

        if (convertView == null) {
            view = LayoutInflater.from(getContext()).inflate(R.layout.organiser_my_event_content, parent, false);
        } else {
            view = convertView;
        }

        dbHandler = new EventDB();
        event = getItem(position);
        Log.d("DB", String.format("Finding %s Event", event.getEventID()));

        // Get all the views from the entrants_content in our xml file
        TextView eventName = view.findViewById(R.id.o_event_title_view);
        TextView eventDate = view.findViewById(R.id.o_event_date_view);
        TextView eventStatus = view.findViewById(R.id.o_event_status_txt_view);
        TextView eventLocation = view.findViewById(R.id.o_event_location_txt_view);
        ImageView eventPoster = view.findViewById(R.id.o_event_image_view);

        // Set all the fields
        eventName.setText(event.getEventTitle());
        eventDate.setText(event.getDate());
        eventLocation.setText(event.getLocation());
        if (event.getEventStatus().value.equals("ongoing")) {
            // Check the status of the event to customize the text
            eventStatus.setText("Waitlist Stauts: OPEN");
        } else {
            eventStatus.setText("Waitlist Status: CLOSED");
        }

        // Get the event poster from firebase
        dbHandler.getImageDownloadUrl(event.getEventPoster(), new DBOnCompleteListener<Uri>() {
            @Override
            public void OnComplete(@NonNull ArrayList<Uri> docs, int queryID, int flags) {
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

        return view;
    }
}
