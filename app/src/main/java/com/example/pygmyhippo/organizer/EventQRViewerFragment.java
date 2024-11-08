package com.example.pygmyhippo.organizer;

/*
 * This fragment will display the qr code
 * Purposes:
 *      - Provides the organiser with their generated QR code
 * TODO:
 *  navigate to event details page
 * */

import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.example.pygmyhippo.R;
import com.example.pygmyhippo.common.Account;
import com.example.pygmyhippo.common.Entrant;
import com.example.pygmyhippo.common.Event;
import com.example.pygmyhippo.database.DBOnCompleteFlags;
import com.example.pygmyhippo.database.DBOnCompleteListener;
import com.google.zxing.EncodeHintType;

import net.glxn.qrgen.android.QRCode;

import java.util.ArrayList;


/**
 * This fragment will display the qrcode
 * @author Griffin
 * @version 1.0
 * No returns and no parameters
 */
public class EventQRViewerFragment extends Fragment implements DBOnCompleteListener<Event> {

    private ImageButton backButton;
    private Button detailsButton;
    private ImageView QRCodeImage;

    private String myEventIDString;
    private EventDB handler;
    private Event myevent;
    private TextView eventTitle, eventDate;

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
     * @param inflater The LayoutInflater object that can be used to inflate
     * any views in the fragment,
     * @param container If non-null, this is the parent view that the fragment's
     * UI should be attached to.  The fragment should not add the view itself,
     * but this can be used to generate the LayoutParams of the view.
     * @param savedInstanceState If non-null, this fragment is being re-constructed
     * from a previous saved state as given here.
     *
     * @return The view
     */
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        View view = inflater.inflate(R.layout.organiser_event_qrcode_view, container, false);
        handler = new EventDB();
        eventTitle = view.findViewById(R.id.o_eventqr_eventTitle);
        eventDate = view.findViewById(R.id.o_eventqr_eventDate);


        if (getArguments() != null) {
            signedInAccount = EventQRViewerFragmentArgs.fromBundle(getArguments()).getSignedInAccount();
            eventID = EventQRViewerFragmentArgs.fromBundle(getArguments()).getEventID();
        }

        // code for button was copied from Kori's work in viewEntantsFragments

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

        Bundle bundle = this.getArguments();
        myEventIDString = bundle.getString("eventID");
        if (getArguments() != null && !myEventIDString.isEmpty()) {
            // searches for the event details
            handler.getEvent(myEventIDString, this);
            QRCodeImage = view.findViewById(R.id.o_eventqr_view);
            Bitmap bitmap = QRCode.from(eventID)
                    .withSize(500, 500)
                    .withHint(EncodeHintType.MARGIN, "1")
                    .bitmap();
            QRCodeImage.setImageBitmap(bitmap);
        }
        return view;
    }

    /**
     * Updates the event details on screen
     */
    private void setScreenDetails(){
        eventDate.setText(myevent.getDate());
        eventTitle.setText(myevent.getEventTitle());
    }

    /**
     * Callback called when view entrant DB queries complete.
     * @param docs - Documents retrieved from DB (if it was a get query).
     * @param queryID - ID of query completed.
     * @param flags - Flags to indicate query status/set how to process query result.
     */
    @Override
    public void OnComplete(@NonNull ArrayList<Event> docs, int queryID, int flags) {
        if (queryID == 1) {
            if (flags == DBOnCompleteFlags.SINGLE_DOCUMENT.value) {
                myevent = docs.get(0);
                setScreenDetails();
            } else {
                // expect 1 document, else there must be an error
                handleDBError();
            }
        }
        else{
            Log.i("DB", String.format("Unknown query ID (%d)", queryID));
            handleDBError();
        }
    }

    /**
     * Displays when database error has occured. Mostly for testing
     * TODO: Think of better exceptions for database errors?
     */
    private void handleDBError () {
        Toast toast = Toast.makeText(getContext(), "DB Error!", Toast.LENGTH_SHORT);
        toast.show();
    }
}
