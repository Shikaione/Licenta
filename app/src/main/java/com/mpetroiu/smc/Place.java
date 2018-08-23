package com.mpetroiu.smc;

public class Place {
    private String mOwner;
    private String mEmail;
    private String mPhone;
    private String mLocation;
    private String mLocationType;
    private String mAddress;
    private String key;

    public Place(){
    }

    public Place(String mOwner, String mEmail, String mPhone, String mLocation, String mLocationType, String mAddress) {
        this.mOwner = mOwner;
        this.mEmail = mEmail;
        this.mPhone = mPhone;
        this.mLocation = mLocation;
        this.mLocationType = mLocationType;
        this.mAddress = mAddress;
    }

    public String getOwner() {
        return mOwner;
    }

    public void setOwner(String mOwner) {
        this.mOwner = mOwner;
    }

    public String getEmail() {
        return mEmail;
    }

    public void setEmail(String mEmail) {
        this.mEmail = mEmail;
    }

    public String getPhone() {
        return mPhone;
    }

    public void setPhone(String mPhone) {
        this.mPhone = mPhone;
    }

    public String getLocation() {
        return mLocation;
    }

    public void setLocation(String mLocation) {
        this.mLocation = mLocation;
    }

    public String getLocationType() {
        return mLocationType;
    }

    public void setLocationType(String mLocationType) {
        this.mLocationType = mLocationType;
    }

    public String getAddress() {
        return mAddress;
    }

    public void setAddress(String mAddress) {
        this.mAddress = mAddress;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }
}
