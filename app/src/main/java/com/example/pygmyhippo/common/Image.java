package com.example.pygmyhippo.common;
/*
The Image data class
Purposes:
    -Holds the attributes we need to eventually process images
Issues:
    - Need to implement image handling in the project
 */

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Image Data class
 */
public class Image implements Parcelable {
    private String url;
    private String ID;
    private ImageType type;

    public enum ImageType {
        Account,
        Event;
        public static ImageType fromOrdinal(int ordinal) {
            return values()[ordinal];
        }
    }

    // Constructor
    public Image(String url, String ID, ImageType type) {
        this.url = url;
        this.ID = ID;
        this.type = type;
    }

    public Image() {}

    // Parcelable implementation
    protected Image(Parcel in) {
        url = in.readString();
        ID = in.readString();
        type = ImageType.fromOrdinal(in.readInt());
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(url);
        dest.writeString(ID);
        dest.writeInt(type.ordinal());
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<Image> CREATOR = new Creator<Image>() {
        @Override
        public Image createFromParcel(Parcel in) {
            return new Image(in);
        }

        @Override
        public Image[] newArray(int size) {
            return new Image[size];
        }
    };

    /**
     * Returns the Image ID
     * @return ID
     */
    public String getID() {
        return ID;
    }

    /**
     * Returns the type of imaage it is
     * @return type
     */
    public ImageType getType() {
        return type;
    }

    /**
     * Returns the Url of the image
     * @return url
     */
    public String getUrl() {
        return url;
    }

    /**
     * Sets the ID of the image
     * @param ID The ID of the Image
     */
    public void setID(String ID) {
        this.ID = ID;
    }

    /**
     * Updates the image type
     * @param type The new type of the image
     */
    public void setType(ImageType type) {
        this.type = type;
    }

    /**
     * Updates the url of the image
     * @param url The new image url
     */
    public void setUrl(String url) {
        this.url = url;
    }
}