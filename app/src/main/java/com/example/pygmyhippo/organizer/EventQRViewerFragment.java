package com.example.pygmyhippo.organizer;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.example.pygmyhippo.R;
import com.example.pygmyhippo.common.Account;
import com.google.zxing.EncodeHintType;

import net.glxn.qrgen.android.QRCode;

/**
 * This fragment will display the qrcode
 * TODO:
 *  - Once connected to the database this will need to be reworked to fetch details
 *      about the event from the database
 * @author Griffin
 * @version 1.0
 * No returns and no parameters
 */
public class EventQRViewerFragment extends Fragment {

    private ImageButton backButton;
    private Button detailsButton;
    private ImageView QRCodeImage;

    private NavController navController;
    private Account signedInAccount;
    private String eventID;


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        navController = Navigation.findNavController(view);
    }

    /**
     * Creates the view
     * A bundle of a string with the event id with key: "my_event_id"
     * @author Griffin
     * @param inflater not sure
     * @param container not sure
     * @param savedInstanceState  not surre
     * @return root not sure
     */
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        View view = inflater.inflate(R.layout.organiser_event_qrcode_view, container, false);

        if (getArguments() != null) {
            signedInAccount = EventQRViewerFragmentArgs.fromBundle(getArguments()).getSignedInAccount();
            eventID = EventQRViewerFragmentArgs.fromBundle(getArguments()).getEventID();
        }

        // code for button was copies from Koris work in viewEntantsFragments
        backButton = view.findViewById(R.id.o_eventqr_backBtn);
        backButton.setOnClickListener(view1 -> {
            navController.popBackStack();
        });

        // set up for the details button
        detailsButton = view.findViewById(R.id.o_eventqr_detailsBtn);
        detailsButton.setOnClickListener(view1 -> {
            Bundle navArgs = new Bundle();
            navArgs.putParcelable("signedInAccount", signedInAccount);
            navArgs.putString("eventID", eventID);
            navController.navigate(R.id.organiser_eventFragment, navArgs);
        });

        if (eventID != null) {
            //TODO: connect to the database and update the status
            QRCodeImage = view.findViewById(R.id.o_eventqr_view);
            Bitmap bitmap = QRCode.from(eventID)
                    .withSize(500, 500)
                    .withHint(EncodeHintType.MARGIN, "1")
                    .bitmap();
            QRCodeImage.setImageBitmap(bitmap);
        }

        return view;
    }
}