package com.example.pygmyhippo.user;

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
import com.example.pygmyhippo.database.DBHandler;
import com.example.pygmyhippo.database.DBOnCompleteFlags;
import com.example.pygmyhippo.database.DBOnCompleteListener;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;

import java.util.ArrayList;

/**
 * DBHandler for ProfileFragment
 *
 * Queries for account based off of document ID.
 * Can delete account based off of document ID.
 */
public class ProfileDB extends DBHandler {
    /**
     * This method will notify the listener to return the account relating to the given ID
     * @param accountID The ID of the account we want to get
     * @param listener What gets notified of query results
     */
    public void getAccountByID(String accountID, DBOnCompleteListener<Account> listener) {
        db.collection("Accounts")
            .document(accountID)
            .get()
            .addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    Log.d("DB", String.format("Successfully got Account with ID %s", accountID));
                    DocumentSnapshot doc = task.getResult();
                    ArrayList<Account> accountList = new ArrayList<>();
                    accountList.add(doc.toObject(Account.class));
                    listener.OnCompleteDB(accountList, 0, DBOnCompleteFlags.SUCCESS.value);
                } else {
                    Log.d("DB", String.format("Unsuccessful in getting Account with ID %s", accountID));
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
}
