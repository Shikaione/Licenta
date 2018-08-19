package com.mpetroiu.smc;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.FrameLayout;

public class MainActivity extends AppCompatActivity {

    private BottomNavigationView mBottomNav;
    private FrameLayout mFrame;

    private HomeFragment mHomeFragment;
    private PlacesFragment mPlaceFragment;
    private FavoritesFragment mFavoriteFragment;
    private SearchFragment mSearchFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mBottomNav = findViewById(R.id.navigation);
        mFrame = findViewById(R.id.main_frame);

        mHomeFragment = new HomeFragment();
        mPlaceFragment = new PlacesFragment();
        mFavoriteFragment = new FavoritesFragment();
        mSearchFragment = new SearchFragment();

        setFragment(mHomeFragment);

        mBottomNav.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                switch (item.getItemId()) {
                    case R.id.nav_home :
                        setFragment(mHomeFragment);
                       return true;
                    case R.id.nav_places :
                        setFragment(mPlaceFragment);
                        return true;
                    case R.id.nav_favorites :
                        setFragment(mFavoriteFragment);
                        return true;
                    case R.id.nav_search :
                        setFragment(mSearchFragment);
                        return true;

                        default:
                            return false;
                }
            }
        });


    }
    private void setFragment(Fragment fragment){
        FragmentTransaction fragmentTransaction  = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.main_frame, fragment);
        fragmentTransaction.commit();
    }
}
