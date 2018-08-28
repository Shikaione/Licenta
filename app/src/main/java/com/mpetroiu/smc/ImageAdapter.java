package com.mpetroiu.smc;


import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.Image;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseError;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static android.content.Context.MODE_PRIVATE;
import static android.support.constraint.Constraints.TAG;

public class ImageAdapter extends RecyclerView.Adapter<ImageAdapter.ImageViewHolder> {

    private Context mContext;

    private DatabaseReference mDataRef;

    private List<Upload> mUploads;
    private ToggleButton mFavorite;
    private Upload uploadCurrent;
    private SharedPref mSharedPref;
    
    private TextView mExplore;

    public ImageAdapter(List<Upload> uploads) {
        mUploads = uploads;
    }

    @NonNull
    @Override
    public ImageViewHolder onCreateViewHolder(@NonNull final ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.card, parent, false);

        mContext = parent.getContext();

        ImageViewHolder mViewHolder = new ImageViewHolder(view);

        mFavorite = view.findViewById(R.id.toggle_favorite);
        mDataRef = FirebaseDatabase.getInstance().getReference().child("Favorites");
        mExplore = view.findViewById(R.id.explore);
        mSharedPref = new SharedPref(mContext);

        return mViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull final ImageViewHolder holder, final int position) {
        uploadCurrent = mUploads.get(position);
        holder.textViewName.setText(uploadCurrent.getName());
        Picasso.get()
                .load(uploadCurrent.getImageUrl())
                .fit()
                .centerCrop()
                .into(holder.imageView);

        if (mSharedPref.loadFavorite()) {
            mFavorite.setChecked(true);
        }

        manageFavorites();

        mExplore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AppCompatActivity activity = (AppCompatActivity) v.getContext();
                ExploreFragment exploreFragment = new ExploreFragment();
                activity.getSupportFragmentManager().beginTransaction().replace(R.id.main_frame,
                        exploreFragment).addToBackStack(null).commit();
            }
        });
    }

    @Override
    public int getItemCount() {
        return mUploads.size();
    }

    public class ImageViewHolder extends RecyclerView.ViewHolder {

        public TextView textViewName;
        public ImageView imageView;

        public ImageViewHolder(@NonNull View itemView) {
            super(itemView);

            textViewName = itemView.findViewById(R.id.text_view_name);
            imageView = itemView.findViewById(R.id.image_view_upload);
        }
    }

    private void manageFavorites(){
        mFavorite.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                if (isChecked) {

                    String name = uploadCurrent.getName();
                    String url = uploadCurrent.getImageUrl();
                    String key = uploadCurrent.getKey();

                    Map<String, String> favMap = new HashMap<>();

                    favMap.put("name", name);
                    favMap.put("imageUrl", url);

                    mDataRef.child(key).setValue(favMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                mSharedPref.SetFavorite(true);

                                Toast.makeText(mContext,
                                        "Favorite added.",
                                        Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                } else {
                    Query favQuery = mDataRef.orderByChild("imageUrl").equalTo(uploadCurrent.getImageUrl());

                    favQuery.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            for (DataSnapshot favSnapshot : dataSnapshot.getChildren()) {
                                favSnapshot.getRef().removeValue();
                                mSharedPref.SetFavorite(false);
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {
                            Log.e(TAG, "onCancelled", databaseError.toException());
                        }
                    });
                }
            }
        });
    }
}
