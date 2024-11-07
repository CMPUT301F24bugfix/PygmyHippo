package com.example.pygmyhippo.organizer;

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
     * Creates the view
     * @author none
     * @param inflater not sure
     * @param container not sure
     * @param savedInstanceState  not sure
     * @return root not sure
     */
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        binding = OrganiserFragmentMyeventsBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        if (getArguments() != null) {
            signedInAccount = MyEventsFragmentArgs.fromBundle(getArguments()).getSignedInAccount();
        }

        //FIXME: add a button to navigate to entrant list to see the draft
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