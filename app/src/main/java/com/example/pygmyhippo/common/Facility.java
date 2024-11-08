package com.example.pygmyhippo.common;

/*
The Data class for the facility that an organiser represents
Purposes:
    - Holds the data to represent the facility profile
    - Is contained inside the Account class, never outside of it
Issues:
    - Image handling
 */
import android.os.Parcel;
import android.os.Parcelable;

/**
 * TODO: Decide how images are gonna be stored and set the appropriate datatype for facilityPicture.
 *
 * Facility Dataclass
 * Organizers can have a facility profile attached to their accounts which they may modify. This
 * facility can be viewed by users when they view an event. Organizers do not have to have a
 * facility to create an event, but only account with organizer role can have facilities.
 */
public class Facility implements Parcelable {
    private String facilityPicture;
    private String name;
    private String location;

    public Facility() {}
    public Facility(String facilityPicture, String name, String location) {
        this.facilityPicture = facilityPicture;
        this.name = name;
        this.location = location;
    }

    protected Facility(Parcel in) {
        facilityPicture = in.readString();
        name = in.readString();
        location = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(facilityPicture);
        dest.writeString(name);
        dest.writeString(location);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<Facility> CREATOR = new Creator<Facility>() {
        @Override
        public Facility createFromParcel(Parcel in) {
            return new Facility(in);
        }

        @Override
        public Facility[] newArray(int size) {
            return new Facility[size];
        }
    };

    /**
     * This method returns the facility's location
     * @return The facility's location
     */
    public String getLocation() {
        return location;
    }

    /**
     * This method updates the facility's location
     * @param location The new location of the facility
     */
    public void setLocation(String location) {
        this.location = location;
    }

    /**
     * This method returns the facility's name
     * @return name
     */
    public String getName() {
        return name;
    }

    /**
     * This method updates the facility's name
     * @param name The new name
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * This method returns the facility's profile picture
     * TODO: Figure out image handling
     * @return facilityPicture
     */
    public String getFacilityPicture() {
        return facilityPicture;
    }

    /**
     * This method updates the facility's picture
     * TODO: Figure out image handling
     * @param facilityPicture The new picture
     */
    public void setFacilityPicture(String facilityPicture) {
        this.facilityPicture = facilityPicture;
    }
}