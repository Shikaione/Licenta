package com.mpetroiu.smc;


import android.Manifest;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.onesignal.OneSignal;

import java.util.ArrayList;


public class ExploreFragment extends Fragment {

    private final static String TAG = "ExploreFragment";
    private static final int ERROR_DIALOG_REQUEST = 9001;
    private static final int REQUEST_PHONE_CALL = 1235;

    private DatabaseReference mDataRef;

    private Context mContext;

    private String key, Address, Phone, mTopic;

    private TextView mPlaceName, mType;

    private ImageView mMapLocation, mMapDirections, mPhone;

    private Button mSubscribe, mUnsubscribe;

    private ArrayList<String> thumbnailList;

    private ViewPager viewPager;
    private ViewPagerAdapter adapter;


    public ExploreFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_explore, container, false);

        mContext = v.getContext();

        mDataRef = FirebaseDatabase.getInstance().getReference().child("Places");

        mPlaceName = v.findViewById(R.id.placeName);
        mType = v.findViewById(R.id.txPlaceType);

        thumbnailList = new ArrayList<>();

        viewPager = v.findViewById(R.id.view_pager);
        adapter = new ViewPagerAdapter(mContext, thumbnailList);
        viewPager.setAdapter(adapter);

        if (getArguments() != null) {
            key = getArguments().getString("key");
            Log.d(TAG, "this is key : " + key);

        }

        showData();

        mMapLocation = v.findViewById(R.id.mapLocation);
        mMapDirections = v.findViewById(R.id.mapDirections);

        if (isServicesOK()) {
            init();
        }

        mPhone = v.findViewById(R.id.callPhone);

        if (ContextCompat.checkSelfPermission(mContext, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.CALL_PHONE}, REQUEST_PHONE_CALL);
            callPlace();
        } else {
            callPlace();
        }


        mSubscribe = v.findViewById(R.id.subscribe);
        mUnsubscribe = v.findViewById(R.id.unsubscribe);
        mSubscribe.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "SubscribedTopic:" + mTopic);
                OneSignal.sendTag("subscribed_topic", mTopic);
                Toast.makeText(mContext, "Subscribed", Toast.LENGTH_SHORT).show();
            }
        });
        mUnsubscribe.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "unSubscribedTopic:" + mTopic);
                OneSignal.deleteTag("subscribed_topic");
                Toast.makeText(mContext, "Unsubscribed", Toast.LENGTH_SHORT).show();
            }
        });

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
                args.putString("action", "location");
                mapsFragment.setArguments(args);
                activity.getSupportFragmentManager().beginTransaction().replace(R.id.main_frame,
                        mapsFragment).addToBackStack(null).commit();

                Log.e(TAG, "Address is :" + Address);
            }
        });

        mMapDirections.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AppCompatActivity activity = (AppCompatActivity) mContext;
                MapsFragment mapsFragment = new MapsFragment();
                Bundle args = new Bundle();
                args.putString("address", Address);
                args.putString("action", "directions");
                mapsFragment.setArguments(args);
                activity.getSupportFragmentManager().beginTransaction().replace(R.id.main_frame,
                        mapsFragment).addToBackStack(null).commit();

                Log.e(TAG, "Address is :" + Address);
            }
        });
    }

    private void callPlace() {
        mPhone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_DIAL);
                intent.setData(Uri.parse("tel:" + Phone));
                startActivity(intent);
            }
        });
    }

    private void showData() {
        DatabaseReference details = mDataRef.child(key);

        details.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                thumbnailList.clear();
                Place placeInfo = dataSnapshot.getValue(Place.class);

                if (placeInfo != null) {
                    mPlaceName.setText(placeInfo.getLocation());
                    String type = "Place type: " + placeInfo.getType();
                    mType.setText(type);
                    mTopic = mPlaceName.getText().toString().replace(" ", "");
                    Address = placeInfo.getAddress();
                    Phone = placeInfo.getPhone();

                    thumbnailList.add(placeInfo.getThumbnail());
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public boolean isServicesOK() {
        Log.d(TAG, "isServicesOK: checking google services version");

        int available = GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(getContext());

        if (available == ConnectionResult.SUCCESS) {
            Log.d(TAG, "isServicesOK: google services are working");
            return true;
        } else if (GoogleApiAvailability.getInstance().isUserResolvableError(available)) {
            Log.d(TAG, "isServicesOK: error occured but we can fix it");
            Dialog dialog = GoogleApiAvailability.getInstance().getErrorDialog(getActivity(), available, ERROR_DIALOG_REQUEST);
        } else {
            Toast.makeText(getContext(), "You can't make map request", Toast.LENGTH_SHORT).show();
        }
        return false;
    }
}

