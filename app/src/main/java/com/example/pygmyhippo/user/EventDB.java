/*
 * DBHandler for EventFragment
 * Purposes: Gets and deletes Events by their EventID. Additionally, updates event
 * Issues: None
 */

package com.example.pygmyhippo.user;

import android.net.Uri;
import android.util.Log;

import androidx.annotation.NonNull;

import com.example.pygmyhippo.common.Entrant;
import com.example.pygmyhippo.common.Event;
import com.example.pygmyhippo.database.DBHandler;
import com.example.pygmyhippo.database.DBOnCompleteFlags;
import com.example.pygmyhippo.database.DBOnCompleteListener;
import com.example.pygmyhippo.database.StorageOnCompleteListener;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.Filter;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;

/**
 * DBHandler for EventFragment
 *
 * Gets and deletes Events by their EventID.
 * @author James
 */
public class EventDB extends DBHandler {
    /**
     * This method will return an event from the database
     * @author James
     * @param eventID The id of the event we want
     * @param listener The listener for when the data is gotten
     */
    public void getEventByID(String eventID, DBOnCompleteListener<Event> listener) {
        db.collection("Events")
            .document(eventID)
            .get()
            .addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    Log.d("DB", String.format("Successfully retrieved Event with ID %s", eventID));
                    DocumentSnapshot doc = task.getResult();
                    ArrayList<Event> eventList = new ArrayList<>();
                    eventList.add(doc.toObject(Event.class));
                    listener.OnCompleteDB(eventList, 0, DBOnCompleteFlags.SINGLE_DOCUMENT.value);
                } else {
                    Log.d("DB", String.format("Unsuccessful finding Event with ID %s", eventID));
                    listener.OnCompleteDB(new ArrayList<>(), 0, DBOnCompleteFlags.ERROR.value);
                }
            });
    }

    /**
     * This method will delete the event from the database
     * @author James
     * @param eventID The ID of the event we want to delete
     * @param listener The listener for when the deletion is done
     */
    public void deleteEventByID(String eventID, DBOnCompleteListener<Event> listener) {
        db.collection("Events")
            .document(eventID)
            .delete()
            .addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    Log.d("DB", String.format("Successfully deleted Event with ID %s", eventID));
                    listener.OnCompleteDB(new ArrayList<>(), 1, DBOnCompleteFlags.SUCCESS.value);
                } else {
                    Log.d("DB", String.format("Unsuccessful in deleting Event with ID %s", eventID));
                    listener.OnCompleteDB(new ArrayList<>(), 1, DBOnCompleteFlags.ERROR.value);
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
                        ArrayList<Event> events = new ArrayList<>();
                        if (task.isSuccessful()) {
                            events.add(event);
                            Log.d("DB", String.format("Successfully updated event with ID (%s).", event.getEventID()));
                            listener.OnCompleteDB(events, 2, DBOnCompleteFlags.SUCCESS.value);
                        } else {
                            Log.d("DB", String.format("Error: Could not update event with ID (%s).", event.getEventID()));
                            listener.OnCompleteDB(events, 2, DBOnCompleteFlags.ERROR.value);
                        }
                    }
                });
    }

    /**
     * This method will return the list of events that an entrant is in
     * @author Kori
     * @param accountID The ID of the entrant
     * @param listener What gets called when the data is retrieved
     */
    public void getEntrantEvents(String accountID, DBOnCompleteListener<Event> listener) {
        // Query modified from James' AllEventsDB
        db.collection("Events")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        // Get all the docs
                        QuerySnapshot docs = task.getResult();
                        Log.d("DB", String.format("Found %d Events", docs.size()));

                        // Convert each doc to an event and store it only if it contains the entrant
                        ArrayList<Event> eventList = new ArrayList<>();
                        docs.forEach(doc -> {
                            // Convert the doc to an event object
                            Event event = doc.toObject(Event.class);

                            // Go through the entrant list if it has
                            if (event.getEntrants() != null) {
                                // Note, setting the status won't alter the results of the query
                                // because hasEntrant only matches by accountID
                                if (event.hasEntrant(new Entrant(accountID, Entrant.EntrantStatus.waitlisted))) {
                                    // If the current Account ID matches, then add this event to the list to return
                                    eventList.add(event);
                                }
                            }
                        });

                        // Call the listener
                        listener.OnCompleteDB(eventList, 3, DBOnCompleteFlags.SUCCESS.value);
                    } else {
                        Log.d("DB", "Could not get Events");
                        listener.OnCompleteDB(new ArrayList<>(), 3, DBOnCompleteFlags.ERROR.value);
                    }
                });
        }
    }
