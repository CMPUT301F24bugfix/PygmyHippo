package com.example.pygmyhippo.database;

/*
This class acts as the data base handler for the main activity
Purposes:
    - Adds server connectivity
    - Gets accounts based on device ID or makes a new one if it doesn't already exist
Issues:
    - Need an actual "registration page" so new users can fill out initial profile data
 */

import android.util.Log;

import com.example.pygmyhippo.common.Account;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

/**
 * DB handler for MainActivty.
 *
 * Generally only used for initially opening the app and checking for accounts associated
 * with the device ID.
 * TODO: Replace this with registration page when that is done.
 */
public class MainActivityDB extends DBHandler {
    public MainActivityDB() {
        super(true);
    }

    /**
     * Searches for accounts associated with a device ID.
     *
     * Query ID: 0
     * @param deviceID - device ID to search for.
     * @param listener - onCompleteListener to call after query completes.
     */
    public void getDeviceAccount(String deviceID, DBOnCompleteListener<Account> listener) {
        db.collection("Accounts")
            .whereEqualTo("deviceID", deviceID)
            .get()
            .addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    QuerySnapshot queryResult = task.getResult();
                    ArrayList<Account> accounts = new ArrayList<>();
                    queryResult.forEach(doc -> accounts.add(doc.toObject(Account.class)));
                    if (accounts.size() == 0) {
                        Log.d("DB", String.format("Found no accounts with device ID (%s)", deviceID));
                        listener.OnCompleteDB(accounts, 0, DBOnCompleteFlags.NO_DOCUMENTS.value);
                    } else if (accounts.size() == 1) {
                        Log.d("DB", String.format("Found account (%s) with device ID (%s)", accounts.get(0).getAccountID(), deviceID));
                        listener.OnCompleteDB(accounts, 0, DBOnCompleteFlags.SINGLE_DOCUMENT.value);
                    } else {
                        Log.d("DB", String.format("Found %d accounts  with device ID (%s)", accounts.size(), deviceID));
                        listener.OnCompleteDB(accounts, 0, DBOnCompleteFlags.MULTIPLE_DOCUMENTS.value);
                    }
                } else {
                    Log.d("DB", String.format("Could not get account with device ID (%s).", deviceID));
                    listener.OnCompleteDB(new ArrayList<>(), 0, DBOnCompleteFlags.ERROR.value);
                }
            });
    }

    /**
     * Creates a new account and adds it to Accounts collection.
     *
     * Query ID: 1.
     * @param deviceID - device ID to use for the new account.
     * @param listener - onCompleteListener to call after query completes.
     */
    public void addNewDevice(String deviceID, DBOnCompleteListener<Account> listener) {
        Log.d("DB", String.format("Adding new account with device ID (%s)", deviceID));
        DocumentReference docRef = db.collection("Accounts").document();
        Account newAccount = new Account();
        newAccount.setAccountID(docRef.getId());
        newAccount.setDeviceID(deviceID);
        newAccount.setCurrentRole(Account.AccountRole.user);
        docRef.set(newAccount).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                ArrayList<Account> accounts = new ArrayList<>();
                accounts.add(newAccount);
                listener.OnCompleteDB(accounts, 1, DBOnCompleteFlags.SUCCESS.value);
            } else {
                listener.OnCompleteDB(new ArrayList<>(), 1, DBOnCompleteFlags.ERROR.value);
            }
        });
    }
}
