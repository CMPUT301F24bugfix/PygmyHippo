package com.example.pygmyhippo.organizer;

/*
This class handles the database queries necessary for the View Entrants fragment.
Contributors: Kori, James (for code style)

Purposes:
    - Get the event to display the entrant list
    - Get the Account of said entrant to display their names in the array adapter
Issues:
    - None so far
 */

import android.util.Log;

import com.example.pygmyhippo.common.Account;
import com.example.pygmyhippo.common.Event;
import com.example.pygmyhippo.database.DBHandler;
import com.example.pygmyhippo.database.DBOnCompleteFlags;
import com.example.pygmyhippo.database.DBOnCompleteListener;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

/**
 * DB Handler for ViewEntrantsFragment
 */
public class ViewEntrantDB extends DBHandler {

    /**
     * This method returns the matching event
     * @param eventID
     * @param listener
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
                            listener.OnComplete(events, 0, DBOnCompleteFlags.NO_DOCUMENTS.value);
                        } else if (events.size() == 1) {
                            Log.d("DB", String.format("Found event (%s) with event ID (%s)", events.get(0).getEventID(), eventID));
                            listener.OnComplete(events, 0, DBOnCompleteFlags.SINGLE_DOCUMENT.value);
                        } else {
                            Log.d("DB", String.format("Found %d events  with event ID (%s)", events.size(), eventID));
                            listener.OnComplete(events, 0, DBOnCompleteFlags.MULTIPLE_DOCUMENTS.value);
                        }
                    } else {
                        Log.d("DB", String.format("Could not get event with event ID (%s).", eventID));
                        listener.OnComplete(new ArrayList<>(), 0, DBOnCompleteFlags.ERROR.value);
                    }
                });
    }

    /**
     * This method returns the account matching the entrant from the list
     * @param accountID
     * @param listener
     */
    public void getAccount(String accountID, DBOnCompleteListener<Account> listener) {
        db.collection("Accounts")
                .whereEqualTo("accountID", accountID)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        QuerySnapshot queryResult = task.getResult();
                        ArrayList<Account> accounts = new ArrayList<>();
                        queryResult.forEach(doc -> accounts.add(doc.toObject(Account.class)));
                        if (accounts.size() == 0) {
                            Log.d("DB", String.format("Found no accounts with account ID (%s)", accountID));
                            listener.OnComplete(accounts, 1, DBOnCompleteFlags.NO_DOCUMENTS.value);
                        } else if (accounts.size() == 1) {
                            Log.d("DB", String.format("Found account (%s) with account ID (%s)", accounts.get(0).getAccountID(), accountID));
                            listener.OnComplete(accounts, 1, DBOnCompleteFlags.SINGLE_DOCUMENT.value);
                        } else {
                            Log.d("DB", String.format("Found %d accounts  with account ID (%s)", accounts.size(), accountID));
                            listener.OnComplete(accounts, 1, DBOnCompleteFlags.MULTIPLE_DOCUMENTS.value);
                        }
                    } else {
                        Log.d("DB", String.format("Could not get account with account ID (%s).", accountID));
                        listener.OnComplete(new ArrayList<>(), 1, DBOnCompleteFlags.ERROR.value);
                    }
                });
    }
}
