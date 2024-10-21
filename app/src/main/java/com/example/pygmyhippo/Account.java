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

package com.example.pygmyhippo;

import androidx.annotation.Nullable;

import java.util.ArrayList;


// accountID is the document ID.
public class Account {
    String accountID;
    String firstName;
    String lastName;
    String pronouns;
    String phoneNumber;
    String deviceID;
    String profilePicture;
    boolean receiveNotifications;

    ArrayList<AccountRole> roles;

    @Nullable
    Facility facilityProfile;

    public static class Facility {
        String facilityPicture;
        String name;
        String location;
    }

    public enum AccountRole {
        user("user"),
        organizer("organizer"),
        admin("admin");

        public final String value;
        AccountRole(String value) {
            this.value = value;
        }
    }
}
