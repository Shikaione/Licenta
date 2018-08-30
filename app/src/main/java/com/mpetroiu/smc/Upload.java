package com.mpetroiu.smc;

public class Upload {

    private String Location;
    private String Thumbnail;
    private String Key;

    public Upload() {
    }

    public Upload(String location, String thumbnail, String key) {
        Location = location;
        Thumbnail = thumbnail;
        Key = key;
    }

    public String getLocation() {
        return Location;
    }

    public void setLocation(String location) {
        Location = location;
    }

    public String getThumbnail() {
        return Thumbnail;
    }

    public void setThumbnail(String thumbnail) {
        Thumbnail = thumbnail;
    }

    public String getKey() {
        return Key;
    }

    public void setKey(String key) {
        Key = key;
    }
}
