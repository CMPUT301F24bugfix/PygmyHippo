/**
 * AllImageDB
 *
 * DBHandler for AllImagesFragment and ImageViewHolder.
 */


package com.example.pygmyhippo.admin;

import android.net.Uri;
import android.util.Log;

import com.example.pygmyhippo.common.Account;
import com.example.pygmyhippo.common.Event;
import com.example.pygmyhippo.common.Image;
import com.example.pygmyhippo.database.DBHandler;
import com.example.pygmyhippo.database.DBOnCompleteFlags;
import com.example.pygmyhippo.database.DBOnCompleteListener;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;

/**
 * DBHandler for AllImagesFragment.
 *
 * Handles getting images from firebase storage, getting accounts and events from firestore.
 *
 * TODO: Need redesign for better compatibility with RecyclerView pagination in AllImagesFragment for getting accounts and events.
 */
public class AllImagesDB extends DBHandler {
    /**
     * Gets a long-live download uris for Picasso to get image from Firebase Storage.
     * @param image Image class with gs:// link to Firebase Storage image.
     * @param listener DBOnCompleteListener to call when query completes.
     */
    public void getImageDownloadUrl(Image image, DBOnCompleteListener<Uri> listener) {
        Log.d("DB", String.format("Getting storage reference for image url %s", image.getUrl()));
        try {
            StorageReference ref = storage.getReferenceFromUrl(image.getUrl());
            ref.getDownloadUrl().addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    Log.d("DB", String.format("Found image at %s", image.getUrl()));
                    Uri uri = task.getResult();
                    ArrayList<Uri> results = new ArrayList<>();
                    results.add(uri);
                    listener.OnComplete(results, 0, DBOnCompleteFlags.SUCCESS.value);
                } else {
                    Log.d("DB", String.format("Image download failed for %s", image.getUrl()));
                }
            });
        } catch (IllegalArgumentException e) {
            Log.d("DB", String.format("IllegalArgumentException for url %s", image.getUrl()));
            listener.OnComplete(new ArrayList<>(), 0, DBOnCompleteFlags.ERROR.value);
        }
    }

    /**
     * Gets accounts from Firestore with non-empty string in profilePicture field.
     * @param limit Limit on number of accounts to get in query.
     * @param listener DBOnCompleteListener to call when query completes.
     */
    public void getAccounts(int limit, DBOnCompleteListener<Object> listener) {
        db.collection("Accounts")
            .whereNotEqualTo("profilePicture", "")
            .limit(limit)
            .get()
            .addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    QuerySnapshot queryResult = task.getResult();
                    Log.d("DB", String.format("%d Accounts with non-empty profilePicture are fetched.", queryResult.size()));
                    ArrayList<Account> accountList = new ArrayList<>();
                    queryResult.forEach(doc -> accountList.add(doc.toObject(Account.class)));
                    listener.OnComplete(new ArrayList<>(accountList), 1, DBOnCompleteFlags.SUCCESS.value);
                } else {
                    Log.d("DB", "Error in getting accounts for All Images");
                    listener.OnComplete(new ArrayList<>(), 1, DBOnCompleteFlags.ERROR.value);
                }
            });
    }

    /**
     * Gets accounts from Firestore with non-empty string in eventPoster field.
     * @param limit Limit on number of events to get in query.
     * @param listener DBOnCompleteListener to call when query completes.
     */
    public void getEvents(int limit, DBOnCompleteListener<Object> listener) {
        db.collection("Events")
            .whereNotEqualTo("eventPoster", "")
            .limit(limit)
            .get()
            .addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    QuerySnapshot queryResult = task.getResult();
                    Log.d("DB", String.format("%d Events with non-empty eventPoster are fetched.", queryResult.size()));
                    ArrayList<Event> eventList = new ArrayList<>();
                    queryResult.forEach(doc -> eventList.add(doc.toObject(Event.class)));
                    listener.OnComplete(new ArrayList<>(eventList), 2, DBOnCompleteFlags.SUCCESS.value);
                } else {
                    Log.d("DB", "Error in getting events for All Images");
                    listener.OnComplete(new ArrayList<>(), 2, DBOnCompleteFlags.ERROR.value);
                }
            });
    }
}