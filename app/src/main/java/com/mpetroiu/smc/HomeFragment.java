package com.mpetroiu.smc;


import android.content.res.Resources;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.Button;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;


public class HomeFragment extends Fragment {

    private final static String TAG = "HomeFragment";

    private DatabaseReference cardRef;
    private RecyclerView mRecyclerView;
    private PlacesAdapter mAdapter;

    private List<Upload> mUploads;

    private Button mNearbyButton;

    public HomeFragment() {

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_home, container, false);

        cardRef = FirebaseDatabase.getInstance().getReference()
                .child("Places");

        mRecyclerView = v.findViewById(R.id.horizontalRecycle);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getContext(), LinearLayout.HORIZONTAL, false);
        mRecyclerView.addItemDecoration(new PlacesFragment.GridSpacingItemDecoration(2, dpToPx(10), true));
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.scrollToPosition(15);

        mUploads = new ArrayList<>();
        mAdapter = new PlacesAdapter(mUploads);
        mRecyclerView.setAdapter(mAdapter);

        mNearbyButton = v.findViewById(R.id.nearbyBtn);
        showNewestPlaces();
        nearbyPlaces();

        return v;
    }

    public static class GridSpacingItemDecoration extends RecyclerView.ItemDecoration {

        private int spanCount;
        private int spacing;
        private boolean includeEdge;

        public GridSpacingItemDecoration(int spanCount, int spacing, boolean includeEdge) {
            this.spanCount = spanCount;
            this.spacing = spacing;
            this.includeEdge = includeEdge;
        }
    }

    private int dpToPx(int dp) {
        Resources r = getResources();
        return Math.round(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, r.getDisplayMetrics()));
    }

    private void showNewestPlaces() {

        cardRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Iterable<DataSnapshot> children = dataSnapshot.getChildren();
                int counter = 0;
                mUploads.clear();
                for (DataSnapshot ds : children) {
                    Upload upload = ds.getValue(Upload.class);
                    upload.setKey(ds.getKey());

                    mUploads.add(upload);
                    counter++;
                    if (counter == 15)
                        break;
                }
                mAdapter.notifyDataSetChanged();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(getContext(), databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void nearbyPlaces() {
        mNearbyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AppCompatActivity activity = (AppCompatActivity) getContext();
                MapsFragment mapsFragment = new MapsFragment();
                Bundle args = new Bundle();
                args.putString("action", "nearby");
                mapsFragment.setArguments(args);
                activity.getSupportFragmentManager().beginTransaction().replace(R.id.main_frame,
                        mapsFragment).addToBackStack(null).commit();

                Log.e(TAG, "Action is :" + "nearby");
            }
        });
    }
}
