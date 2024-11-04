package com.example.pygmyhippo;

import android.util.Log;

import com.example.pygmyhippo.common.Account;
import com.example.pygmyhippo.database.DBHandler;
import com.example.pygmyhippo.database.DBOnCompleteFlags;
import com.example.pygmyhippo.database.DBOnCompleteListener;
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
                        listener.OnComplete(accounts, 0, DBOnCompleteFlags.NO_DOCUMENTS.value);
                    } else if (accounts.size() == 1) {
                        Log.d("DB", String.format("Found account (%s) with device ID (%s)", accounts.get(0).getAccountID(), deviceID));
                        listener.OnComplete(accounts, 0, DBOnCompleteFlags.SINGLE_DOCUMENT.value);
                    } else {
                        Log.d("DB", String.format("Found %d accounts  with device ID (%s)", accounts.size(), deviceID));
                        listener.OnComplete(accounts, 0, DBOnCompleteFlags.MULTIPLE_DOCUMENTS.value);
                    }
                } else {
                    Log.d("DB", String.format("Could not get account with device ID (%s).", deviceID));
                    listener.OnComplete(new ArrayList<>(), 0, DBOnCompleteFlags.ERROR.value);
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
                listener.OnComplete(accounts, 1, DBOnCompleteFlags.SUCCESS.value);
            } else {
                listener.OnComplete(new ArrayList<>(), 1, DBOnCompleteFlags.ERROR.value);
            }
        });
    }
}