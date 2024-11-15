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

import com.example.pygmyhippo.common.Image;
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
}