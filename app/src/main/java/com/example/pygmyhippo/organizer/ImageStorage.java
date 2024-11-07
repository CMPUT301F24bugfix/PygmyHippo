package com.example.pygmyhippo.organizer;

import android.net.Uri;
import android.util.Log;

import com.example.pygmyhippo.common.Event;
import com.example.pygmyhippo.database.DBHandler;
import com.example.pygmyhippo.database.DBOnCompleteFlags;
import com.example.pygmyhippo.database.DBOnCompleteListener;
import com.google.firebase.storage.FirebaseStorage;
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
    public void uploadImageToFirebase(Uri imageUri,  DBOnCompleteListener<Event> listener) {
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
                            listener.OnComplete(events, 3,DBOnCompleteFlags.SUCCESS.value);
                        });
                    }
                    else{
                        Log.e("FirebaseStorage:", "Image uploaded Error.");
                        listener.OnComplete(events, 3,DBOnCompleteFlags.ERROR.value);
                    }
                });
    }
}