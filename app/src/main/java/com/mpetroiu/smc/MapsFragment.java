package com.mpetroiu.smc;


import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class MapsFragment extends Fragment implements OnMapReadyCallback {

    @Override
    public void onMapReady(GoogleMap googleMap) {
        Toast.makeText(mContext, "Map is Ready", Toast.LENGTH_SHORT).show();
        Log.d(TAG, "onMapReady: map is ready");

        mMap = googleMap;
        mMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(mContext, R.raw.map_style));
        if (mLocationPermissionsGranted) {

            if (ActivityCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_FINE_LOCATION)
                    != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(mContext,
                    Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                return;
            }
            if (action.equals("location")) {
                Log.d(TAG, "mapUsedFor: " + action);
                geoLocate();
                mMap.getUiSettings().setMyLocationButtonEnabled(false);
            } else if (action.equals("directions")) {
                Log.d(TAG, "mapUsedFor: " + action);
                getDeviceLocation();
                geoLocate();
                mMap.setMyLocationEnabled(true);
                mMap.getUiSettings().setMyLocationButtonEnabled(false);

                Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        String url = getRequestUrl();
                        TaskRequestDirections taskRequestDirections = new TaskRequestDirections();
                        taskRequestDirections.execute(url);
                    }
                }, 2500);

            } else if (action.equals("nearby")) {
                mMap.setMyLocationEnabled(true);
                getDeviceLocation();

                Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        Object dataTransfer[] = new Object[2];

                        String resturant = "restuarant";
                        String url = getUrl(deviceLocation.getLatitude(), deviceLocation.getLongitude(), resturant);

                        GetNearbyPlacesData getNearbyPlacesData = new GetNearbyPlacesData();

                        dataTransfer[0] = mMap;
                        dataTransfer[1] = url;

                        getNearbyPlacesData.execute(dataTransfer);
                    }
                }, 2500);

                Toast.makeText(mContext, "Showing Nearby Restaurants", Toast.LENGTH_SHORT).show();
            }

        }
    }


    public static final String TAG = "MapsFragment";

    private static final String FINE_LOCATION = Manifest.permission.ACCESS_FINE_LOCATION;
    private static final String COURSE_LOCATION = Manifest.permission.ACCESS_COARSE_LOCATION;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1234;
    private static final float DEFAULT_ZOOM = 15f;
    private static final int PROXIMITY_RADIUS = 10000;
    private double latitude, longitude;

    private GoogleMap mMap;

    private Boolean mLocationPermissionsGranted = false;
    private FusedLocationProviderClient mFusedLocationProviderClient;

    private Location deviceLocation;
    private Address location;

    private SupportMapFragment mMapFragment;

    private Context mContext;

    private String address, action;

    private DownloadURL downloadURL;

    public MapsFragment() {
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_maps, container, false);
        mContext = v.getContext();

        downloadURL = new DownloadURL();

        if (getArguments() != null) {
            address = getArguments().getString("address");
            action = getArguments().getString("action");

            Log.d(TAG, "Address Received : " + address);
            Log.d(TAG, "Action Received : " + action);
        }

        getLocationPermission();

        return v;
    }


    private void getDeviceLocation() {
        Log.d(TAG, "getDeviceLocation: getting the devices current location");

        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(mContext);

        try {
            if (mLocationPermissionsGranted) {

                final Task location = mFusedLocationProviderClient.getLastLocation();
                location.addOnCompleteListener(new OnCompleteListener() {
                    @Override
                    public void onComplete(@NonNull Task task) {
                        if (task.isSuccessful()) {
                            Log.d(TAG, "onComplete: found location!");
                            deviceLocation = (Location) task.getResult();

                            moveCamera(new LatLng(deviceLocation.getLatitude(), deviceLocation.getLongitude()),
                                    DEFAULT_ZOOM,
                                    "My Location");

                        } else {
                            Log.d(TAG, "onComplete: current location is null");
                            Toast.makeText(mContext, "unable to get current location", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        } catch (SecurityException e) {
            Log.e(TAG, "getDeviceLocation: SecurityException: " + e.getMessage());
        }
    }

    private void moveCamera(LatLng latLng, float zoom, String title) {
        Log.d(TAG, "moveCamera: moving the camera to: lat: " + latLng.latitude + ", lng: " + latLng.longitude);
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, zoom));

        if (!title.equals("My Location")) {

            MarkerOptions options = new MarkerOptions()
                    .position(latLng)
                    .title(title);
            mMap.addMarker(options);
        }

        hideSoftKeyboard();
    }

    private void initMap() {
        Log.d(TAG, "initMap: initializing map");
        mMapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        mMapFragment.getMapAsync(this);

    }

    private void geoLocate() {
        Log.d(TAG, "geoLocate: geolocating");

        Geocoder geocoder = new Geocoder(mContext);

        List<Address> list = new ArrayList<>();

        try {
            list = geocoder.getFromLocationName(address, 1);
        } catch (IOException e) {
            Log.e(TAG, "geoLocate: IOException: " + e.getMessage());
        }

        if (list.size() > 0) {
            location = list.get(0);

            Log.d(TAG, "geoLocate: found a location: " + address.toString());

            moveCamera(new LatLng(location.getLatitude(), location.getLongitude()), 18f,
                    location.getAddressLine(0));

        }
    }

    private String getRequestUrl() {
        String str_org = "origin=" + deviceLocation.getLatitude() + "," + deviceLocation.getLongitude();
        String str_dest = "destination=" + location.getLatitude() + "," + location.getLongitude();
        String sensor = "sensor=false";
        String mode = "mode=driving";
        String param = str_org + "&" + str_dest + "&" + sensor + "&" + mode;
        String output = "json";

        String url = "https://maps.googleapis.com/maps/api/directions/" + output + "?" + param;

        return url;
    }

    private String getUrl(double latitude, double longitude, String nearbyPlace) {

        StringBuilder googlePlaceUrl = new StringBuilder("https://maps.googleapis.com/maps/api/place/nearbysearch/json?");
        googlePlaceUrl.append("location=" + latitude + "," + longitude);
        googlePlaceUrl.append("&radius=" + PROXIMITY_RADIUS);
        googlePlaceUrl.append("&type=" + nearbyPlace);
        googlePlaceUrl.append("&sensor=true");
        googlePlaceUrl.append("&key=" + "AIzaSyDjfVfpGB7b_kHN6l7vzlvVVzVxxPuTaWU");

        Log.d("MapsActivity", "url = " + googlePlaceUrl.toString());

        return googlePlaceUrl.toString();
    }


    private void getLocationPermission() {
        Log.d(TAG, "getLocationPermission: getting location permissions");
        String[] permissions = {Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION};

        if (ContextCompat.checkSelfPermission(mContext,
                FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            if (ContextCompat.checkSelfPermission(mContext,
                    COURSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                mLocationPermissionsGranted = true;
                initMap();
            } else {
                requestPermissions(
                        permissions,
                        LOCATION_PERMISSION_REQUEST_CODE);
            }
        } else {
            requestPermissions(
                    permissions,
                    LOCATION_PERMISSION_REQUEST_CODE);
        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        Log.d(TAG, "onRequestPermissionsResult: called.");
        mLocationPermissionsGranted = false;

        switch (requestCode) {
            case LOCATION_PERMISSION_REQUEST_CODE: {
                if (grantResults.length > 0) {
                    for (int i = 0; i < grantResults.length; i++) {
                        if (grantResults[i] != PackageManager.PERMISSION_GRANTED) {
                            mLocationPermissionsGranted = false;
                            Log.d(TAG, "onRequestPermissionsResult: permission failed");
                            return;
                        }
                    }
                    Log.d(TAG, "onRequestPermissionsResult: permission granted");
                    mLocationPermissionsGranted = true;
                    initMap();
                }
            }
        }
    }

    private void hideSoftKeyboard() {
        getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
    }

    private class TaskRequestDirections extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... strings) {
            String responseString = "";
            try {
                responseString = downloadURL.readUrl(strings[0]);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return responseString;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);

            TaskParser taskParser = new TaskParser();
            taskParser.execute(s);
        }
    }

    public class TaskParser extends AsyncTask<String, Void, List<List<HashMap<String, String>>>> {

        @Override
        protected List<List<HashMap<String, String>>> doInBackground(String... strings) {
            JSONObject jsonObject = null;
            List<List<HashMap<String, String>>> routes = null;
            try {
                jsonObject = new JSONObject(strings[0]);
                DirectionsParser directionsParser = new DirectionsParser();
                routes = directionsParser.parse(jsonObject);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return routes;
        }

        @Override
        protected void onPostExecute(List<List<HashMap<String, String>>> lists) {
            ArrayList points = null;

            PolylineOptions polylineOptions = null;

            for (List<HashMap<String, String>> path : lists) {
                points = new ArrayList();
                polylineOptions = new PolylineOptions();

                for (HashMap<String, String> point : path) {
                    double lat = Double.parseDouble(point.get("lat"));
                    double lon = Double.parseDouble(point.get("lon"));
                    points.add(new LatLng(lat, lon));
                }
                polylineOptions.addAll(points);
                polylineOptions.width(15);
                polylineOptions.color(Color.BLUE);
                polylineOptions.geodesic(true);
            }

            if (polylineOptions != null) {
                mMap.addPolyline(polylineOptions);
            } else {
                Toast.makeText(mContext, "Directions not found", Toast.LENGTH_SHORT).show();
            }
        }
    }
}

