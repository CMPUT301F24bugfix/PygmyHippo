package com.example.pygmyhippo.user;

/*
This fragment gives the ability to scan QR codes
Purposes:
    - Lets the user scan QR codes so that the can view event details and join if they want
Issues:
    - Nothing to stop the navigation if the QR code doesn't actually have the encoded event ID
 */

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.example.pygmyhippo.R;
import com.example.pygmyhippo.common.Account;
import com.example.pygmyhippo.common.Event;
import com.example.pygmyhippo.database.DBOnCompleteFlags;
import com.example.pygmyhippo.database.DBOnCompleteListener;
import com.example.pygmyhippo.database.EventDB;
import com.example.pygmyhippo.databinding.UserFragmentQrBinding;
import com.journeyapps.barcodescanner.BarcodeCallback;
import com.journeyapps.barcodescanner.DecoratedBarcodeView;

import java.util.ArrayList;

/**
 * This fragment will hold the QR scanner
 * @author Katharine
 * @version 2.0
 * No returns and no parameters
 */
public class QRFragment extends Fragment implements DBOnCompleteListener<Event> {

    private UserFragmentQrBinding binding;
    private NavController navController;
    private DecoratedBarcodeView QRScannerView;
    private static final int CAMERA_PERMISSION_REQUEST_CODE = 0;

    private String eventID;
    private Account signedInAccount;

    private EventDB DBhandler;

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

        binding = UserFragmentQrBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        DBhandler = new EventDB();

        // Get the current account
        signedInAccount = QRFragmentArgs.fromBundle(getArguments()).getSignedInAccount();

        return root;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        navController = Navigation.findNavController(view);

        // Get the scanner and ask user for permission to use camera
        QRScannerView = view.findViewById(R.id.u_QRScanner);
        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.CAMERA)
                == PackageManager.PERMISSION_GRANTED) {
            // Let the user scan
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

    /**
     * Lets the user actually scan
     */
    private void startSingleScan() {
        QRScannerView.decodeSingle(callback);
    }

    /**
     * When the QR is successfully scanned, will read an event ID and take that to the event fragment
     * So that that fragment can get populated with those details
     */
    private final BarcodeCallback callback = result -> {
        if (result != null && result.getText() != null) {
            eventID = result.getText();
            DBhandler.getEventByID(eventID, this);
        }
    };

    @Override
    public void onResume() {
        super.onResume();
        if (QRScannerView != null && ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.CAMERA)
                == PackageManager.PERMISSION_GRANTED) {
            QRScannerView.resume();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if (QRScannerView != null) {
            QRScannerView.pause();
        }
    }

    @Override
    public void OnCompleteDB(@NonNull ArrayList<Event> docs, int queryID, int flags) {
        if (queryID == 1) {
            if (flags == DBOnCompleteFlags.SINGLE_DOCUMENT.value) {
                Bundle navArgs = new Bundle();
                navArgs.putParcelable("signedInAccount", signedInAccount);
                navArgs.putString("eventID", eventID);
                navController.navigate(R.id.u_eventFragment, navArgs);
            } else {
                Toast badEvent = Toast.makeText(getActivity(), "No event is associated with this QR Code", Toast.LENGTH_SHORT);
                badEvent.show();
                startSingleScan();
            }
        }
    }
    

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}