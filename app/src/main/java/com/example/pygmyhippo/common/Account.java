/*
 * TODO: Decide how images are gonna be stored and set the appropriate datatype for profilePicture.
 * TODO: Decide how images are gonna be stored and set the appropriate datatype for facilityPicture.
 *
 * Account Dataclass
 * The structure of this class should be similar to the schema used for Account Documents on
 * firebase. This contains all the info for any user on the app, this includes: admins, organizers,
 * and users. Accounts can have multiple roles, they can switch between them on the profile page of
 * the app. Different roles have different permissions and views associated with them.
 *
 * AccountRole Enum
 * There are only three roles an account on this app can have, one account can have between 1-3
 * AccountRoles assigned to it. Only one role may be active at one time. Roles influence the views
 * and permissions the account has.
 *
 * Facility Dataclass
 * Organizers can have a facility profile attached to their accounts which they may modify. This
 * facility can be viewed by users when they view an event. Organizers do not have to have a
 * facility to create an event, but only account with organizer role can have facilities.
 */

package com.example.pygmyhippo.common;

import android.net.Uri;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.pygmyhippo.MainActivity;
import com.google.android.gms.auth.api.signin.internal.Storage;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.sql.Ref;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


/**
 * Our account class
 * TODO:
 *  - Use a builder for initialization
 *  - connect to the database
 * @author James, Griffin, Jennifer
 * @version 1.1
 */
public class Account {

    private String accountID;
    private String name;
    private String pronouns;
    private String phoneNumber;
    private String emailAddress;
    private String deviceID;
    private Uri profilePicture;
    private String location; // TODO: revaluate once we have a location API
    private boolean receiveNotifications;
    private boolean enableGeolocation;
    private ArrayList<AccountRole> roles;
    private AccountRole currentRole;

    // Firebase Instances
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    FirebaseStorage photoStore = FirebaseStorage.getInstance();
    //Firebase Collection References
    CollectionReference AccountRef = db.collection("All Accounts");
    StorageReference storageRef= photoStore.getReference();

    @Nullable
    private Facility facilityProfile;

    public Account(){

    }

    public Account(
            String accountID,
            String name,
            String pronouns,
            String phoneNumber,
            String emailAddress,
            String deviceID,
            Uri profilePicture,
            String location,
            boolean receiveNotifications,
            boolean enableGeolocation,
            ArrayList<AccountRole> roles,
            AccountRole currentRole,
            @Nullable Facility facilityProfile) {
        this.accountID = accountID;
        this.name = name;
        this.pronouns = pronouns;
        this.phoneNumber = phoneNumber;
        this.emailAddress = emailAddress;
        this.deviceID = deviceID;
        this.profilePicture = profilePicture;
        this.location = location;
        this.receiveNotifications = receiveNotifications;
        this.enableGeolocation = enableGeolocation;
        this.roles = roles;
        this.currentRole = currentRole;
        this.facilityProfile = facilityProfile;
    }

    public Account(String accountID, String name, String pronouns, String phoneNumber, String emailAddress, String deviceID, Uri profilePicture, String location, boolean receiveNotifications, boolean enableGeolocation) {

        this.accountID = accountID;
        this.name = name;
        this.pronouns = pronouns;
        this.phoneNumber = phoneNumber;
        this.emailAddress = emailAddress;
        this.deviceID = deviceID;
        this.profilePicture = profilePicture;
        this.location = location;
        this.receiveNotifications = receiveNotifications;
        this.enableGeolocation = enableGeolocation;

        StorageReference avatarRef = storageRef.child("/"+deviceID+"/avatar/"+profilePicture.getLastPathSegment());
        UploadTask uploadAvatar =  avatarRef.putFile(profilePicture);

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
                    String imageLink = "gs://pygmyhippo-b7892.appspot.com/" + deviceID + "/avatar/" + profilePicture.getLastPathSegment();
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
                    newUser.put("current_role", "entrant");
                    newUser.put("roles", userRoles);
                    AccountRef.document(deviceID).set(newUser);

                } else {
                    // Handle failures
                    // ...



                }
            }
        });

    }

    public static class Facility {
        private String facilityPicture;
        private String name;

        public String getLocation() {
            return location;
        }

        public void setLocation(String location) {
            this.location = location;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getFacilityPicture() {
            return facilityPicture;
        }

        public void setFacilityPicture(String facilityPicture) {
            this.facilityPicture = facilityPicture;
        }

        private String location;
    }

    public enum AccountRole {
        user("user"),
        organiser("organiser"),
        admin("admin");

        public final String value;
        AccountRole(String value) {
            this.value = value;
        }
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }


    public String getAccountID() {
        return accountID;
    }

    public void setAccountID(String accountID) {
        this.accountID = accountID;
    }

    public String getPronouns() {
        return pronouns;
    }

    public void setPronouns(String pronouns) {
        this.pronouns = pronouns;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getEmailAddress() {
        return emailAddress;
    }

    public void setEmailAddress(String emailAddress) {
        this.emailAddress = emailAddress;
    }

    public String getDeviceID() {
        return deviceID;
    }

    public void setDeviceID(String deviceID) {
        this.deviceID = deviceID;
    }

    public Uri getProfilePicture() {
        // FIXME: how are we going to handle images since this would have to return a link to image on firestore
        return profilePicture;
    }

    public void setProfilePicture(Uri profilePicture) {
        // FIXME: How are we going to handle images, for now this can be a link to a image in firestore
        this.profilePicture = profilePicture;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public boolean isReceiveNotifications() {
        return receiveNotifications;
    }

    public void setReceiveNotifications(boolean receiveNotifications) {
        this.receiveNotifications = receiveNotifications;
    }

    public boolean isEnableGeolocation() {
        return enableGeolocation;
    }

    public void setEnableGeolocation(boolean enableGeolocation) {
        this.enableGeolocation = enableGeolocation;
    }

    public ArrayList<AccountRole> getRoles() {
        return roles;
    }

    public void setRoles(ArrayList<AccountRole> roles) {
        this.roles = roles;
    }

    public AccountRole getCurrentRole() {
        return currentRole;
    }

    public void setCurrentRole(AccountRole currentRole) {
        this.currentRole = currentRole;
    }

    @Nullable
    public Facility getFacilityProfile() {
        return facilityProfile;
    }

    public void setFacilityProfile(@Nullable Facility facilityProfile) {
        this.facilityProfile = facilityProfile;
    }
}