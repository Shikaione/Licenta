package com.mpetroiu.smc;


import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;

import java.util.ArrayList;

import static android.support.constraint.Constraints.TAG;

public class SearchAdapter extends RecyclerView.Adapter<SearchAdapter.SearchViewHolder> implements View.OnClickListener {

    private static final String TAG = "SearchAdapter";

    Context context;
    ArrayList<String> locationList;
    ArrayList<String> addressList;
    ArrayList<String> thumbnailList;
    ArrayList<String> keyList;


    @Override
    public void onClick(View v) {

    }

    class SearchViewHolder extends RecyclerView.ViewHolder {
        ImageView thumb;
        TextView title, address;

        public SearchViewHolder(View itemView) {
            super(itemView);
            title =  itemView.findViewById(R.id.searchPlaceTitle);
            address = itemView.findViewById(R.id.searchPlaceAddress);
            thumb = itemView.findViewById(R.id.resultImg);

        }
    }

    public SearchAdapter(Context context,
                         ArrayList<String> locationList,
                         ArrayList<String> addressList,
                         ArrayList<String> thumbnailList,
                         ArrayList<String> keyList) {
        this.context = context;
        this.locationList = locationList;
        this.addressList = addressList;
        this.thumbnailList = thumbnailList;
        this.keyList = keyList;
    }

    @Override
    public SearchAdapter.SearchViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.search_list, parent, false);
        return new SearchAdapter.SearchViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final SearchViewHolder holder, final int position) {
        holder.title.setText(locationList.get(position));
        holder.address.setText(addressList.get(position));

        Glide.with(context).load(thumbnailList.get(position)).asBitmap().placeholder(R.mipmap.ic_launcher_round).into(holder.thumb);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AppCompatActivity activity = (AppCompatActivity) v.getContext();
                ExploreFragment exploreFragment = new ExploreFragment();
                Bundle args = new Bundle();
                args.putString("key", keyList.get(position));
                exploreFragment.setArguments(args);
                activity.getSupportFragmentManager().beginTransaction().replace(R.id.main_frame,
                        exploreFragment).addToBackStack(null).commit();

                Log.e(TAG, "Key is :" + keyList.get(position));
                Toast.makeText(context, "Full Name Clicked", Toast.LENGTH_SHORT).show();
            }
        });


    }

    @Override
    public int getItemCount() {
        return locationList.size();
    }
}