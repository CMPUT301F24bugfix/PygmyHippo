package com.example.pygmyhippo.database;

/*
This class is the specialized handler for the event fragment
Purposes:
    - To provide server connectivity to the eventFragment
Issues:
    - None
 */

import android.util.Log;

import androidx.annotation.NonNull;

import com.example.pygmyhippo.common.Entrant;
import com.example.pygmyhippo.common.Event;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * Post Event Database handler.
 *
 * Adds and search Event to Firestore when an organiser creates it from PostEventFragment.
 */
public class EventDB extends DBHandler {
    public EventDB(boolean useFirebase) {
        super(useFirebase);
    }

    /**
     * This posts event to database
     * @param newEvent The event getting posted to the database
     * @param listener What gets notified when the update is successful or unsuccessful
     * @author James
     */
    public void addEvent(@NonNull Event newEvent, DBOnCompleteListener<Event> listener) {
        if (useFirebase) {
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
        } else {
            Log.d("DB", "Using offline data for addEvent()");
            ArrayList<Event> newEventList = new ArrayList<>();
            newEventList.add(newEvent);
            listener.OnCompleteDB(newEventList, 0, DBOnCompleteFlags.SUCCESS.value);
        }
    }


    /**
     * This method returns the matching event
     * @param eventID The ID used to retrieve the matching event from the database
     * @param listener What gets notified of results
     * @author Kori
     */
    public void getEventByID(String eventID, DBOnCompleteListener<Event> listener) {
        if (useFirebase) {
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
        } else {
            Log.d("DB", "Using offline data for getEventByID()");
            ArrayList<Event> events = new ArrayList<>();
            Event newEvent = new Event();
            newEvent.setEventTitle("Some Awesome Event Title");
            newEvent.setDescription("Awesome stuff is gonna happen here!");
            newEvent.setLocation("Somewhere close by");
            newEvent.setEventStatus(Event.EventStatus.ongoing);
            newEvent.setCost("$5 CAD / $10 USD");
            newEvent.setDate("Sometime soon");
            newEvent.setEventWinnersCount(100);
            newEvent.setEventID(eventID);
            events.add(newEvent);
            listener.OnCompleteDB(events, 1, DBOnCompleteFlags.SINGLE_DOCUMENT.value);
        }
    }

    /**
     * This method will set the currently existing event to the new one with updated values
     * @author Kori
     * @param event The event we want to update
     * @param listener The listener that initiates when the data is done updating
     */
    public void updateEvent(Event event, DBOnCompleteListener<Event> listener) {
        if (useFirebase) {
            db.collection("Events")
                .document(event.getEventID())
                .set(event)
                .addOnCompleteListener(task -> {
                    ArrayList<Event> events = new ArrayList<>();
                    if (task.isSuccessful()) {
                        events.add(event);
                        Log.d("DB", String.format("Successfully updated event with ID (%s).", event.getEventID()));
                        listener.OnCompleteDB(events, 2, DBOnCompleteFlags.SUCCESS.value);
                    } else {
                        Log.d("DB", String.format("Error: Could not update event with ID (%s).", event.getEventID()));
                        listener.OnCompleteDB(events, 2, DBOnCompleteFlags.ERROR.value);
                    }
                });
        } else {
            Log.d("DB", "Using offline data for updateEvent()");
            ArrayList<Event> events = new ArrayList<>();
            events.add(event);
            listener.OnCompleteDB(events, 2, DBOnCompleteFlags.SUCCESS.value);
        }
    }

