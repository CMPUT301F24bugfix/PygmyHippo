package com.example.pygmyhippo.common;

import android.os.Build;
import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.ArrayList;


/**
 * TODO: Decide how images are gonna be stored and set the appropriate datatype for profilePicture.
 * TODO:
 *  - Use a builder for initialization
 *  - connect to the database
 * Account Dataclass
 * The structure of this class should be similar to the schema used for Account Documents on
 * firebase. This contains all the info for any user on the app, this includes: admins, organizers,
 * and users. Accounts can have multiple roles, they can switch between them on the profile page of
 * the app. Different roles have different permissions and views associated with them.

 * @author James, Griffin
 */
public class Account implements Parcelable {

    private String accountID;
    private String name;
    private String pronouns;
    private String phoneNumber;
    private String emailAddress;
    private String deviceID;
    private String profilePicture;
    private String location; // TODO: revaluate once we have a location API
    private boolean receiveNotifications;
    private boolean enableGeolocation;

    private ArrayList<AccountRole> roles;
    private AccountRole currentRole;

    @Nullable
    private Facility facilityProfile;

    public Account(){
        this.accountID = null;
        this.name = "";
        this.pronouns = "";
        this.phoneNumber = "";
        this.emailAddress = "";
        this.deviceID = null;
        this.profilePicture = "";
        this.location = "";
        this.receiveNotifications = false;
        this.enableGeolocation = false;

        this.roles = new ArrayList<>();
        this.roles.add(AccountRole.user);

        this.facilityProfile = null;
    }

    public Account(
            String accountID,
            String name,
            String pronouns,
            String phoneNumber,
            String emailAddress,
            String deviceID,
            String profilePicture,
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

    public Account(String accountID, String name, String pronouns, String phoneNumber, String emailAddress, String deviceID, String profilePicture, String location, boolean receiveNotifications, boolean enableGeolocation) {
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
    }

    protected Account(Parcel in) {
        accountID = in.readString();
        name = in.readString();
        pronouns = in.readString();
        phoneNumber = in.readString();
        emailAddress = in.readString();
        deviceID = in.readString();
        profilePicture = in.readString();
        location = in.readString();
        receiveNotifications = in.readByte() != 0;
        enableGeolocation = in.readByte() != 0;
        facilityProfile = in.readParcelable(Facility.class.getClassLoader());
    }

    public static final Creator<Account> CREATOR = new Creator<Account>() {
        @Override
        public Account createFromParcel(Parcel in) {
            return new Account(in);
        }

        @Override
        public Account[] newArray(int size) {
            return new Account[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(@NonNull Parcel dest, int flags) {
        dest.writeString(accountID);
        dest.writeString(name);
        dest.writeString(pronouns);
        dest.writeString(phoneNumber);
        dest.writeString(emailAddress);
        dest.writeString(deviceID);
        dest.writeString(profilePicture);
        dest.writeString(location);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            dest.writeBoolean(receiveNotifications);
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            dest.writeBoolean(enableGeolocation);
        }

        ArrayList<String> roleStrings = new ArrayList<>();
        for (AccountRole role : this.roles) {
            roleStrings.add(role.value);
        }
        dest.writeStringList(roleStrings);
        dest.writeParcelable(facilityProfile, 0);
    }

    /*
     * AccountRole Enum
     * There are only three roles an account on this app can have, one account can have between 1-3
     * AccountRoles assigned to it. Only one role may be active at one time. Roles influence the views
     * and permissions the account has.
     */
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

    public String getProfilePicture() {
        // FIXME: how are we going to handle images since this would have to return a link to image on firestore
        return profilePicture;
    }

    public void setProfilePicture(String profilePicture) {
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
