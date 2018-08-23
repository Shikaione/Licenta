package com.mpetroiu.smc;


import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import static android.support.constraint.Constraints.TAG;

public class ExploreFragment extends Fragment {

    private final static String TAG = "ExploreFragment";
    private ListView mList;

    private List<Place> mPlaces;


    public ExploreFragment() {
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_explore, container, false);

        mList = v.findViewById(R.id.exploreList);

        mPlaces = new ArrayList<>();

        DatabaseReference mDataRef = FirebaseDatabase.getInstance().getReference().child("Places").child("Place");

        mDataRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot ds : dataSnapshot.getChildren()) {

                    Upload upload = ds.getValue(Upload.class);
                    upload.setKey(ds.getKey());

                    Log.e(TAG, "keyIS: " + upload.getKey());


                    Place place = ds.getValue(Place.class);
                    place.setKey(ds.getKey());

                    Log.e(TAG, "keyIS: " + place.getKey());

                    mPlaces.add(place);


                    ArrayAdapter adapter = new ArrayAdapter(getContext(), android.R.layout.simple_list_item_1, mPlaces);
                    mList.setAdapter(adapter);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        return v;
    }

    private void showData(DataSnapshot dataSnapshot) {

    }
}

