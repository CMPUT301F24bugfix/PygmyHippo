package com.example.pygmyhippo.user;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.pygmyhippo.databinding.UserFragmentMyeventsBinding;

/**
 * This fragment will hold My events
 * @author none
 * @version none
 * No returns and no parameters
 */
public class MyEventsFragment extends Fragment {

    private UserFragmentMyeventsBinding binding;

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


        binding = UserFragmentMyeventsBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        final TextView textView = binding.textHome;

        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}