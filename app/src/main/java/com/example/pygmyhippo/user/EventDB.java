/*
 * DBHandler for EventFragment
 * Purposes: Gets and deletes Events by their EventID. Additionally, updates event
 * Issues: None
 */

package com.example.pygmyhippo.user;

import android.util.Log;

import androidx.annotation.NonNull;

import com.example.pygmyhippo.common.Event;
import com.example.pygmyhippo.database.DBHandler;
import com.example.pygmyhippo.database.DBOnCompleteFlags;
import com.example.pygmyhippo.database.DBOnCompleteListener;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;

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
                    listener.OnComplete(eventList, 0, DBOnCompleteFlags.SINGLE_DOCUMENT.value);
                } else {
                    Log.d("DB", String.format("Unsuccessful finding Event with ID %s", eventID));
                    listener.OnComplete(new ArrayList<>(), 0, DBOnCompleteFlags.ERROR.value);
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
                    listener.OnComplete(new ArrayList<>(), 1, DBOnCompleteFlags.SUCCESS.value);
                } else {
                    Log.d("DB", String.format("Unsuccessful in deleting Event with ID %s", eventID));
                    listener.OnComplete(new ArrayList<>(), 1, DBOnCompleteFlags.ERROR.value);
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
                            listener.OnComplete(new ArrayList<>(), 2, DBOnCompleteFlags.SUCCESS.value);
                        } else {
                            Log.d("DB", String.format("Error: Could not update event with ID (%s).", event.getEventID()));
                            listener.OnComplete(new ArrayList<>(), 2, DBOnCompleteFlags.ERROR.value);
                        }
                    }
                });
    }
}
