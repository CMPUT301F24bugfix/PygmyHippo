package com.example.pygmyhippo.common;

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

    public String getID() {
        return ID;
    }

    public ImageType getType() {
        return type;
    }

    public String getUrl() {
        return url;
    }

    public void setID(String ID) {
        this.ID = ID;
    }

    public void setType(ImageType type) {
        this.type = type;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}