package com.example.pygmyhippo.organizer;

/*
 * This fragment will display the qr code
 * Purposes:
 *      - Provides the organiser with their generated QR code
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
import androidx.navigation.Navigation;

import com.example.pygmyhippo.R;
import com.example.pygmyhippo.common.Account;
import com.example.pygmyhippo.common.AppNavController;
import com.example.pygmyhippo.common.Event;
import com.example.pygmyhippo.database.DBOnCompleteFlags;
import com.example.pygmyhippo.database.DBOnCompleteListener;
import com.example.pygmyhippo.database.EventDB;
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

    private AppNavController navController;
    private Account signedInAccount;
    private String eventID;
    private boolean useNavigation, useFirebase;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            EventQRViewerFragmentArgs args = EventQRViewerFragmentArgs.fromBundle(getArguments());
            signedInAccount = args.getSignedInAccount();
            eventID = args.getEventID();
            useNavigation = args.getUseNavigation();
            useFirebase = args.getUseFirebase();
            myEventIDString = args.getEventID();
        }
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        navController = new AppNavController(useNavigation, Navigation.findNavController(view));

        handler = new EventDB(useFirebase);
        handler.getEventByID(myEventIDString, this);
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
        eventTitle = view.findViewById(R.id.o_eventqr_eventTitle);
        eventDate = view.findViewById(R.id.o_eventqr_eventDate);
        QRCodeImage = view.findViewById(R.id.o_eventqr_view);

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

        return view;
    }

    /**
     * Updates the event details on screen
     */
    private void setScreenDetails(){
        Bitmap bitmap = QRCode.from(myevent.getEventID())
                .withSize(500, 500)
                .withHint(EncodeHintType.MARGIN, "1")
                .bitmap();
        QRCodeImage.setImageBitmap(bitmap);
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
    public void OnCompleteDB(@NonNull ArrayList<Event> docs, int queryID, int flags) {
        if (queryID == 1) {
            if (flags == DBOnCompleteFlags.SINGLE_DOCUMENT.value) {
                myevent = docs.get(0);
                if(myevent.isValidHashcode()){
                    Log.i("QR Viewer", "Valid hashcode, displaying info");
                    setScreenDetails();
                }
                else{
                    Log.e("QR Viewer", "Invalid hashcode");
                    Toast.makeText(getContext(), "Event not valid", Toast.LENGTH_LONG).show();
                }
            } else {
                // expect 1 document, else there must be an error
                Log.e("QR Viewer", "Database Error");
                Toast.makeText(getContext(), "Event not valid", Toast.LENGTH_LONG).show();
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
