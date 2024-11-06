package com.example.pygmyhippo.admin;

import android.util.Log;

import com.example.pygmyhippo.common.Account;
import com.example.pygmyhippo.database.DBHandler;
import com.example.pygmyhippo.database.DBOnCompleteFlags;
import com.example.pygmyhippo.database.DBOnCompleteListener;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class AllUsersDB extends DBHandler {
    public void getUsers(int limit, DBOnCompleteListener<Account> listener) {
        db.collection("Accounts")
            .limit(limit)
            .get()
            .addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    QuerySnapshot query = task.getResult();
                    Log.d("DB", String.format("Successfully got %d accounts from Firestore", query.size()));
                    ArrayList<Account> accountList = new ArrayList<>();
                    query.forEach(doc -> {
                        accountList.add(doc.toObject(Account.class));
                    });
                    listener.OnComplete(accountList, 0, DBOnCompleteFlags.SUCCESS.value);
                } else {
                    Log.d("DB", "Could not get accounts from Firestore");
                    listener.OnComplete(new ArrayList<>(), 0, DBOnCompleteFlags.ERROR.value);
                }
            });
    }
}
