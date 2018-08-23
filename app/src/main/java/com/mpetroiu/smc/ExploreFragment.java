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
        DatabaseReference mDataRef = FirebaseDatabase.getInstance().getReferenceFromUrl("https://licenta-smc.firebaseio.com/Places/Place");
        String key = mDataRef.push().getRef().toString();
        Log.d(TAG,"isthisKey: "+ " " + key);
        mDataRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    Place placeInfo = new Place();

                    placeInfo.setOwner(ds.child("name").getValue(Place.class).getOwner());
                    placeInfo.setEmail(ds.child("email").getValue(Place.class).getEmail());
                    placeInfo.setPhone(ds.child("phone").getValue(Place.class).getPhone());
                    placeInfo.setLocation(ds.child("location").getValue(Place.class).getLocation());
                    placeInfo.setLocationType(ds.child("type").getValue(Place.class).getLocationType());
                    placeInfo.setAddress(ds.child("address").getValue(Place.class).getAddress());

                    ArrayList<String> array = new ArrayList<>();
                    array.add(placeInfo.getOwner());
                    array.add(placeInfo.getEmail());
                    array.add(placeInfo.getPhone());
                    array.add(placeInfo.getLocation());
                    array.add(placeInfo.getLocationType());
                    array.add(placeInfo.getAddress());

                    Log.d(TAG,"owner" + placeInfo.getOwner());

                    ArrayAdapter adapter = new ArrayAdapter(getContext(), android.R.layout.simple_list_item_1, array);
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

