/**
 * DBHandler for AllEventsFragment
 */

package com.example.pygmyhippo.admin;

import android.util.Log;

import com.example.pygmyhippo.common.Event;
import com.example.pygmyhippo.database.DBHandler;
import com.example.pygmyhippo.database.DBOnCompleteFlags;
import com.example.pygmyhippo.database.DBOnCompleteListener;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;


/**
 * DBHandler for AllEventsFragment
 *
 * Handles getting events from Firestore.
 */
public class AllEventsDB extends DBHandler {
    public void getEvents(int limit, DBOnCompleteListener<Event> listener) {
        db.collection("Events")
            .limit(limit)
            .get()
            .addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    QuerySnapshot docs = task.getResult();
                    Log.d("DB", String.format("Found %d Events", docs.size()));

                    ArrayList<Event> eventList = new ArrayList<>();
                    docs.forEach(doc -> {
                        eventList.add(doc.toObject(Event.class));
                    });

                    listener.OnComplete(eventList, 0, DBOnCompleteFlags.SUCCESS.value);
                } else {
                    Log.d("DB", "Could not get Events");
                    listener.OnComplete(new ArrayList<>(), 0, DBOnCompleteFlags.ERROR.value);
                }
            });
    }
}
