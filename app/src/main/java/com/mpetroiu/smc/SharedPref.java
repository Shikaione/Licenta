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
        editor.commit();
    }

    public Boolean loadFavorite(){
        Boolean state = mSharedPref.getBoolean("Favorite",false);
        return state;
    }
}