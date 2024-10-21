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
    private String accountID;
    private String firstName;
    private String lastName;
    private String pronouns;
    private String phoneNumber;
    private String emailAddress;
    private String deviceID;
    private String profilePicture;
    private boolean receiveNotifications;

    private ArrayList<AccountRole> roles;

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

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
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

    public String getProfilePicture() {
        // FIXME: how are we going to handle images since this would have to return a link to image on firestore
        return profilePicture;
    }

    public void setProfilePicture(String profilePicture) {
        // FIXME: How are we going to handle images, for now this can be a link to a image in firestore
        this.profilePicture = profilePicture;
    }

    public boolean isReceiveNotifications() {
        return receiveNotifications;
    }

    public void setReceiveNotifications(boolean receiveNotifications) {
        this.receiveNotifications = receiveNotifications;
    }

    public ArrayList<AccountRole> getRoles() {
        return roles;
    }

    public void setRoles(ArrayList<AccountRole> roles) {
        this.roles = roles;
    }

    @Nullable
    public Facility getFacilityProfile() {
        return facilityProfile;
    }

    public void setFacilityProfile(@Nullable Facility facilityProfile) {
        this.facilityProfile = facilityProfile;
    }
}
