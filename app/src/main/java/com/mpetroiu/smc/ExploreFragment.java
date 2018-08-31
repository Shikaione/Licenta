package com.mpetroiu.smc;


import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.firebase.auth.FirebaseUser;
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
    private static final int ERROR_DIALOG_REQUEST = 9001;

    private DatabaseReference mDataRef;
    private Context mContext;

    private String key, Address;
    private TextView mPlaceName;
    private ImageView mMapLocation;

    public ExploreFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_explore, container, false);

        mContext = v.getContext();

        mDataRef = FirebaseDatabase.getInstance().getReference().child("Places");

        mPlaceName = v.findViewById(R.id.placeName);

        if (getArguments() != null) {
            key  = getArguments().getString("key");
            Log.d(TAG, "this is key : " + key);

        }

        showData();

        mMapLocation = v.findViewById(R.id.mapLocation);

        if(isServicesOK()){
            init();
        }

        return v;
    }

    private void init() {
        mMapLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AppCompatActivity activity = (AppCompatActivity) mContext;
                MapsFragment mapsFragment = new MapsFragment();
                Bundle args = new Bundle();
                args.putString("address", Address);
                mapsFragment.setArguments(args);
                activity.getSupportFragmentManager().beginTransaction().replace(R.id.main_frame,
                        mapsFragment).addToBackStack(null).commit();

                Log.e(TAG, "Address is :" + Address);
            }
        });
    }

    private void showData() {
        DatabaseReference details = mDataRef.child(key);

        details.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Place placeInfo = dataSnapshot.getValue(Place.class);

                if(placeInfo != null){
                    mPlaceName.setText(placeInfo.getLocation());
                    Address = placeInfo.getAddress();
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public boolean isServicesOK(){
        Log.d(TAG, "isServicesOK: checking google services version");

        int available = GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(getContext());

        if(available == ConnectionResult.SUCCESS){
            Log.d(TAG, "isServicesOK: google services are working");
            return true;
        }else if(GoogleApiAvailability.getInstance().isUserResolvableError(available)){
            Log.d(TAG,"isServicesOK: error occured but we can fix it");
            Dialog dialog = GoogleApiAvailability.getInstance().getErrorDialog(getActivity(), available, ERROR_DIALOG_REQUEST);
        }else{
            Toast.makeText(getContext(), "You can't make map request", Toast.LENGTH_SHORT).show();
        }
        return false;
    }
}

