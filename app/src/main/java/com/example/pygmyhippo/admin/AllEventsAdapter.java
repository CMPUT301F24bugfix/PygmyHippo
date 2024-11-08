package com.example.pygmyhippo.admin;

/*
This class acts as the adapter for the AllEventsFragment
Purposes:
    - To format how the events will be displayed in a list
    - For the event list the admin should be able to browse
Issues:
    - None
 */

import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.example.pygmyhippo.common.Event;
import com.example.pygmyhippo.common.RecyclerClickListener;
import android.view.View;

import com.example.pygmyhippo.common.BaseRecyclerAdapter;
import com.example.pygmyhippo.common.BaseViewHolder;
import com.example.pygmyhippo.R;

import java.util.ArrayList;

/**
 * Adapter for RecyclerView in AllEventsFragment.
 * @author James
 */
public class AllEventsAdapter extends BaseRecyclerAdapter<Event, AllEventsAdapter.EventViewHolder> {
    /**
     * This class is the child of the base, and holds event views
     * @author James
     */
    public static class EventViewHolder extends BaseViewHolder<Event> {
        TextView nameTextView, occurrenceTextView, locationTextView, waitlistStatusTextView;

        public EventViewHolder(@NonNull View itemView) {
            super(itemView);
            nameTextView = itemView.findViewById(R.id.a_alllist_event_name_text);
            occurrenceTextView = itemView.findViewById(R.id.a_alllist_event_occurence_text);
            locationTextView = itemView.findViewById(R.id.a_alllist_event_location_text);
            waitlistStatusTextView = itemView.findViewById(R.id.a_alllist_event_waitlist_status_text);
        }

        /**
         * This method initializes the fields of the list element
         * @param event What we populate the fields with
         */
        @Override
        public void setViews(Event event) {
            nameTextView.setText(event.getEventTitle());
            occurrenceTextView.setText(""); // TODO: figure out how occurrence is stored then add it here.
            locationTextView.setText(event.getLocation());
            waitlistStatusTextView.setText(event.getEventStatus() == Event.EventStatus.ongoing ? "Waitlist Open" : "Waitlist Closed");
        }
    }


    public AllEventsAdapter(ArrayList<Event> dataList, RecyclerClickListener listener) {
        super(dataList, listener);
    }

    @NonNull
    @Override
    public EventViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.admin_all_list_event_item, parent, false);

        // Initialize the holder
        AllEventsAdapter.EventViewHolder viewHolder = new EventViewHolder(view);

        view.setOnClickListener(v -> {
            listener.onItemClick(viewHolder.getAdapterPosition());
        });

        return viewHolder;
    }
}
