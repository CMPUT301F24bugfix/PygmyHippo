package com.example.pygmyhippo.organizer;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.example.pygmyhippo.R;
import com.example.pygmyhippo.databinding.OrganiserFragmentMyeventsBinding;

/**
 * This fragment will hold My events
 * @author none
 * @version none
 * No returns and no parameters
 */
public class MyEventsFragment extends Fragment {

    private OrganiserFragmentMyeventsBinding binding;

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

        final TextView textView = binding.textHome;


        //FIXME: add a button to navigate to entrant list
        Button button = binding.buttonViewEntrants;
        button.setOnClickListener(view -> {
            Navigation.findNavController(view).navigate(R.id.action_navigation_home_to_oViewEntrantsFragment);
        });

        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}