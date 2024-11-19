package com.example.pygmyhippo.database;

/*
This class specifically handles storing the event poster to the database
Purposes:
    - Stores the event poster for the organiser (ie, lets them upload)
Issues:
    - Perhaps make an image Handler class so we dont have to use and event listener for this
    - Should convert the image attributes to an actual image class
 */

import android.net.Uri;
import android.util.Log;

import com.example.pygmyhippo.common.Account;
import com.example.pygmyhippo.common.Event;
import com.example.pygmyhippo.common.Image;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.UUID;

/**
 * Post Event Database handler.
 *
 * Adds image to Firestore storage when an organiser uploads it from PostEventFragment.
 */
public class ImageStorage extends DBHandler {
    /**
     * This posts image to database
     * @param imageUri
     * @param listener
     * @author Griffin
     */
    public void uploadImageToFirebase(Uri imageUri, StorageOnCompleteListener<Image> listener) {
        StorageReference storageRef = storage.getReference();
        String imageName = "events/" + UUID.randomUUID().toString();
        StorageReference imageRef = storageRef.child(imageName);
        ArrayList<Image> images = new ArrayList<>();

        imageRef.putFile(imageUri)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        imageRef.getDownloadUrl().addOnSuccessListener(uri -> {
                            Log.d("FirebaseStorage", "Image uploaded successfully. URL: " +  uri.toString());
                            Image newImage = new Image();
                            newImage.setUrl(uri.toString());
                            newImage.setType(Image.ImageType.Event);

                            images.add(newImage);
                            listener.OnCompleteStorage(images, 0, DBOnCompleteFlags.SUCCESS.value);
                        });
                    }
                    else{
                        Log.e("FirebaseStorage:", "Image uploaded Error.");
                        listener.OnCompleteStorage(images, 0,DBOnCompleteFlags.ERROR.value);
                    }
                });
    }

    /**
     * Gets a long-live download uris for Picasso to get image from Firebase Storage.
     * @author James
     * @param url String rul with gs:// link to Firebase Storage image.
     * @param listener DBOnCompleteListener to call when query completes.
     */
    public void getImageDownloadUrl(String url, StorageOnCompleteListener<Uri> listener) {
        Log.d("DB", String.format("Getting storage reference for image url %s", url));
        try {
            // Try to get an image
            StorageReference ref = storage.getReferenceFromUrl(url);
            ref.getDownloadUrl().addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    // Add the result to an array list and notify the listener
                    Log.d("DB", String.format("Found image at %s", url));
                    Uri uri = task.getResult();
                    ArrayList<Uri> results = new ArrayList<>();
                    results.add(uri);
                    listener.OnCompleteStorage(results, 1, DBOnCompleteFlags.SUCCESS.value);
                } else {
                    Log.d("DB", String.format("Image download failed for %s", url));
                }
            });
        } catch (IllegalArgumentException e) {
            // Notify the listener with an error flag raised
            Log.d("DB", String.format("IllegalArgumentException for url %s", url));
            listener.OnCompleteStorage(new ArrayList<>(), 1, DBOnCompleteFlags.ERROR.value);
        }
    }

    /**
     * Gets accounts from Firestore with non-empty string in profilePicture field.
     * @param limit Limit on number of accounts to get in query.
     * @param listener DBOnCompleteListener to call when query completes.
     */
    public void getAccountsWithImage(int limit, StorageOnCompleteListener<Object> listener) {
        db.collection("Accounts")
                .whereNotEqualTo("profilePicture", "")
                .limit(limit)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        // Convert each document to an account, add it to a list, and notify the listener with success flag raised
                        QuerySnapshot queryResult = task.getResult();
                        Log.d("DB", String.format("%d Accounts with non-empty profilePicture are fetched.", queryResult.size()));
                        ArrayList<Account> accountList = new ArrayList<>();
                        queryResult.forEach(doc -> accountList.add(doc.toObject(Account.class)));
                        listener.OnCompleteStorage(new ArrayList<>(accountList), 2, DBOnCompleteFlags.SUCCESS.value);
                    } else {
                        // Notify the listener with the Error flag raised
                        Log.d("DB", "Error in getting accounts for All Images");
                        listener.OnCompleteStorage(new ArrayList<>(), 2, DBOnCompleteFlags.ERROR.value);
                    }
                });
    }

    /**
     * Gets accounts from Firestore with non-empty string in eventPoster field.
     * @param limit Limit on number of events to get in query.
     * @param listener DBOnCompleteListener to call when query completes.
     */
    public void getEventsWithImage(int limit, StorageOnCompleteListener<Object> listener) {
        db.collection("Events")
                .whereNotEqualTo("eventPoster", "")
                .limit(limit)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        // Convert the docs to events, pass them as a list to the listener with the succes flag raised
                        QuerySnapshot queryResult = task.getResult();
                        Log.d("DB", String.format("%d Events with non-empty eventPoster are fetched.", queryResult.size()));
                        ArrayList<Event> eventList = new ArrayList<>();
                        queryResult.forEach(doc -> eventList.add(doc.toObject(Event.class)));
                        listener.OnCompleteStorage(new ArrayList<>(eventList), 3, DBOnCompleteFlags.SUCCESS.value);
                    } else {
                        // Notify the listener with error flag raised
                        Log.d("DB", "Error in getting events for All Images");
                        listener.OnCompleteStorage(new ArrayList<>(), 3, DBOnCompleteFlags.ERROR.value);
                    }
                });
    }

    /**
     * This posts a profile image to the database
     * @param imageUri
     * @param listener
     * @author Griffin (Kori modified this)
     */
    public void uploadProfileImageToFirebase(Uri imageUri, StorageOnCompleteListener<Image> listener) {
        StorageReference storageRef = storage.getReference();
        String imageName = "profiles/" + UUID.randomUUID().toString();
        StorageReference imageRef = storageRef.child(imageName);
        ArrayList<Image> images = new ArrayList<>();

        imageRef.putFile(imageUri)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        imageRef.getDownloadUrl().addOnSuccessListener(uri -> {
                            Log.d("FirebaseStorage", "Image uploaded successfully. URL: " +  uri.toString());
                            Image newImage = new Image();
                            newImage.setUrl(uri.toString());
                            newImage.setType(Image.ImageType.Event);

                            images.add(newImage);
                            listener.OnCompleteStorage(images, 4, DBOnCompleteFlags.SUCCESS.value);
                        });
                    }
                    else{
                        Log.e("FirebaseStorage:", "Image uploaded Error.");
                        listener.OnCompleteStorage(images, 4,DBOnCompleteFlags.ERROR.value);
                    }
                });
    }

    /**
     * This deletes the given image from the database
     * @param url The url of the image we want to delete
     * @param listener What gets called when the deletion is done
     * @author Kori
     */
    public void DeleteImageByURL(String url, StorageOnCompleteListener<Image> listener) {
        StorageReference imageRef = storage.getReferenceFromUrl(url);

        // Delete the image using the reference we got based from the url
        imageRef.delete()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Log.d("FirebaseStorage", "Image deleted successfully. URL: " + url);

                        listener.OnCompleteStorage(new ArrayList<>(), 5, DBOnCompleteFlags.SUCCESS.value);
                    }
                    else{
                        Log.e("FirebaseStorage:", "Image deletion Error.");
                        listener.OnCompleteStorage(new ArrayList<>(), 5,DBOnCompleteFlags.ERROR.value);
                    }
                });
    }
}