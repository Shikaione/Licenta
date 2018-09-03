package com.mpetroiu.smc;

import com.google.firebase.database.Exclude;

class Place {

    private String Owner;
    private String Phone;
    private String Location;
    private String Type;
    private String Email;
    private String Address;
    private String Thumbnail;
    private String Key;

    public Place(){

    }

    public Place(String owner,
                 String phone,
                 String location,
                 String type,
                 String email,
                 String address,
                 String thumbnail,
                 String key) {
        Owner = owner;
        Phone = phone;
        Location = location;
        Type = type;
        Email = email;
        Address = address;
        Thumbnail = thumbnail;
        Key = key;
    }

    public String getOwner() {
        return Owner;
    }

    public void setOwner(String owner) {
        Owner = owner;
    }

    public String getPhone() {
        return Phone;
    }

    public void setPhone(String phone) {
        Phone = phone;
    }

    public String getLocation() {
        return Location;
    }

    public void setLocation(String location) {
        Location = location;
    }

    public String getType() {
        return Type;
    }

    public void setType(String type) {
        Type = type;
    }

    public String getEmail() {
        return Email;
    }

    public void setEmail(String email) {
        Email = email;
    }

    public String getAddress() {
        return Address;
    }

    public void setAddress(String address) {
        Address = address;
    }

    public String getThumbnail() {
        return Thumbnail;
    }

    public void setThumbnail(String thumbnail) {
        Thumbnail = thumbnail;
    }

    @Exclude
    public String getKey() {
        return Key;
    }

    @Exclude
    public void setKey(String key) {
        Key = key;
    }
}
