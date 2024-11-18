package com.example.pygmyhippo.user;

/*
This Fragment will display a chosen event from a user's event list
Will be used by users and admins
Purposes:
        - If the user is chosen from the waitlist, let the user accept the invitation to register/sign up when chosen to participate in an event
        - If the user is chosen from the waitlist, let the user decline an invitation when chosen to participate in an event
        - If the user is not chosen from the waitlist, let the user have another chance to be chosen for the waitlist
Contributors: Katharine, Kori
 */

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.fragment.app.Fragment;

import com.example.pygmyhippo.R;
import com.example.pygmyhippo.common.Account;
import com.example.pygmyhippo.common.Entrant;
import com.example.pygmyhippo.common.Event;
import com.example.pygmyhippo.database.DBOnCompleteFlags;
import com.example.pygmyhippo.database.DBOnCompleteListener;
import com.example.pygmyhippo.database.ImageStorage;
import com.example.pygmyhippo.database.StorageOnCompleteListener;
import com.example.pygmyhippo.databinding.UserFragmentEventBinding;
import com.example.pygmyhippo.organizer.EventFragmentArgs;

import java.util.ArrayList;

public class ViewMyEventFragment extends Fragment implements DBOnCompleteListener<Event>{
    private UserFragmentEventBinding binding;
    private NavController navController;

    private Event event;

    // this is the current user who is trying to join the event
    private Entrant entrant;
    private ArrayList<Entrant> entrants;
    private Account signedInAccount;
    private String eventID;

    private EventDB DBhandler;
    private ImageStorage imageHandler;

    private TextView eventNameView, eventDateView, eventTimeView, eventOrganizerView,
            eventLocationView, eventCostView, eventAboutDescriptionView;
    private Button registerButton, deleteEventButton, deleteQRCodeButton;
    private ConstraintLayout adminConstraint;
    private ImageView eventPoster;


    // populate single event page with hardcoded event information
    public Event hardcodeEvent() {
        entrants = new ArrayList<>();

        return event = new Event(
                "Hippo Party",
                "1",
                "The Hippopotamus Society",
                entrants,
                "The Swamp",
                "2024-10-31",
                "4:00 PM MST - 4:00 AM MST",
                "Love hippos and a party? Love a party! Join a party!",
                "$150.00",
                "hippoparty.png",
                Event.EventStatus.ongoing,
                true
        );
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        navController = Navigation.findNavController(view);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = UserFragmentEventBinding.inflate(inflater, container, false);
        View view = binding.getRoot();

        if (getArguments() != null) {
            signedInAccount = com.example.pygmyhippo.organizer.EventFragmentArgs.fromBundle(getArguments()).getSignedInAccount();
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

        return view;
    }


    @Override
    public void OnCompleteDB(@NonNull ArrayList<Event> docs, int queryID, int flags) {

    }
}
