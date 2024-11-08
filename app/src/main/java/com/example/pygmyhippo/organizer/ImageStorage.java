package com.example.pygmyhippo.organizer;

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

import com.example.pygmyhippo.database.DBHandler;
import com.example.pygmyhippo.database.DBOnCompleteFlags;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.UUID;
import com.example.pygmyhippo.common.Event;
import com.example.pygmyhippo.database.DBOnCompleteListener;

/**
 * Post Event Database handler.
 *
 * Adds image to Firestore storage when an organiser uploads it from PostEventFragment.
 * TODO:
 * - Perhaps make an image Handler class so we dont have to use and event listener for this
 * (^talk to griffin if you are confused about this)
 */
public class ImageStorage extends DBHandler {
    /**
     * This posts image to database
     * @param imageUri
     * @param listener
     * @author Griffin
     */
    public void uploadImageToFirebase(Uri imageUri, DBOnCompleteListener<Event> listener) {
        StorageReference storageRef = storage.getReference();
        String imageName = "events/" + UUID.randomUUID().toString();
        StorageReference imageRef = storageRef.child(imageName);
        ArrayList<Event> events = new ArrayList<>();

        imageRef.putFile(imageUri)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        imageRef.getDownloadUrl().addOnSuccessListener(uri -> {
                            Log.d("FirebaseStorage", "Image uploaded successfully. URL: " +  uri.toString());
                            Event newEvent = new Event();
                            newEvent.setEventPoster(uri.toString());
                            events.add(newEvent);
                            listener.OnComplete(events, 3, DBOnCompleteFlags.SUCCESS.value);
                        });
                    }
                    else{
                        Log.e("FirebaseStorage:", "Image uploaded Error.");
                        listener.OnComplete(events, 3,DBOnCompleteFlags.ERROR.value);
                    }
                });
    }
}