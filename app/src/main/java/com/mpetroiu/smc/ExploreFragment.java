package com.mpetroiu.smc;


import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class ExploreFragment extends Fragment {

    private final static String TAG = "ExploreFragment";

    private List<Place> mPlaces;
    private DatabaseReference mDatabase;
    private String key;
    private TextView mPlaceName;

    public ExploreFragment() {
    }
    
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_explore, container, false);

        mDatabase = FirebaseDatabase.getInstance().getReference().child("Places");

        mPlaceName = v.findViewById(R.id.placeName);

        if (getArguments() != null) {
            key  = getArguments().getString("key");
            Log.d(TAG, "this is key : " + key);

        }

        /*DatabaseReference placeRef = mDatabase.child(key);

        placeRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Place place = dataSnapshot.getValue(Place.class);

               if (place != null) {
                   mPlaceName.setText(place.getLocation());
                   Log.d(TAG, "Place name : " + mPlaceName.toString());
               }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });*/

        return v;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


    }

    private void showData() {

    }
}

