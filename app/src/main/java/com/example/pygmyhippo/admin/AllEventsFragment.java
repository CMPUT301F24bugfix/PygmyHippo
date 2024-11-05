package com.example.pygmyhippo.admin;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.pygmyhippo.R;
import com.example.pygmyhippo.common.Account;
import com.example.pygmyhippo.common.Event;
import com.example.pygmyhippo.common.RecyclerClickListener;
import com.example.pygmyhippo.database.DBOnCompleteFlags;
import com.example.pygmyhippo.database.DBOnCompleteListener;
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
    AdminFragmentAllListBinding binding;
    ArrayList<Event> allEvents;
    AllEventsAdapter adapter;
    AllEventsDB handler;
    Account signedInAccount;

    @Override
    public void onItemClick(int position) {
        Log.i("User", String.format("Admin clicked item at position %d", position));
        assert getActivity() != null;
        NavController navController = Navigation.findNavController(getActivity(), R.id.nav_host_fragment_activity_main);

        Bundle navArgs = new Bundle();
        navArgs.putParcelable("signedInAccount", signedInAccount);
        navArgs.putString("eventID", allEvents.get(position).getEventID());
        navController.navigate(R.id.action_admin_navigation_all_events_to_admin_navigation_event_page, navArgs);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        binding = AdminFragmentAllListBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        signedInAccount = AllEventsFragmentArgs.fromBundle(getArguments()).getSignedInAccount();
        allEvents = new ArrayList<>();

        handler = new AllEventsDB();
        handler.getEvents(1000, this);

        adapter = new AllEventsAdapter(allEvents, this);
        binding.aAlllistRecycler.setLayoutManager(new LinearLayoutManager(getContext()));
        binding.aAlllistRecycler.setAdapter(adapter);

        ArrayAdapter<CharSequence> categoryAdapter = ArrayAdapter.createFromResource(
                this.requireContext(),
                R.array.all_events_category_spinner,
                android.R.layout.simple_spinner_item
        );

        categoryAdapter.setDropDownViewResource(android.R.layout.simple_spinner_item);

        binding.aAlllistCategorySpinner.setAdapter(categoryAdapter);

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
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    @Override
    public void OnComplete(@NonNull ArrayList<Event> docs, int queryID, int flags) {
        if (queryID == 0) {
            if (flags == DBOnCompleteFlags.SUCCESS.value) {
                docs.forEach(doc -> {
                    allEvents.add(doc);
                    adapter.notifyItemInserted(allEvents.size() - 1);
                });
            }
        }
    }
}
