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
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.onesignal.OneSignal;

import java.util.ArrayList;
import java.util.HashMap;

import me.zhanghai.android.materialratingbar.MaterialRatingBar;


public class ExploreFragment extends Fragment {

    private final static String TAG = "ExploreFragment";
    private static final int ERROR_DIALOG_REQUEST = 9001;
    private static final int REQUEST_PHONE_CALL = 1235;

    private DatabaseReference mDataRef;
    private DatabaseReference mComments;

    private Context mContext;

    private String key, Address, Phone, mTopic;

    private TextView mPlaceName, mType, mCommentName, mCommentText;

    private ImageView mMapLocation, mMapDirections, mPhone;

    private Button mSubscribe, mUnsubscribe, mWriteComment, mPostComment;

    private ArrayList<String> thumbnailList, nameList, commentList;

    private MaterialRatingBar mRatingBar;

    private ViewPager viewPager;
    private ViewPagerAdapter adapter;
    private RecyclerView mCommentRecycle;
    private CommentAdapter mCommAdapter;
    private AlertDialog mDialog;

    private FirebaseAuth mAuth;

    private String uid;

    public ExploreFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_explore, container, false);

        mContext = v.getContext();

        mAuth = FirebaseAuth.getInstance();

        mDataRef = FirebaseDatabase.getInstance().getReference().child("Places");

        mPlaceName = v.findViewById(R.id.placeName);
        mType = v.findViewById(R.id.txPlaceType);
        mRatingBar = (MaterialRatingBar) v.findViewById(R.id.rating);


        thumbnailList = new ArrayList<>();

        viewPager = v.findViewById(R.id.view_pager);
        adapter = new ViewPagerAdapter(mContext, thumbnailList);
        viewPager.setAdapter(adapter);

        if (getArguments() != null) {
            key = getArguments().getString("key");
            Log.d(TAG, "this is key : " + key);

        }

        mComments = mDataRef.child(key).child("comments");

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

        mWriteComment = v.findViewById(R.id.writeComment);
        mCommentRecycle = v.findViewById(R.id.commentList);
        mCommentRecycle.setHasFixedSize(true);
        mCommentRecycle.setLayoutManager(new LinearLayoutManager(getContext()));
        mCommentRecycle.setItemAnimator(new DefaultItemAnimator());

        nameList = new ArrayList<>();
        commentList = new ArrayList<>();

        showComments();

        mCommAdapter = new CommentAdapter(getContext(), nameList, commentList);
        mCommentRecycle.setAdapter(mCommAdapter);
        mCommentRecycle.scrollToPosition(mCommAdapter.getItemCount() - 1);

        loginAnon();
        mRatingBar.setOnRatingChangeListener(new MaterialRatingBar.OnRatingChangeListener() {
            @Override
            public void onRatingChanged(MaterialRatingBar ratingBar, float rating) {
                DatabaseReference ratingRef = mDataRef.child(key).child("rating");
                ratingRef.child("uid").setValue(uid);
                ratingRef.child("rating").setValue(rating);
            }
        });

        showRating();
        writeComment();

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

    private void showComments() {
        mComments.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                nameList.clear();
                commentList.clear();

                int counter = 0;
                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    String name = ds.getValue(Comment.class).getName();
                    String comment = ds.getValue(Comment.class).getComment();
                    String key = ds.getValue(Comment.class).getKey();

                    nameList.add(name);
                    commentList.add(comment);
                    counter++;

                    if (counter == 10)
                        break;
                }
                mCommAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void writeComment() {
        mWriteComment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loginAnon();

                final AlertDialog.Builder mBuilder = new AlertDialog.Builder(mContext);
                View mView = getLayoutInflater().inflate(R.layout.comment_dialog, null);

                mCommentName = mView.findViewById(R.id.etUserName);
                mCommentText = mView.findViewById(R.id.etComment);
                mPostComment = mView.findViewById(R.id.postComment);

                mBuilder.setView(mView);
                mDialog = mBuilder.create();
                mDialog.show();

                mPostComment.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        HashMap<String, String> comm = new HashMap<>();

                        String name = mCommentName.getText().toString();
                        String comment = mCommentText.getText().toString();

                        comm.put("name", name);
                        comm.put("comment", comment);
                        comm.put("uid", uid);
                        mComments.push().setValue(comm);

                        mDialog.dismiss();


                    }
                });
            }
        });
    }

    private void loginAnon() {
        mAuth.signInAnonymously().addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    uid = mAuth.getCurrentUser().getUid();
                    Log.d(TAG, "loginAnon: is Successful " + uid);
                } else {
                    Log.d(TAG, "loginAnon: failed ");
                }
            }
        });

    }

    private void showRating() {
        DatabaseReference ratingRef = mDataRef.child(key).child("rating");
        ratingRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    Log.d(TAG,"dataSNAP: " + dataSnapshot);
                    if (dataSnapshot.exists()) {
                        mRatingBar.setRating(Float.valueOf(dataSnapshot.child("rating").getValue().toString()));

                }
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

