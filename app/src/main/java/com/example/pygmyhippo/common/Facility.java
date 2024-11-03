package com.example.pygmyhippo.common;

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
}