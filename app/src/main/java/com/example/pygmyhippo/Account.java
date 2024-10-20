package com.example.pygmyhippo;

/*
    For testing only
 */

import androidx.annotation.Nullable;

// Account dataclass.
// The structure of this class should be similar to the schema used for Account Documents
// on firebase.
public class Account {
    String accountID;
    String firstName;
    String lastName;
    String pronouns;
    String deviceID;
    Integer phoneNumber;
    // FIXME: unsure of datatype
    // Might change to eventPosterID depending on how we decide to handle images.
    String profilePicture;
    boolean receiveNotifications;

    AccountRole role;

    @Nullable
    Facility facilityProfile;

    public static class Facility {
        // FIXME: unsure of datatype
        // Might change to eventPosterID depending on how we decide to handle images.
        String facilityPicture;
        String name;
        String location;
    }

    public enum AccountRole {
        user,
        organizer,
        admin;
    }
}