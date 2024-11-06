package com.example.pygmyhippo.organizer;

import android.util.Log;

import androidx.annotation.NonNull;

import com.example.pygmyhippo.common.Event;
import com.example.pygmyhippo.database.DBHandler;
import com.example.pygmyhippo.database.DBOnCompleteFlags;
import com.example.pygmyhippo.database.DBOnCompleteListener;
import com.google.firebase.firestore.DocumentReference;

import java.util.ArrayList;

/**
 * Post Event Database handler.
 *
 * Adds new Event to Firestore when an organiser creates it from PostEventFragment.
 */
public class PostEventDB extends DBHandler {
    public void addEvent(@NonNull Event newEvent, DBOnCompleteListener<Event> listener) {
        DocumentReference docRef = db.collection("Events").document();
        newEvent.setEventID(docRef.getId());
        docRef.set(newEvent)
            .addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    Log.d("DB", String.format("Successfully added Event %s", newEvent.getEventID()));
                    ArrayList<Event> newEventList = new ArrayList<>();
                    newEventList.add(newEvent);
                    listener.OnComplete(newEventList, 0, DBOnCompleteFlags.SUCCESS.value);
                } else {
                    Log.d("DB", String.format("Unsuccessfully in adding Event %s", newEvent.getEventID()));
                    listener.OnComplete(new ArrayList<>(), 0, DBOnCompleteFlags.ERROR.value);
                }
            });
    }
}
