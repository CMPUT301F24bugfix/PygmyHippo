package com.example.pygmyhippo.organizer;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.pygmyhippo.databinding.OrganiserPostEventBinding;

/**
 * This fragment will hold the post event
 * @author none
 * @version none
 * No returns and no parameters
 */
public class PostEvent extends Fragment {
    /* The future fragment for the QR Code Scanner */

    private OrganiserPostEventBinding binding;

    /**
     * Creates the view
     * @author none
     * @param inflater not sure
     * @param container not sure
     * @param savedInstanceState  not surre
     * @return root not sure
     */
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        binding = OrganiserPostEventBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}