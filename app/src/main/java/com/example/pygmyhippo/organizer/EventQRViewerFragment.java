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
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.example.pygmyhippo.R;
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
    private String myEventString;

    /**
     * Creates the view
     * @author Griffin
     * @param inflater not sure
     * @param container not sure
     * @param savedInstanceState  not surre
     * @return root not sure
     */
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.o_event_qrcode_view, container, false);

        // code for button was copies from Koris work in viewEntantsFragments
        backButton = view.findViewById(R.id.o_eventqr_backBtn);
        backButton.setOnClickListener(view1 -> {
            Navigation.findNavController(view1).popBackStack();
        });

        // set up for the details button
//        detailsButton = view.findViewById(R.id.o_eventqr_detailsBtn);
//        backButton.setOnClickListener(view1 -> {
//            // TODO: connect to navigating to single event
//            //Navigation.findNavController(view1).navigate(R.id.action_view_eventqr_fragment_to_organiser_postEvent_page);
//        });

        super.onCreate(savedInstanceState);
        Bundle bundle = this.getArguments();
        myEventString = bundle.getString("my_event_id");

        if (getArguments() != null && !myEventString.isEmpty()) {
            //TODO: connect to the database and update the status
            QRCodeImage = view.findViewById(R.id.o_eventqr_view);
            Bitmap bitmap = QRCode.from(myEventString)
                    .withSize(500, 500)
                    .withHint(EncodeHintType.MARGIN, "1")
                    .bitmap();
            QRCodeImage.setImageBitmap(bitmap);
        }

        return view;
    }
}
