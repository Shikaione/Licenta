package com.mpetroiu.smc;

import android.content.Context;
import android.content.SharedPreferences;

public class SharedPref{

    SharedPreferences mSharedPref;
    public SharedPref(Context context){
        mSharedPref = context.getSharedPreferences("filename", Context.MODE_PRIVATE);
    }

    public  void SetFavorite(Boolean state){
        SharedPreferences.Editor editor = mSharedPref.edit();
        editor.putBoolean("Favorite",state);
        editor.putBoolean("Subscribed",state);
        editor.apply();
    }

    public void setSubscribed(Boolean state){
        SharedPreferences.Editor editor = mSharedPref.edit();
        editor.putBoolean("Subscribed",state);
        editor.apply();
    }

    public Boolean loadFavorite(){
        Boolean state = mSharedPref.getBoolean("Favorite",false);
        return state;
    }

    public boolean loadSubscribed(){
        Boolean state = mSharedPref.getBoolean("Subscribed", false);
        return state;
    }
}