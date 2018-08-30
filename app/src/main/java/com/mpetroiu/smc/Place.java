package com.mpetroiu.smc;

import com.google.firebase.database.Exclude;

class Place {

    private String Owner;
    private String Phone;
    private String Location;
    private String LocType;
    private String Email;
    private String Address;
    private String Key;

    public Place(){

    }

    public Place(String owner, String phone, String location, String locType, String email, String address, String key) {
        Owner = owner;
        Phone = phone;
        Location = location;
        LocType = locType;
        Email = email;
        Address = address;
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

    public String getLocType() {
        return LocType;
    }

    public void setLocType(String locType) {
        LocType = locType;
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

    @Exclude
    public String getKey() {
        return Key;
    }

    @Exclude
    public void setKey(String key) {
        Key = key;
    }
}
