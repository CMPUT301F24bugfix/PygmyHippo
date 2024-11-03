package com.example.pygmyhippo.common.UserProcessing;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.widget.ImageView;

import androidx.annotation.NonNull;

import com.example.pygmyhippo.common.Account;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;


import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;

/**
 * Allows Download of avatar from online generator
 * Source Code from Googles Cronet Docs
 * https://chromium.googlesource.com/chromium/src/+/refs/heads/main/components/cronet/getting_started.md
 * Accessed November 1, 2024
 * Supplemented holes in the code with the Kotlin CodeLab from android
 * https://developer.android.com/codelabs/cronet#7
 * and the Android developer docs
 * https://developer.android.com/develop/connectivity/cronet/start#java
 @author Google, implemented by Jennifer Mckay
 @Version 1.0
 TODO: make this run async
 */
public class UserProcessing {
    private String imageUrl = "https://avatar.iran.liara.run/username?username="; // urls can be used interchangeably
    private String imageUrl2 = "https://api.multiavatar.com/";
    private String avatarPath;
    private ImageView imageView;
    Context context;
    byte[] avatarBytes;

    // User info
    String userName;
    String userEmail;
    String userPronouns;
    String userPhone;
    String userLocation;
    String accountId;
    String deviceID;
    Boolean userNotify;
    Boolean userGeolocation;


    public void processData(
            Activity activity,
            ImageView imageView,
            String name,
            String phone,
            String email,
            String pronouns,
            String deviceId,
            String location,
            String accountId,
            Boolean allowGeo,
            Boolean allowNotifications,
            String imagePath
    ) throws Exception {
        this.userName = name;
        this.userEmail = email;
        this.userPhone = phone;
        this.userPronouns = pronouns;
        this.userLocation = location;
        this.deviceID = deviceId;
        this.accountId = accountId;
        this.userGeolocation = allowGeo;
        this.userNotify = allowNotifications;
        this.context = activity.getApplicationContext();
        this.imageView = new ImageView(context);

        if (imagePath == null) {
            this.avatarPath = imageUrl + userName;
            loadAndUpload();
            System.out.println("generate avatar..");
        } else {
            System.out.println("Creating...");
            Account newAccount = new Account(
                    userEmail + deviceID,
                    userName,
                    userPronouns,
                    userPhone,
                    userEmail,
                    deviceID,
                    imagePath,
                    null,
                    userLocation,
                    userNotify,
                    userGeolocation
            );
            CreateUser newUser= new CreateUser();
            newUser.setInfoAndCreate(newAccount);

        }
    }

    /** Code is from Chatgpt for uploading from Picasso's cache to firebase
     * Prompts: Access the files stored in cache by picasso, I need (it) to be from from cache because firebase cannot load from a url
     * Accessed November 2, 2024
     * @Author: Chatgpt, implemented and Edited by Jennifer
     *
     * */
    public void loadAndUpload() {
        // Create a Target to handle the bitmap,
        Target target = new Target() {
            @Override
            public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                // Upload the bitmap to Firebase
                try {
                    uploadAvatar(bitmap);
                    System.out.println("Setting avatar");
                } catch (FileNotFoundException e) {
                    throw new RuntimeException(e);
                }
            }

            @Override
            public void onBitmapFailed(Exception e, Drawable errorDrawable) {
                // Handle loading error
                System.out.println("Bitmap failed" + e);
            }

            @Override
            public void onPrepareLoad(Drawable placeHolderDrawable) {
                // Optional: Prepare for loading
            }
        };

        // Load the image into the Target
        Picasso.get()
                .load(avatarPath)
                .into(target);
    }

    /** Code is to upload generated avatar to firebase, first two lines are final bit of code from chatgpt, the rest is from the fisebase docs on uploading files
     * https://firebase.google.com/docs/storage/android/upload-files
     * Accessed November 2, 2024
     * @Author: Jennifer and Firebase documentation
     * @version 1.0
     * */
    private void uploadAvatar(Bitmap bitmap) throws FileNotFoundException {
        // Convert bitmap to byte array for upload
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
        this.avatarBytes = stream.toByteArray();

        // Upload byteArray to Firebase Storage
        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference avatarRef = storage.getReference().child("/"+deviceID+"/avatar/"+"avatar");
        UploadTask uploadAvatar = avatarRef.putBytes(this.avatarBytes);

        System.out.println("Set avatar bytes");
        // do not move logic to Account or this will break
        Task<Uri> urlTask = uploadAvatar.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
            @Override
            public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                if (!task.isSuccessful()) {
                    throw task.getException();
                }
                // Continue with the task to get the download URL
                return avatarRef.getDownloadUrl();
            }
        }).addOnCompleteListener(new OnCompleteListener<Uri>() {
            @Override
            public void onComplete(@NonNull Task<Uri> task) {
                if (task.isSuccessful()) {
                    Uri downloadUri = task.getResult();
                    System.out.println("Creating...");
                    try {
                        Account newAccount = new Account(
                                userEmail + deviceID,
                                userName,
                                userPronouns,
                                userPhone,
                                userEmail,
                                deviceID,
                                null,
                                downloadUri,
                                userLocation,
                                userNotify,
                                userGeolocation
                        );

                       CreateUser newUser= new CreateUser();
                        newUser.setInfoAndCreate(newAccount);
                    } catch (FileNotFoundException e) {
                        throw new RuntimeException(e);
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                } else {
                    // Handle failures
                    // ...

                }
            }
        });






    }

}


