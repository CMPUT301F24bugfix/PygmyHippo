package com.example.pygmyhippo.user;

/*
This fragment will display the list of events that the user joined the waitlist on
Purposes:
    - Give the user the ability to navigate to an event
    - As such, allows them to check events and leave waitlist if they want
Issues:
    - None
 */

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.example.pygmyhippo.R;
import com.example.pygmyhippo.common.Account;
import com.example.pygmyhippo.common.Event;
import com.example.pygmyhippo.database.DBOnCompleteFlags;
import com.example.pygmyhippo.database.EventDB;
import com.example.pygmyhippo.database.DBOnCompleteListener;
import com.example.pygmyhippo.databinding.UserFragmentMyeventsBinding;
import com.example.pygmyhippo.organizer.MyEventsFragmentArgs;

import java.util.ArrayList;

/**
 * This fragment will hold My events for users
 * @author Kori
 * @version 1.0
 * No returns and no parameters
 */
public class MyEventsFragment extends Fragment implements DBOnCompleteListener<Event> {

    private UserFragmentMyeventsBinding binding;
    private NavController navController;
    private Account signedInAccount;
    private ArrayList<Event> eventDataList;
    private ArrayAdapter<Event> eventArrayAdapter;
    private ListView eventListView;
    private EventDB dbHandler;

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Set up nav controller
        navController = Navigation.findNavController(view);

        // Get the data for the list
        dbHandler = new EventDB();
        dbHandler.getEntrantEvents(signedInAccount.getAccountID(), this);
    }

    /**
     * Creates the view
     * @param inflater The LayoutInflater object that can be used to inflate
     * any views in the fragment,
     * @param container If non-null, this is the parent view that the fragment's
     * UI should be attached to.  The fragment should not add the view itself,
     * but this can be used to generate the LayoutParams of the view.
     * @param savedInstanceState If non-null, this fragment is being re-constructed
     * from a previous saved state as given here.
     *
     * @return View
     */
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        binding = UserFragmentMyeventsBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        if (getArguments() != null) {
            signedInAccount = MyEventsFragmentArgs.fromBundle(getArguments()).getSignedInAccount();
        }

        // Get the list view, and initialize the data list
        eventListView = binding.uEventListview;
        eventDataList = new ArrayList<>();

        // Set up our array adapter and connect it to our listView
        eventArrayAdapter = new com.example.pygmyhippo.organizer.EventArrayAdapter(root.getContext(), eventDataList);
        eventListView.setAdapter(eventArrayAdapter);

        // Set up the onClickListener
        eventListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                // Create a bundle to pass to the next fragment (viewing an event)
                Bundle navArgs = new Bundle();
                navArgs.putParcelable("signedInAccount", signedInAccount);
                navArgs.putString("eventID", eventDataList.get(i).getEventID());
                navController.navigate(R.id.user_to_event_fragment, navArgs);
            }
        });
        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    /**
     * Callback called when event DB queries complete.
     * @param docs - Documents retrieved from DB (if it was a get query).
     * @param queryID - ID of query completed.
     * @param flags - Flags to indicate query status/set how to process query result.
     */
    @Override
    public void OnCompleteDB(@NonNull ArrayList<Event> docs, int queryID, int flags) {
        switch (queryID) {
            case 5: // getEvent()
                if (flags != DBOnCompleteFlags.ERROR.value) {
                    if (flags != DBOnCompleteFlags.NO_DOCUMENTS.value) {
                        // There are events for this organiser, so update the list
                        eventDataList = docs;

                        // Set the new data and notify the adapter
                        eventArrayAdapter.clear();
                        eventArrayAdapter.addAll(eventDataList);
                        eventArrayAdapter.notifyDataSetChanged();
                    }
                } else {
                    // There has been an error in retrieving the data
                    handleDBError();
                }
                break;
            default:
                Log.i("DB", String.format("Unknown query ID (%d)", queryID));
                handleDBError();
        }
    }

    /**
     * This method displays if there was an error in the database
     */
    private void handleDBError() {
        Toast toast = Toast.makeText(getContext(), "DB Error!", Toast.LENGTH_SHORT);
        toast.show();
    }
}