package com.example.pygmyhippo.common;
/*
The Image data class
Purposes:
    -Holds the attributes we need to eventually process images
Issues:
    - Need to implement image handling in the project
 */

/**
 * Image Data class
 */
public class Image {
    private String url;
    private String ID;
    private ImageType type;

    public enum ImageType {
        Account,
        Event,
    }

    public Image(String url, String ID, ImageType type) {
        this.url = url;
        this.ID = ID;
        this.type = type;
    }

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