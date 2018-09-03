package com.mpetroiu.smc;

import android.content.Context;
import android.graphics.Rect;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import static android.support.constraint.Constraints.TAG;


public class SearchFragment extends Fragment {

    private static final String TAG = "SearchFragment";

    private EditText mSearchField;

    private Place mPlace;

    private ArrayList<String> locationList;
    private ArrayList<String> addressList;
    private ArrayList<String> thumbnailList;
    private ArrayList<String> keyList;

    private RecyclerView mSearchResult;

    private DatabaseReference mPlaceRef;

    private SearchAdapter mAdapter;

    public SearchFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {


        final View v = inflater.inflate(R.layout.fragment_search, container, false);

        mPlaceRef = FirebaseDatabase.getInstance().getReference();

        mPlace = new Place();

        mSearchField = v.findViewById(R.id.etSearch);

        mSearchResult = v.findViewById(R.id.search_result);

        mSearchResult.setHasFixedSize(true);
        mSearchResult.setLayoutManager(new LinearLayoutManager(getContext()));
        mSearchResult.setItemAnimator(new DefaultItemAnimator());

        locationList = new ArrayList<>();
        addressList = new ArrayList<>();
        thumbnailList = new ArrayList<>();
        keyList = new ArrayList<>();

        mSearchField.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (!s.toString().isEmpty()) {
                    setAdapter(s.toString());
                } else {
                    locationList.clear();
                    addressList.clear();
                    thumbnailList.clear();
                    keyList.clear();
                    mSearchResult.removeAllViews();
                }
            }
        });

        return v;
    }

    private void setAdapter(final String searchedString) {

        mPlaceRef.child("Places").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                locationList.clear();
                addressList.clear();
                thumbnailList.clear();
                keyList.clear();


                mSearchResult.removeAllViews();

                int counter = 0;

                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {

                    String location = snapshot.getValue(Place.class).getLocation();
                    String address = snapshot.getValue(Place.class).getAddress();
                    String type = snapshot.getValue(Place.class).getType();
                    String thumb = snapshot.getValue(Place.class).getThumbnail();
                    String key = snapshot.getKey();

                    if (location.toLowerCase().contains(searchedString.toLowerCase())) {
                        locationList.add(location);
                        addressList.add(address);
                        thumbnailList.add(thumb);
                        keyList.add(key);

                        counter++;
                    } else if (address.toLowerCase().contains(searchedString.toLowerCase())) {
                        locationList.add(location);
                        addressList.add(address);
                        thumbnailList.add(thumb);
                        keyList.add(key);

                        counter++;
                    } else if (type.toLowerCase().contains(searchedString.toLowerCase())) {
                        locationList.add(location);
                        addressList.add(address);
                        thumbnailList.add(thumb);
                        keyList.add(key);

                        counter++;
                    }

                    if (counter == 15)
                        break;
                }

                mAdapter = new SearchAdapter(getContext(), locationList, addressList, thumbnailList, keyList);
                mSearchResult.setAdapter(mAdapter);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();

        getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
    }
}




