package com.example.pygmyhippo.database;

/*
 * DBHandler for ProfileFragment
 *
 * Purposes:
 *      - Provide server connectivity to the profile fragments
 * Issues:
 *      - None
 */

import android.util.Log;

import androidx.annotation.NonNull;

import com.example.pygmyhippo.common.Account;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

/**
 * DBHandler for ProfileFragment
 *
 * Queries for account based off of document ID.
 * Can delete account based off of document ID.
 */
public class AccountDB extends DBHandler {
    /**
     * This method will notify the listener to return the account relating to the given ID
     * @param accountID The ID of the account we want to get
     * @param listener What gets notified of query results
     */
    public void getAccountByID(String accountID, DBOnCompleteListener<Account> listener) {
        db.collection("Accounts")
                .whereEqualTo("accountID", accountID)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        QuerySnapshot queryResult = task.getResult();

                        // Add results to a list, this gets passed to the listener
                        ArrayList<Account> accounts = new ArrayList<>();
                        queryResult.forEach(doc -> accounts.add(doc.toObject(Account.class)));
                        if (accounts.size() == 0) {
                            Log.d("DB", String.format("Found no accounts with account ID (%s)", accountID));
                            listener.OnCompleteDB(accounts, 0, DBOnCompleteFlags.NO_DOCUMENTS.value);
                        } else if (accounts.size() == 1) {
                            Log.d("DB", String.format("Found account (%s) with account ID (%s)", accounts.get(0).getAccountID(), accountID));
                            listener.OnCompleteDB(accounts, 0, DBOnCompleteFlags.SINGLE_DOCUMENT.value);
                        } else {
                            Log.d("DB", String.format("Found %d accounts  with account ID (%s)", accounts.size(), accountID));
                            listener.OnCompleteDB(accounts, 0, DBOnCompleteFlags.MULTIPLE_DOCUMENTS.value);
                        }
                    } else {
                        Log.d("DB", String.format("Could not get account with account ID (%s).", accountID));
                        listener.OnCompleteDB(new ArrayList<>(), 0, DBOnCompleteFlags.ERROR.value);
                    }
                });
    }


    /**
     * This method will delete an account from the database
     * @param accountID The ID of the account we want to delete
     * @param listener What gets notified of query results
     */
    public void deleteAccountByID(String accountID, DBOnCompleteListener<Account> listener) {
        db.collection("Accounts")
            .document(accountID)
            .delete()
            .addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    Log.d("DB", String.format("Successfully deleted Account with ID %s", accountID));
                    listener.OnCompleteDB(new ArrayList<>(), 1, DBOnCompleteFlags.SUCCESS.value);
                } else {
                    Log.d("DB", String.format("Unsuccessful in deleting Account with ID %s", accountID));
                    listener.OnCompleteDB(new ArrayList<>(), 1, DBOnCompleteFlags.ERROR.value);
                }
            });
    }

    /**
     * This method just updates the current role of an account
     * @param accountID The account being updated
     * @param newRole The new role to replace the old
     * @param listener What gets notified of query results
     */
    public void changeCurrentRole(String accountID, Account.AccountRole newRole, DBOnCompleteListener<Account> listener) {
        db.collection("Accounts")
                .document(accountID)
                .update("currentRole", newRole)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Log.d("DB", String.format("Successfully updated current role to %s for account %s", newRole, accountID));
                        ArrayList<Account> accountList = new ArrayList<>();
                        Account newAccount = new Account();
                        newAccount.setAccountID(accountID);
                        newAccount.setCurrentRole(newRole);
                        accountList.add(newAccount);
                        listener.OnCompleteDB(accountList, 2, DBOnCompleteFlags.SUCCESS.value);
                    } else {
                        Log.d("DB", String.format("Could not update current role for account %s", accountID));
                        listener.OnCompleteDB(new ArrayList<>(), 2, DBOnCompleteFlags.SUCCESS.value);
                    }
                });
    }

    /**
     * This method will set the currently existing account to the new one with updated values
     * @author Kori
     * @param account The account we want to update
     * @param listener The listener that initiates when the data is done updating
     */
    public void updateProfile(Account account, DBOnCompleteListener<Account> listener) {
        db.collection("Accounts")
                .document(account.getAccountID())
                .set(account)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Log.d("DB", String.format("Successfully updated account with ID (%s).", account.getAccountID()));
                            listener.OnCompleteDB(new ArrayList<>(), 3, DBOnCompleteFlags.SUCCESS.value);
                        } else {
                            Log.d("DB", String.format("Error: Could not update account with ID (%s).", account.getAccountID()));
                            listener.OnCompleteDB(new ArrayList<>(), 3, DBOnCompleteFlags.ERROR.value);
                        }
                    }
                });
    }

    /**
     * This query specifically gets all the users from the database
     * @param limit The max amount of accounts we want to retrieve
     * @param listener The listener that gets notified when the data is retrieved
     */
    public void getUsers(int limit, DBOnCompleteListener<Account> listener) {
        db.collection("Accounts")
                .limit(limit)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        QuerySnapshot query = task.getResult();
                        Log.d("DB", String.format("Successfully got %d accounts from Firestore", query.size()));

                        // Convert and add the docs to accounts into a list, and notify the listener about the success
                        ArrayList<Account> accountList = new ArrayList<>();
                        query.forEach(doc -> {
                            accountList.add(doc.toObject(Account.class));
                        });
                        listener.OnCompleteDB(accountList, 4, DBOnCompleteFlags.SUCCESS.value);
                    } else {
                        // Notify the listener of an error
                        Log.d("DB", "Could not get accounts from Firestore");
                        listener.OnCompleteDB(new ArrayList<>(), 4, DBOnCompleteFlags.ERROR.value);
                    }
                });
    }
}