    /**
     * This method returns the list of events belonging to an organiser
     * @param accountID The ID used to retrieve the matching events from the database
     * @param listener What gets notified of results
     * @author Kori
     */
    public void getOrganiserEvents(String accountID, DBOnCompleteListener<Event> listener) {
        if (useFirebase) {
            db.collection("Events")
                .whereEqualTo("organiserID", accountID)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        QuerySnapshot queryResult = task.getResult();
                        ArrayList<Event> events = new ArrayList<>();
                        queryResult.forEach(doc -> events.add(doc.toObject(Event.class)));
                        if (events.isEmpty()) {
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
        } else {
            Log.d("DB", "Using offline data for getOrganiserEvents()");
            ArrayList<String> names = new ArrayList<>(Arrays.asList("Event1", "Event2", "Event3", "Event4"));
            ArrayList<Event> events = new ArrayList<>();
            names.forEach(name -> {
                Event newEvent = new Event();
                newEvent.setEventTitle(name);
                events.add(newEvent);
            });
            listener.OnCompleteDB(events, 3, DBOnCompleteFlags.SINGLE_DOCUMENT.value);
        }
    }

    /**
     * This method will delete the event from the database
     * @author James
     * @param eventID The ID of the event we want to delete
     * @param listener The listener for when the deletion is done
     */
    public void deleteEventByID(String eventID, DBOnCompleteListener<Event> listener) {
        if (useFirebase) {
            db.collection("Events")
                .document(eventID)
                .delete()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Log.d("DB", String.format("Successfully deleted Event with ID %s", eventID));
                        listener.OnCompleteDB(new ArrayList<>(), 4, DBOnCompleteFlags.SUCCESS.value);
                    } else {
                        Log.d("DB", String.format("Unsuccessful in deleting Event with ID %s", eventID));
                        listener.OnCompleteDB(new ArrayList<>(), 4, DBOnCompleteFlags.ERROR.value);
                    }
                });
        } else {
            Log.d("DB", "Using offline data for deleteEventByID()");
            listener.OnCompleteDB(new ArrayList<>(), 4, DBOnCompleteFlags.SUCCESS.value);
        }
    }

    /**
     * This method will return the list of events that an entrant is in
     * @author Kori
     * @param accountID The ID of the entrant
     * @param listener What gets called when the data is retrieved
     */
    public void getEntrantEvents(String accountID, DBOnCompleteListener<Event> listener) {
        if (useFirebase) {
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
                        listener.OnCompleteDB(eventList, 5, DBOnCompleteFlags.SUCCESS.value);
                    } else {
                        Log.d("DB", "Could not get Events");
                        listener.OnCompleteDB(new ArrayList<>(), 5, DBOnCompleteFlags.ERROR.value);
                    }
                });
        } else {
            Log.d("DB", "Using offline data for getEntrantEvents()");
            ArrayList<String> names = new ArrayList<>(Arrays.asList("Event1", "Event2", "Event3", "Event4"));
            ArrayList<Event> events = new ArrayList<>();
            names.forEach(name -> {
                Event newEvent = new Event();
                newEvent.setEventTitle(name);
                events.add(newEvent);
            });
            listener.OnCompleteDB(events, 5, DBOnCompleteFlags.SUCCESS.value);
        }
    }

    /**
     * Returns a limited amount of events from the whole event collection from the database
     * @param limit
     *          The max amount of events we want
     * @param listener
     *          The DB listener for data callback
     */
    public void getEvents(int limit, DBOnCompleteListener<Event> listener) {
        if (useFirebase) {
            db.collection("Events")
                .limit(limit)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        // Get all the docs
                        QuerySnapshot docs = task.getResult();
                        Log.d("DB", String.format("Found %d Events", docs.size()));

                        // Convert each doc to an event and store it
                        ArrayList<Event> eventList = new ArrayList<>();
                        docs.forEach(doc -> {
                            eventList.add(doc.toObject(Event.class));
                        });

                        // Call the listener
                        listener.OnCompleteDB(eventList, 6, DBOnCompleteFlags.SUCCESS.value);
                    } else {
                        Log.d("DB", "Could not get Events");
                        listener.OnCompleteDB(new ArrayList<>(), 6, DBOnCompleteFlags.ERROR.value);
                    }
                });
        } else {
            Log.d("DB", "Using offline data for getEvents()");
            ArrayList<String> names = new ArrayList<>(Arrays.asList("Event1", "Event2", "Event3", "Event4"));
            ArrayList<Event> events = new ArrayList<>();
            names.forEach(name -> {
                Event newEvent = new Event();
                newEvent.setEventTitle(name);
                events.add(newEvent);
            });
            listener.OnCompleteDB(events, 6, DBOnCompleteFlags.SUCCESS.value);
        }
    }
}
