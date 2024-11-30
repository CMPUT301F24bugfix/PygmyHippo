package com.example.pygmyhippo.common;
/*
This is the Account data class
Purposes:
    - Model an account for everyone (user, organiser, admin)
    - Hold the attributes to get or set account info
    - Allow different permissions depending on the role (right now this is done for admin)
Issues:
    - Constructors can get large and hard to read (so could look into builders for better initialization)
    - Nothing is done with location yet (This class doesn't need/use it)
 */
import android.os.Build;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.ArrayList;


/**
 * TODO:
 *  - Use a builder for initialization
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
    private String location; // TODO: Remove
    private boolean receiveNotifications;
    private boolean enableGeolocation;

    private ArrayList<AccountRole> roles;
    private AccountRole currentRole; // TODO remove

    @Nullable
    private Facility facilityProfile;

    /**
     * Constructor for account without any parameters needed
     */
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

        this.facilityProfile = new Facility();
    }

    /**
     * Constructor with all params
     * @param accountID
     * @param name
     * @param pronouns
     * @param phoneNumber
     * @param emailAddress
     * @param deviceID
     * @param profilePicture
     * @param location
     * @param receiveNotifications
     * @param enableGeolocation
     * @param roles
     * @param currentRole
     * @param facilityProfile
     */
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

    /**
     * Constructor with specific params
     * @param accountID
     * @param name
     * @param phoneNumber
     * @param emailAddress
     * @param location
     */
    public Account(String accountID, String name, String phoneNumber, String emailAddress, String location) {
        this.accountID = accountID;
        this.name = name;
        this.phoneNumber = phoneNumber;
        this.emailAddress = emailAddress;
        this.location = location;
    }

    /**
     * Constructor with specific Params
     * @param accountID
     * @param name
     * @param pronouns
     * @param phoneNumber
     * @param emailAddress
     * @param deviceID
     * @param profilePicture
     * @param location
     * @param receiveNotifications
     * @param enableGeolocation
     */
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

    /**
     * Makes the account Parcelable
     * @param in
     */
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

        ArrayList<String> parcelRoles = new ArrayList<>();
        in.readStringList(parcelRoles);

        roles = new ArrayList<AccountRole>();
        parcelRoles.forEach(roleString -> {
            switch (roleString) {
                case "user":
                    roles.add(AccountRole.user);
                    break;
                case "organiser":
                    roles.add(AccountRole.organiser);
                    break;
                case "admin":
                    roles.add(AccountRole.admin);
                default:
                    Log.d("Account", String.format("Unknown roleString %s", roleString));
            }
        });

        in.readString();
        currentRole = AccountRole.user; // TODO Remove
        facilityProfile = in.readParcelable(Facility.class.getClassLoader());
    }

    /**
     * Create Account from Parcel
     */
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
        dest.writeByte((byte) (receiveNotifications ? 1 : 0));
        dest.writeByte((byte) (enableGeolocation ? 1 : 0));

        ArrayList<String> roleStrings = new ArrayList<>();
        roles.forEach(role -> roleStrings.add(role.value));
        dest.writeStringList(roleStrings);

        dest.writeString(currentRole.value);

        dest.writeParcelable(facilityProfile, flags);
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

    /**
     * Returns the account name
     * @return the name of the account
     */
    public String getName() {
        return name;
    }

    /**
     * Changes the account name
     * @param name The name we want to change to
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Will return the Id of the account
     * @return accountID
     */
    public String getAccountID() {
        return accountID;
    }

    /**
     * Will change the ID of the account
     * @param accountID The ID we want to change to
     */
    public void setAccountID(String accountID) {
        this.accountID = accountID;
    }

    /**
     * Will return the Pronouns of the account
     * @return pronouns
     */
    public String getPronouns() {
        return pronouns;
    }

    /**
     * Changes the pronouns of the account
     * @param pronouns The pronouns we want to change to
     */
    public void setPronouns(String pronouns) {
        this.pronouns = pronouns;
    }

    /**
     * Will return the Account's phone number
     * @return phoneNumber
     */
    public String getPhoneNumber() {
        return phoneNumber;
    }

    /**
     * Will set the Account's phone number
     * @param phoneNumber The phone number we want to change to
     */
    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    /**
     * Will return the Account's email
     * @return emailAddress
     */
    public String getEmailAddress() {
        return emailAddress;
    }

    /**
     * Changes the account's email address
     * @param emailAddress What we want to change the email to
     */
    public void setEmailAddress(String emailAddress) {
        this.emailAddress = emailAddress;
    }

    /**
     * Returns the Account's device ID
     * This is the device ID that will get recognized so the user doesn't have to sign in
     * @return deviceID
     */
    public String getDeviceID() {
        return deviceID;
    }

    /**
     * Sets the deviceID of the account
     * @param deviceID The ID we want to change to
     */
    public void setDeviceID(String deviceID) {
        this.deviceID = deviceID;
    }

    /**
     * Will return the profile picture of the account
     * TODO: Figure out how to handle images
     * @return profilePicture
     */
    public String getProfilePicture() {
        // FIXME: how are we going to handle images since this would have to return a link to image on firestore
        return profilePicture;
    }

    /**
     * Will set the profile picture of the account
     * @param profilePicture The profile picture we want to set for the account
     */
    public void setProfilePicture(String profilePicture) {
        // FIXME: How are we going to handle images, for now this can be a link to a image in firestore
        this.profilePicture = profilePicture;
    }

    /**
     * Will return the location of the user who uses the account
     * @return location
     */
    public String getLocation() {
        return location;
    }

    /**
     * Will change the location of the account
     * @param location The location we want to change it to
     */
    public void setLocation(String location) {
        this.location = location;
    }

    /**
     * Will return if the user wants notifications or not
     * @return receiveNotifications
     */
    public boolean isReceiveNotifications() {
        return receiveNotifications;
    }

    /**
     * Will change the account's notification preference
     * @param receiveNotifications The boolean indicating true for wanting notifications, and false for not
     */
    public void setReceiveNotifications(boolean receiveNotifications) {
        this.receiveNotifications = receiveNotifications;
    }

    /**
     * Will return the boolean value indicating if geolocation is endabled or not
     * @return true for enabled geolocation and false for disabled geolocation
     */
    public boolean isEnableGeolocation() {
        return enableGeolocation;
    }

    /**
     * Wiil set the boolean value indicating if the account has or doesn't have geolocation enabled
     * @param enableGeolocation true for enabling and false for disabling
     */
    public void setEnableGeolocation(boolean enableGeolocation) {
        this.enableGeolocation = enableGeolocation;
    }

    /**
     * This will return the list of roles the account has. ex: If the user is both an admin and organizer then the list would
     * contain those roles
     * @return The list of the account's current roles
     */
    public ArrayList<AccountRole> getRoles() {
        return roles;
    }

    /**
     * This will change the list of the current permission roles that the account has
     * @param roles The new list of roles for the account
     */
    public void setRoles(ArrayList<AccountRole> roles) {
        this.roles = roles;
    }

    public AccountRole getCurrentRole() {
        return currentRole;
    }

    public void setCurrentRole(AccountRole currentRole) {
        this.currentRole = currentRole;
    }

    /**
     * This will return the profile of the facility
     * Only accounts with organiser permissions will have facility profiles
     * @return facilityProfile
     */
    @Nullable
    public Facility getFacilityProfile() {
        return facilityProfile;
    }

    /**
     * This will set the profile of the facility for the account if it has an organiser role
     * @param facilityProfile The profile we want to set to
     */
    public void setFacilityProfile(@Nullable Facility facilityProfile) {
        this.facilityProfile = facilityProfile;
    }
}
