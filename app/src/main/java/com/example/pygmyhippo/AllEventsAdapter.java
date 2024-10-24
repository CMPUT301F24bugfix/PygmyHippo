package com.example.pygmyhippo;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;

import java.util.ArrayList;

public class AllEventsAdapter extends BaseRecyclerAdapter<Event, AllEventsAdapter.EventViewHolder> {
    public static class EventViewHolder extends BaseViewHolder<Event> {
        TextView nameTextView, occurrenceTextView, locationTextView, waitlistStatusTextView;

        public EventViewHolder(@NonNull View itemView) {
            super(itemView);
            nameTextView = itemView.findViewById(R.id.a_alllist_event_name_text);
            occurrenceTextView = itemView.findViewById(R.id.a_alllist_event_occurence_text);
            locationTextView = itemView.findViewById(R.id.a_alllist_event_location_text);
            waitlistStatusTextView = itemView.findViewById(R.id.a_alllist_event_waitlist_status_text);
        }

        @Override
        public void setViews(Event event) {
            nameTextView.setText(event.getTitle());
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
                .inflate(R.layout.all_list_event_item, parent, false);
        AllEventsAdapter.EventViewHolder viewHolder = new EventViewHolder(view);

        view.setOnClickListener(v -> {
            listener.onItemClick(viewHolder.getAdapterPosition());
        });

        return viewHolder;
    }
}
