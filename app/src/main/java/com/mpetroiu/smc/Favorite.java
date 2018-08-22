package com.mpetroiu.smc;

public class Favorite {

    private String mName;
    private String mImageUrl;
    private boolean isFav;

    private Favorite(){

    }

    public Favorite(String mName, String mImageUrl, boolean isFav) {
        if(mName.trim().equals("")){
            mName = "No Name";
        }
        this.mName = mName;
        this.mImageUrl = mImageUrl;
        this.isFav = isFav;
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

    public boolean isFav() {
        return isFav;
    }

    public void setFav(boolean fav) {
        isFav = fav;
    }
}
