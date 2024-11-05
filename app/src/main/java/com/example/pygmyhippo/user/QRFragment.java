package com.example.pygmyhippo.user;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.example.pygmyhippo.R;
import com.example.pygmyhippo.databinding.UserFragmentQrBinding;
import com.journeyapps.barcodescanner.BarcodeCallback;
import com.journeyapps.barcodescanner.BarcodeResult;
import com.journeyapps.barcodescanner.DecoratedBarcodeView;

/**
 * This fragment will hold the QR scanner
 * @author Katharine
 * @version 1.0
 * No returns and no parameters
 */
public class QRFragment extends Fragment {

    private UserFragmentQrBinding binding;
    private DecoratedBarcodeView barcodeView;
    private static final int CAMERA_PERMISSION_REQUEST_CODE = 0;

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

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        barcodeView = view.findViewById(R.id.u_barcodeScanner);
        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.CAMERA)
                == PackageManager.PERMISSION_GRANTED) {
            startSingleScan();
        } else {
            // request for camera permission
            // TODO: with database, can check past permissions on profile
            // maybe set initial to null, check if null, then set, can change in profile
            ActivityCompat.requestPermissions(requireActivity(),
                    new String[]{Manifest.permission.CAMERA},
                    CAMERA_PERMISSION_REQUEST_CODE);
        }
    }

    private void startSingleScan() {
        barcodeView.decodeSingle(callback);
    }

    private final BarcodeCallback callback = result -> {
        if (result != null && result.getText() != null) {
            String eventID = result.getText();
            Toast testShowBarcode = Toast.makeText(getActivity(), eventID, Toast.LENGTH_LONG);
            testShowBarcode.show();

            // TODO: if evenID doesnt correpond to anything in database, restart using startSingleScan()

            Navigation.findNavController(requireView()).navigate(R.id.action_scanQRcodeFragment_to_eventFragment);
        }
    };

    @Override
    public void onResume() {
        super.onResume();
        if (barcodeView != null && ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.CAMERA)
                == PackageManager.PERMISSION_GRANTED) {
            barcodeView.resume();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if (barcodeView != null) {
            barcodeView.pause();
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}