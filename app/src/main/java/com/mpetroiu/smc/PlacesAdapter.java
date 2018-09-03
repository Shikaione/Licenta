package com.mpetroiu.smc;


import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
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

import static android.support.constraint.Constraints.TAG;

public class PlacesAdapter extends RecyclerView.Adapter<PlacesAdapter.ImageViewHolder> {

    private Context mContext;

    private DatabaseReference mDataRef;

    private List<Upload> mUploads;

    private Upload uploadCurrent;

    private TextView mExplore, mFavorite;

    public PlacesAdapter(List<Upload> uploads) {
        mUploads = uploads;
    }

    @NonNull
    @Override
    public ImageViewHolder onCreateViewHolder(@NonNull final ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.place_card, parent, false);

        mContext = parent.getContext();

        ImageViewHolder mViewHolder = new ImageViewHolder(view);

        mFavorite = view.findViewById(R.id.addFavorite);

        mDataRef = FirebaseDatabase.getInstance().getReference().child("Favorites");
        mExplore = view.findViewById(R.id.explore);

        return mViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull final ImageViewHolder holder, final int position) {
        uploadCurrent = mUploads.get(position);
        holder.textViewName.setText(uploadCurrent.getLocation());
        Picasso.get()
                .load(uploadCurrent.getThumbnail())
                .fit()
                .centerCrop()
                .into(holder.imageView);

        addFavorite();

        mExplore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AppCompatActivity activity = (AppCompatActivity) v.getContext();
                ExploreFragment exploreFragment = new ExploreFragment();
                Bundle args = new Bundle();
                args.putString("key", mUploads.get(position).getKey());
                exploreFragment.setArguments(args);
                activity.getSupportFragmentManager().beginTransaction().replace(R.id.main_frame,
                        exploreFragment).addToBackStack(null).commit();

                Log.e(TAG, "Key is :" + uploadCurrent.getKey());

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

    private void addFavorite() {

        mFavorite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String location = uploadCurrent.getLocation();
                String thumbnail = uploadCurrent.getThumbnail();
                final String key = uploadCurrent.getKey();

                Log.e(TAG, "Key is :" + uploadCurrent.getKey());

                final Map<String, String> favMap = new HashMap<>();

                favMap.put("location", location);
                favMap.put("thumbnail", thumbnail);


                Query favQuery = mDataRef.orderByChild("thumbnail").equalTo(uploadCurrent.getThumbnail());

                favQuery.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.child(key).exists()) {
                            Toast.makeText(mContext,
                                    "Already favorite.",
                                    Toast.LENGTH_SHORT).show();
                        } else {
                            mDataRef.child(key).setValue(favMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {

                                        Toast.makeText(mContext,
                                                "Favorite added.",
                                                Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
            }
        });
    }
}
