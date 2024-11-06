/**
 * DBHandler for EventFragment
 *
 * Gets and deletes Events by their EventID.
 */

package com.example.pygmyhippo.user;

import android.util.Log;

import com.example.pygmyhippo.common.Event;
import com.example.pygmyhippo.database.DBHandler;
import com.example.pygmyhippo.database.DBOnCompleteFlags;
import com.example.pygmyhippo.database.DBOnCompleteListener;
import com.google.firebase.firestore.DocumentSnapshot;

import java.util.ArrayList;

/**
 * DBHandler for EventFragment
 *
 * Gets and deletes Events by their EventID.
 */
public class EventDB extends DBHandler {
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
}
