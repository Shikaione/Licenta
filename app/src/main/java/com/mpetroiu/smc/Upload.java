package com.mpetroiu.smc;

public class Upload {

    private String mName;
    private String mImageUrl;
    private String key;

    public Upload() {
    }

    public Upload(String mName, String mImageUrl, String isFav) {
        if (mName.trim().equals("")) {
            mName = "No Name";
        }

        this.mName = mName;
        this.mImageUrl = mImageUrl;
    }

    public String getName() {
        return mName;
    }

    public void setName(String mName) {
        this.mName = mName;
    }

    public String getImageUrl() {
        return mImageUrl;
    }

    public void setImageUrl(String mImageUrl) {
        this.mImageUrl = mImageUrl;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }
}
