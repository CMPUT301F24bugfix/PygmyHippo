package com.example.pygmyhippo.organizer;

/*
This class will display the list of an organiser's events
Purposes:
    - Give the organiser ability to check their event details
    - Basically gives them access to update event details
Issues:
    - This has not been implemented yet. We just have a button that shows what the navigation will look like once this is done
 */

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.example.pygmyhippo.R;
import com.example.pygmyhippo.common.Account;
import com.example.pygmyhippo.databinding.OrganiserFragmentMyeventsBinding;

/**
 * This fragment will hold My events
 * @author none
 * @version none
 * No returns and no parameters
 */
public class MyEventsFragment extends Fragment {

    private OrganiserFragmentMyeventsBinding binding;
    private NavController navController;
    private Account signedInAccount;


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        navController = Navigation.findNavController(view);
    }


    /**
     *
     * @param inflater The LayoutInflater object that can be used to inflate
     * any views in the fragment,
     * @param container If non-null, this is the parent view that the fragment's
     * UI should be attached to.  The fragment should not add the view itself,
     * but this can be used to generate the LayoutParams of the view.
     * @param savedInstanceState If non-null, this fragment is being re-constructed
     * from a previous saved state as given here.
     *
     * @return the view
     */
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        binding = OrganiserFragmentMyeventsBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        if (getArguments() != null) {
            signedInAccount = MyEventsFragmentArgs.fromBundle(getArguments()).getSignedInAccount();
        }

        //FIXME: added a button to navigate to entrant list to see the draft, will delete once list is implemented
        Button button = binding.buttonSampleEvent;
        button.setOnClickListener(view -> {
            Bundle navArgs = new Bundle();
            navArgs.putParcelable("signedInAccount", signedInAccount);
            navArgs.putString("eventID", "37Pm3bM0Z0xBjwWLGTqD"); // TODO remove hardcoded eventID.
            navController.navigate(R.id.organiser_eventFragment, navArgs);
        });

        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}