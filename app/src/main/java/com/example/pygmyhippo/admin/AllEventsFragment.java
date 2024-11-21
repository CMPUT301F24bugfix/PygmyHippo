package com.example.pygmyhippo.admin;
/*
This class displays the list of all events
Purposes:
    - Allows the admin to browse all events
    - Allows the admin to select an event the want to view and potentially delete
Issues:
    - No spinner functionality yet
 */


import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.pygmyhippo.R;
import com.example.pygmyhippo.common.Account;
import com.example.pygmyhippo.common.AppNavController;
import com.example.pygmyhippo.common.Event;
import com.example.pygmyhippo.common.RecyclerClickListener;
import com.example.pygmyhippo.database.DBOnCompleteFlags;
import com.example.pygmyhippo.database.DBOnCompleteListener;
import com.example.pygmyhippo.database.EventDB;
import com.example.pygmyhippo.databinding.AdminFragmentAllListBinding;

import java.util.ArrayList;

/**
 * Admin fragment for displaying all events.
 *
 * Events are displayed in a list (RecyclerView). Events can be sorted by various attributes in
 * different orders. When events are clicked, the fragment should navigate to that particular
 * event's profile page with admin permissions.
 */
public class AllEventsFragment extends Fragment implements RecyclerClickListener, DBOnCompleteListener<Event> {
    private AdminFragmentAllListBinding binding;

    private ArrayList<Event> allEvents;
    private AllEventsAdapter adapter;
    private EventDB handler;

    private AppNavController navController;
    private Account signedInAccount;
    private boolean useNavigation, useFirebase;


    @Override
    public void onItemClick(int position) {
        Log.i("User", String.format("Admin clicked item at position %d", position));

        // An event was clicked, so send that info to the EventFragment for the Admin to view (and navigate there)
        Bundle navArgs = new Bundle();
        navArgs.putParcelable("signedInAccount", signedInAccount);
        navArgs.putString("eventID", allEvents.get(position).getEventID());
        navArgs.putBoolean("isAdmin", true);
        navArgs.putBoolean("useNavigation", true);
        navArgs.putBoolean("useFirebase", true);
        navController.navigate(R.id.action_admin_navigation_all_events_to_admin_navigation_event_page, navArgs);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            AllEventsFragmentArgs args = AllEventsFragmentArgs.fromBundle(getArguments());
            signedInAccount = args.getSignedInAccount();
            useNavigation = args.getUseNavigation();
            useFirebase = args.getUseFirebase();
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        binding = AdminFragmentAllListBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        // Get the signed in account that was passed from the fragment that navigated here
        allEvents = new ArrayList<>();

        // Fill the fields of the recycle view
        adapter = new AllEventsAdapter(allEvents, this);
        binding.aAlllistRecycler.setLayoutManager(new LinearLayoutManager(getContext()));
        binding.aAlllistRecycler.setAdapter(adapter);

        // Initialize the 1st spinner
        ArrayAdapter<CharSequence> categoryAdapter = ArrayAdapter.createFromResource(
                this.requireContext(),
                R.array.all_events_category_spinner,
                android.R.layout.simple_spinner_item
        );

        categoryAdapter.setDropDownViewResource(android.R.layout.simple_spinner_item);
        binding.aAlllistCategorySpinner.setAdapter(categoryAdapter);

        // Initialize the second spinner
        ArrayAdapter<CharSequence> orderAdapter = ArrayAdapter.createFromResource(
                this.requireContext(),
                R.array.order_spinner,
                android.R.layout.simple_spinner_item
        );

        orderAdapter.setDropDownViewResource(android.R.layout.simple_spinner_item);

        binding.aAlllistOrderSpinner.setAdapter(orderAdapter);

        binding.aAlllistTitleText.setText(R.string.all_events_title);

        return root;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        navController = new AppNavController(useNavigation, Navigation.findNavController(view));

        // Initialize the handler and get all the events
        handler = new EventDB(useFirebase);
        handler.getEvents(1000, this);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    @Override
    public void OnCompleteDB(@NonNull ArrayList<Event> docs, int queryID, int flags) {
        if (queryID == 6) {
            if (flags == DBOnCompleteFlags.SUCCESS.value) {
                // Add each retrieved event to the list
                docs.forEach(doc -> {
                    allEvents.add(doc);
                    adapter.notifyItemInserted(allEvents.size() - 1);
                });
            }
        }
    }
}
