package com.example.pygmyhippo.organizer;

/*
This class is the specialized handler for the event fragment
Purposes:
    - To provide server connectivity to the eventFragment
Issues:
    - None
 */

import android.net.Uri;
import android.util.Log;

import androidx.annotation.NonNull;

import com.example.pygmyhippo.common.Event;
import com.example.pygmyhippo.common.Image;
import com.example.pygmyhippo.database.DBHandler;
import com.example.pygmyhippo.database.DBOnCompleteFlags;
import com.example.pygmyhippo.database.DBOnCompleteListener;
import com.example.pygmyhippo.database.StorageOnCompleteListener;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;

/**
 * Post Event Database handler.
 *
 * Adds and search Event to Firestore when an organiser creates it from PostEventFragment.
 */
public class EventDB extends DBHandler {
    /**
     * This posts event to database
     * @param newEvent The event getting posted to the database
     * @param listener What gets notified when the update is successful or unsuccessful
     * @author James
     */
    public void addEvent(@NonNull Event newEvent, DBOnCompleteListener<Event> listener) {
        DocumentReference docRef = db.collection("Events").document();
        newEvent.setEventID(docRef.getId());
        docRef.set(newEvent)
            .addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    Log.d("DB", String.format("Successfully added Event %s", newEvent.getEventID()));

                    // Add the translated documents to a list to pass to the listener
                    ArrayList<Event> newEventList = new ArrayList<>();
                    newEventList.add(newEvent);
                    listener.OnCompleteDB(newEventList, 0, DBOnCompleteFlags.SUCCESS.value);
                } else {
                    // Notify the listeners of errors
                    Log.d("DB", String.format("Unsuccessfully in adding Event %s", newEvent.getEventID()));
                    listener.OnCompleteDB(new ArrayList<>(), 0, DBOnCompleteFlags.ERROR.value);
                }
            });
    }


    /**
     * This method returns the matching event
     * @param eventID The ID used to retrieve the matching event from the database
     * @param listener What gets notified of results
     * @author Kori
     */
    public void getEvent(String eventID, DBOnCompleteListener<Event> listener) {
        db.collection("Events")
                .whereEqualTo("eventID", eventID)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        QuerySnapshot queryResult = task.getResult();
                        ArrayList<Event> events = new ArrayList<>();
                        queryResult.forEach(doc -> events.add(doc.toObject(Event.class)));
                        if (events.size() == 0) {
                            Log.d("DB", String.format("Found no events with event ID (%s)", eventID));
                            listener.OnCompleteDB(events, 1, DBOnCompleteFlags.NO_DOCUMENTS.value);
                        } else if (events.size() == 1) {
                            Log.d("DB", String.format("Found event (%s) with event ID (%s)", events.get(0).getEventID(), eventID));
                            listener.OnCompleteDB(events, 1, DBOnCompleteFlags.SINGLE_DOCUMENT.value);
                        } else {
                            Log.d("DB", String.format("Found %d events  with event ID (%s)", events.size(), eventID));
                            listener.OnCompleteDB(events, 1, DBOnCompleteFlags.MULTIPLE_DOCUMENTS.value);
                        }
                    } else {
                        Log.d("DB", String.format("Could not get event with event ID (%s).", eventID));
                        listener.OnCompleteDB(new ArrayList<>(), 1, DBOnCompleteFlags.ERROR.value);
                    }
                });
    }

    /**
     * Gets a long-live download uris for Picasso to get image from Firebase Storage.
     * @author James
     * @param url String rul with gs:// link to Firebase Storage image.
     * @param listener DBOnCompleteListener to call when query completes.
     */
    public void getImageDownloadUrl(String url, StorageOnCompleteListener<Uri> listener) {
        Log.d("DB", String.format("Getting storage reference for image url %s", url));
        try {
            // Try to get an image
            StorageReference ref = storage.getReferenceFromUrl(url);
            ref.getDownloadUrl().addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    // Add the result to an array list and notify the listener
                    Log.d("DB", String.format("Found image at %s", url));
                    Uri uri = task.getResult();
                    ArrayList<Uri> results = new ArrayList<>();
                    results.add(uri);
                    listener.OnCompleteStorage(results, 2, DBOnCompleteFlags.SUCCESS.value);
                } else {
                    Log.d("DB", String.format("Image download failed for %s", url));
                }
            });
        } catch (IllegalArgumentException e) {
            // Notify the listener with an error flag raised
            Log.d("DB", String.format("IllegalArgumentException for url %s", url));
            listener.OnCompleteStorage(new ArrayList<>(), 2, DBOnCompleteFlags.ERROR.value);
        }
    }

    /**
     * This method returns the list of events belonging to an organiser
     * @param accountID The ID used to retrieve the matching events from the database
     * @param listener What gets notified of results
     * @author Kori
     */
    public void getOrganiserEvents(String accountID, DBOnCompleteListener<Event> listener) {
        db.collection("Events")
                .whereEqualTo("organiserID", accountID)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        QuerySnapshot queryResult = task.getResult();
                        ArrayList<Event> events = new ArrayList<>();
                        queryResult.forEach(doc -> events.add(doc.toObject(Event.class)));
                        if (events.size() == 0) {
                            Log.d("DB", String.format("Found no events with organiser ID (%s)", accountID));
                            listener.OnCompleteDB(events, 3, DBOnCompleteFlags.NO_DOCUMENTS.value);
                        } else if (events.size() == 1) {
                            Log.d("DB", String.format("Found event (%s) with organiser ID (%s)", events.get(0).getEventID(), accountID));
                            listener.OnCompleteDB(events, 3, DBOnCompleteFlags.SINGLE_DOCUMENT.value);
                        } else {
                            Log.d("DB", String.format("Found %d events  with organiser ID (%s)", events.size(), accountID));
                            listener.OnCompleteDB(events, 3, DBOnCompleteFlags.MULTIPLE_DOCUMENTS.value);
                        }
                    } else {
                        Log.d("DB", String.format("Could not get event with organiser ID (%s).", accountID));
                        listener.OnCompleteDB(new ArrayList<>(), 3, DBOnCompleteFlags.ERROR.value);
                    }
                });
    }

    /**
     * This method will set the currently existing event to the new one with updated values
     * @author Kori
     * @param event The event we want to update
     * @param listener The listener that initiates when the data is done updating
     */
    public void updateEvent(Event event, DBOnCompleteListener<Event> listener) {
        db.collection("Events")
                .document(event.getEventID())
                .set(event)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Log.d("DB", String.format("Successfully updated event with ID (%s).", event.getEventID()));
                            listener.OnCompleteDB(new ArrayList<>(), 4, DBOnCompleteFlags.SUCCESS.value);
                        } else {
                            Log.d("DB", String.format("Error: Could not update event with ID (%s).", event.getEventID()));
                            listener.OnCompleteDB(new ArrayList<>(), 4, DBOnCompleteFlags.ERROR.value);
                        }
                    }
                });
    }
}
