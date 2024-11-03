package com.example.pygmyhippo.common.UserProcessing;

import android.net.Uri;

import androidx.annotation.NonNull;

import com.example.pygmyhippo.common.Account;
import com.example.pygmyhippo.common.Callback.Worker;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.example.pygmyhippo.common.Callback.MyCallback;
import java.util.ArrayList;
import java.util.HashMap;

public class CreateUser {
    private String accountID;
    private String name;
    private String pronouns;
    private String phoneNumber;
    private String emailAddress;
    private String deviceID;
    private String profilePicture;
    private Uri pictureUri;
    private String location;
    private boolean receiveNotifications;
    private boolean enableGeolocation;
    private ArrayList<Account.AccountRole> roles;
    private Account.AccountRole currentRole;
   // private DBConnector test = new DBConnector();

    // Firebase Instances
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    FirebaseStorage photoStore = FirebaseStorage.getInstance();
    //Firebase Collection References
    CollectionReference AccountRef = db.collection("All Accounts");
    StorageReference storageRef= photoStore.getReference();

    public void setInfoAndCreate(Account user) throws Exception {
        Worker createWorker = new Worker();
        createWorker.callback = new Create();
        this.accountID = user.getAccountID();
        this.name = user.getName();
        this.pronouns = user.getPronouns();
        this.emailAddress = user.getEmailAddress();
        this.phoneNumber = user.getPhoneNumber();
        this.deviceID = user.getDeviceID();
        this.profilePicture = user.getProfilePicture();
        this.location = user.getLocation();
        this.receiveNotifications = user.isReceiveNotifications();
        this.enableGeolocation = user.isEnableGeolocation();
        this.pictureUri = user.getPictureUri();
        // write logic for current role and roles
        createWorker.onEvent();
    }


    public class Create implements MyCallback {
        @Override
        public void callbackCall() throws Exception {
            if (phoneNumber == null) phoneNumber = "";
         if (profilePicture == null) {
            String imageLink = "gs://pygmyhippo-b7892.appspot.com/" + deviceID + "/avatar/" + pictureUri.getLastPathSegment();
            // user roles
            ArrayList<String> userRoles = new ArrayList<>();
            userRoles.add("user");

            // Add the user to the Firestore collection
            HashMap<String, Object> newUser = new HashMap<>();
            newUser.put("name", name);
            newUser.put("pronouns", pronouns);
            newUser.put("phone_number", phoneNumber);
            newUser.put("email_address", emailAddress);
            newUser.put("device_id", deviceID);
            newUser.put("location", location);
            newUser.put("receive_notifications", receiveNotifications);
            newUser.put("enable_geolocation", enableGeolocation);
            newUser.put("profile_photo", pictureUri.toString()); // for displaying photo
            newUser.put("profile_photo_storage_link", imageLink); // for performing db queries on photo
            newUser.put("current_role", "entrant");
            newUser.put("roles", userRoles);
            AccountRef.document(deviceID).set(newUser);

            System.out.println("User Created!");

        } else {
            Uri profilePicUri = Uri.parse(profilePicture);
            StorageReference avatarRef = storageRef.child("/" + deviceID + "/avatar/" + profilePicUri.getLastPathSegment());
            UploadTask uploadAvatar;
            uploadAvatar = avatarRef.putFile(profilePicUri);


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
                        // direct link for a profile photo
                        String imageLink = "gs://pygmyhippo-b7892.appspot.com/" + deviceID + "/avatar/" + profilePicUri.getLastPathSegment();
                        // user roles
                        ArrayList<String> userRoles = new ArrayList<>();
                        userRoles.add("entrant");

                        // Add the user to the Firestore collection
                        HashMap<String, Object> newUser = new HashMap<>();
                        newUser.put("name", name);
                        newUser.put("pronouns", pronouns);
                        newUser.put("phone_number", phoneNumber);
                        newUser.put("email_address", emailAddress);
                        newUser.put("device_id", deviceID);
                        newUser.put("location", location);
                        newUser.put("receive_notifications", receiveNotifications);
                        newUser.put("enable_geolocation", enableGeolocation);
                        newUser.put("profile_photo", downloadUri); // for displaying photo
                        newUser.put("profile_photo_storage_link", imageLink); // for performing db queries on photo
                        newUser.put("current_role", "user");
                        newUser.put("roles", userRoles);
                        AccountRef.document(deviceID).set(newUser);

                        System.out.println("User Created!");

                    } else {
                        // Handle failures
                        System.out.println("User Not Created! :(");
                        // ...

                    }
                }
            });

        }



        }
    }




}


/*  // Call this to populate the class before calling getters
    public void getAccount(String userId) {

        db.collection("All Accounts")
                        .whereEqualTo("device_id", userId)
                        .addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot querySnapshots, @Nullable FirebaseFirestoreException error) {
                if (error != null) {
                    Log.e("Firestore", error.toString());
                    return;
                }
                if (querySnapshots != null) {
                    for (QueryDocumentSnapshot doc: querySnapshots) {

                        accountID = doc.getString("account_id");
                       deviceID = doc.getId();
                       name = doc.getString("name");
                       emailAddress = doc.getString("email_address");
                       phoneNumber = doc.getString("phone_number");
                       pronouns = doc.getString("pronouns");
                       location = doc.getString("location");
                       profilePicture = doc.getString("profile_photo");
                       receiveNotifications = doc.getBoolean("receive_notifications");
                       enableGeolocation = doc.getBoolean("enable_geolocation");
                       String currentRoleString = doc.getString("current_user");
                        // Write logic to retrieve roles and AccountRole class

                    }
                }
            }
        });
    }


 */
