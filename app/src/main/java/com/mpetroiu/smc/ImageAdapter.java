package com.mpetroiu.smc;


import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.Image;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
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

import static android.content.Context.MODE_PRIVATE;
import static android.support.constraint.Constraints.TAG;

public class ImageAdapter extends RecyclerView.Adapter<ImageAdapter.ImageViewHolder> {

    private DatabaseReference mDataRef;

    private List<Upload> mUploads;

    private ToggleButton mFavorite;
    private Context mContext;
    private Upload uploadCurrent;
    private Boolean isFavorite;
    private String url;

    public ImageAdapter(List<Upload> uploads){
        mUploads = uploads;
    }
    @NonNull
    @Override
    public ImageViewHolder onCreateViewHolder(@NonNull final ViewGroup parent, int viewType) {
       View view = LayoutInflater.from(parent.getContext())
               .inflate(R.layout.card, parent, false);

        ImageViewHolder vH = new ImageViewHolder(view);
        mContext = parent.getContext();
        mFavorite = (ToggleButton) view.findViewById(R.id.toggle_favorite);
        mDataRef = FirebaseDatabase.getInstance().getReference().child("Favorites");

        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
               checkFavorite();
            }
        }, 3000);

       return  vH;
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
        mFavorite.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                if(isChecked){
                    String name = uploadCurrent.getName();
                    String url = uploadCurrent.getImageUrl();
                    final Boolean isFav = isChecked;

                    Map<String, String> favMap = new HashMap<>();
                    favMap.put("name", name);
                    favMap.put("url", url);
                    favMap.put("isFav", isFav.toString());

                    mDataRef.push().setValue(favMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(task.isSuccessful()){
                                Toast.makeText(mContext, "Favorite added.", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }else{
                    url = uploadCurrent.getImageUrl();

                    Query favQuery = mDataRef.orderByChild("url").equalTo(url);

                    favQuery.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            for (DataSnapshot favSnapshot: dataSnapshot.getChildren()) {
                                favSnapshot.getRef().removeValue();
                            }
                        }
                        @Override
                        public void onCancelled(DatabaseError databaseError) {
                            Log.e(TAG, "onCancelled", databaseError.toException());
                        }
                    });
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return mUploads.size();
    }

    public class ImageViewHolder extends RecyclerView.ViewHolder{

        public TextView textViewName;
        public ImageView imageView;

        public ImageViewHolder(@NonNull View itemView) {
            super(itemView);

            textViewName = itemView.findViewById(R.id.text_view_name);
            imageView = itemView.findViewById(R.id.image_view_upload);
        }
    }

    public void checkFavorite(){

        Query favQuery = mDataRef.orderByChild("url").equalTo(url);

        favQuery.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                isFavorite = (Boolean) dataSnapshot.child("isFav").getValue();
                Log.d("isFavorite: "," " + isFavorite);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        mFavorite.setChecked(isFavorite);
    }

}
