package com.example.pygmyhippo.user;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.example.pygmyhippo.R;
import com.example.pygmyhippo.databinding.UserFragmentQrBinding;
/**
 * This fragment will hold the QR scanner
 * @author Katharine
 * @version 1.0
 * No returns and no parameters
 */
public class QRFragment extends Fragment {

    private UserFragmentQrBinding binding;

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

        binding = UserFragmentQrBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        return root;
    }

    // qr code scanner to event button
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        Button scanQRButton = view.findViewById(R.id.u_scanQRButtonView);
        scanQRButton.setOnClickListener(view1 -> {
            Navigation.findNavController(view1).navigate(R.id.action_scanQRcodeFragment_to_eventFragment);
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}