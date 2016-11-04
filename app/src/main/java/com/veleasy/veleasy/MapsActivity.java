package com.veleasy.veleasy;

import android.Manifest;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.RemoteViews;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.AutocompleteFilter;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocompleteFragment;
import com.google.android.gms.location.places.ui.PlaceSelectionListener;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class MapsActivity extends FragmentActivity implements PlaceSelectionListener,OnMapReadyCallback,GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, LocationListener {


    public static final int MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 123;
    private String URL = "http://opendata.paris.fr/api/records/1.0/search/?dataset=stations-velib-disponibilites-en-temps-reel&geofilter.distance=48.855221%2C2.347919%2C400";
    private static final int ARROW_B = R.mipmap.arrow_b;
    private static final int ARROW_O = R.mipmap.arrow_o;
    private static final int BLUE_COLOR = 0xff4285F4;
    private static final int ORANGE_COLOR = 0xffFFA500;

    private GoogleMap mMap;
    private GoogleApiClient mGoogleApiClient = null;
    private JsonObjectRequest jsonObjectRequest;
    private LatLng circleCenter = null;
    private HashMap<Station,Marker> cachedStation;
    private Circle circle;
    private Location mLastLocation;
    private boolean isShowingVelib = true;
    private boolean zoomOnPositionOnce = true;
    private Button buttonVelib;
    private Button buttonPlace;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);


        PlaceAutocompleteFragment autocompleteFragment = (PlaceAutocompleteFragment)
                getFragmentManager().findFragmentById(R.id.autocomplete_fragment);
        AutocompleteFilter typeFilter = new AutocompleteFilter.Builder()
                .setTypeFilter(AutocompleteFilter.TYPE_FILTER_ADDRESS)
                .build();
        autocompleteFragment.setFilter(typeFilter);
        autocompleteFragment.setOnPlaceSelectedListener(this);


        jsonObjectRequest = new JsonObjectRequest
                (Request.Method.GET, URL, null, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        addMarkersToMap(response);
                    }
                }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e("mba","TozLose");

                    }
                });

        // Check if user gave the permission for Location
        int permissionCheck = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION);
        if(permissionCheck != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
        } else {
            initMapAsync();
        }
        cachedStation = new HashMap<>();
        buttonPlace = (Button)findViewById(R.id.place);
        buttonVelib = (Button)findViewById(R.id.velo);
        buttonPlace.setClickable(false);
        buttonVelib.setClickable(false);
    }

    public void callToApi(boolean isCenterDefined){
        buttonPlace.setClickable(false);
        buttonVelib.setClickable(false);
        mMap.clear();
        if(isCenterDefined)
            changeVolleyRequest();
        VolleySingleton.getInstance(getBaseContext()).addToRequestQueue(jsonObjectRequest);
    }

    private void changeVolleyRequest() {
        URL = "http://opendata.paris.fr/api/records/1.0/search/?dataset=stations-velib-disponibilites-en-temps-reel&geofilter.distance="+ circleCenter.latitude+"%2C"+ circleCenter.longitude+"%2C400";
        jsonObjectRequest = new JsonObjectRequest
                (Request.Method.GET, URL, null, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        addMarkersToMap(response);
                    }
                }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e("mba","TozLose");

                    }
                });
    }

    public void addMarkersToMap(JSONObject jsonObject) {
        try {
            cachedStation.clear();
            JSONArray jsonArray = jsonObject.getJSONArray("records");
            int n = jsonArray.length();
            for(int i = 0 ; i < n ; i++)
                addMarkerToMap((JSONObject) jsonArray.get(i));
            CircleOptions circleOptions;
            int strokeColor;
            if(circleCenter != null) {
                if(isShowingVelib)
                    strokeColor = BLUE_COLOR;
                else
                    strokeColor = ORANGE_COLOR;
                circleOptions = new CircleOptions().center(circleCenter).strokeColor(strokeColor).radius(400); // In meters
            }else {
                if(isShowingVelib)
                    strokeColor = BLUE_COLOR;
                else
                    strokeColor = ORANGE_COLOR;
                circleOptions = new CircleOptions().center(new LatLng(48.855221, 2.347919)).strokeColor(strokeColor).radius(400); // In meters
            }
            circle = mMap.addCircle(circleOptions);
            // Toast.makeText(this, "Activating buttons", Toast.LENGTH_SHORT).show();
            buttonPlace.setClickable(true);
            buttonVelib.setClickable(true);

        } catch (JSONException e) {
            e.printStackTrace();
            Log.e("Log", "Ouah ca marche aps");
        }

    }

    /**
     * Allows to add a MyMarker on the googleMap from the data contained in the JsonObject
     * @param jsonObject Contains all the information of one station
     *
     */
    public void addMarkerToMap(JSONObject jsonObject){

        Station st = Station.getStation(jsonObject);
        Integer numberToShow;
        int bitmap;
        if(isShowingVelib) {
            numberToShow = st.getAvailableBike();
            bitmap = ARROW_B;
        } else {
            numberToShow = st.getAvailableBikeStand();
            bitmap = ARROW_O;
        }

        Marker m = mMap.addMarker(MarkerManager.getNewMarker(this, st.getPosition(), bitmap, numberToShow));
        cachedStation.put(st,m);

    }

    public void showVelib(View v) {
        if (!isShowingVelib) {
            circle.setStrokeColor(0xff4285F4);
            int bitmap = ARROW_B;
            buttonPlace.setBackgroundResource(R.mipmap.parkingicon);
            buttonVelib.setBackgroundResource(R.mipmap.velibiconactive);
            isShowingVelib = true;
        for (Map.Entry<Station, Marker> entry : cachedStation.entrySet()) {
            Integer numberToShow = entry.getKey().getAvailableBike();
            entry.getValue().setIcon(BitmapDescriptorFactory.fromBitmap(Tools.writeTextOnDrawable(this, bitmap, numberToShow.toString())));
        }
      }
    }

    public void showPlaces(View v) {
        if(isShowingVelib) {
            circle.setStrokeColor(0xffFFA500);
            buttonPlace.setBackgroundResource(R.mipmap.parkingiconactive);
            buttonVelib.setBackgroundResource(R.mipmap.velibicon);
            isShowingVelib = false;
            int bitmap = ARROW_O;
            for (Map.Entry<Station, Marker> entry : cachedStation.entrySet()) {
                Integer numberToShow = entry.getKey().getAvailableBikeStand();
                entry.getValue().setIcon(BitmapDescriptorFactory.fromBitmap(Tools.writeTextOnDrawable(this, bitmap, numberToShow.toString())));
            }

        }
    }

    public void startPreferenceActivity(View v) {
        startActivity(new Intent(this, FavActivity.class));
    }

    public void goToMyLocation(View v) {
        Log.d("widgeg","toz");
        /*if(mLastLocation != null) {
            circleCenter = new LatLng(mLastLocation.getLatitude(), mLastLocation.getLongitude());
            mMap.moveCamera(CameraUpdateFactory.newLatLng(circleCenter));
            mMap.animateCamera(CameraUpdateFactory.zoomTo(14));
            callToApi(true);
        }*/
        Context context = getApplicationContext();
        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
        RemoteViews remoteViews = new RemoteViews(context.getPackageName(), R.layout.velwidget_layout);
        ComponentName thisWidget = new ComponentName(context, VelWidgetProvider.class);
        //remoteViews.setTextViewText(R.id.nbVelib1, "myText 10");
        appWidgetManager.updateAppWidget(thisWidget, remoteViews);
    }

    public void initMapAsync(){
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @SuppressWarnings("MissingPermission")
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setMyLocationEnabled(true);
        buildAndConnectGoogleApiClient();
        mMap.setOnMarkerClickListener(new MarkerManager(this, this));

        // Paris 1er arrondissement
        mMap.moveCamera(CameraUpdateFactory.newLatLng(new LatLng(48.855221, 2.347919)));
        mMap.animateCamera(CameraUpdateFactory.zoomTo(14));

        // Calls the api in Chatelet if  user didnt set location on
        boolean isLocationEnabled = false;
        try {
           isLocationEnabled = Settings.Secure.getInt(getBaseContext().getContentResolver(), Settings.Secure.LOCATION_MODE)  != 0; // Mode_off = 0
        } catch (Settings.SettingNotFoundException e) {
            e.printStackTrace();
        }

        if(!isLocationEnabled) {
            circleCenter = new LatLng(48.855221, 2.347919);
            callToApi(false);
        }

        mMap.setOnCameraIdleListener(new GoogleMap.OnCameraIdleListener() {
            @Override
            public void onCameraIdle() {
                LatLng newCameraPos = mMap.getCameraPosition().target;

                if(newCameraPos == null || circleCenter == null)
                    return;
                float[] results = new float[10];
                Location.distanceBetween(circleCenter.latitude, circleCenter.longitude,newCameraPos.latitude,newCameraPos.longitude,results);
                if(results[0] >= 350){
                    circleCenter = newCameraPos;
                    callToApi(true);
                }
            }
        });
    }

    public synchronized void buildAndConnectGoogleApiClient(){
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
        mGoogleApiClient.connect();
    }

    public void updatePos(Location l){
        mLastLocation = l;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Log.d("PERMISSION","Jai la permission");
                    initMapAsync();

                    // Access the RequestQueue through singleton class.

                } else {
                    Log.d("PERMISSION","Jai pas la permission");
                }
                break;
            }
        }
    }

    @SuppressWarnings("MissingPermission")
    @Override
    public void onLocationChanged(Location location) {
        LatLng pos;
        pos = new LatLng(location.getLatitude(), location.getLongitude());
        Location mLastLocation = location;
        if(zoomOnPositionOnce){
            zoomOnPositionOnce = false;
            circleCenter = pos;
            mMap.moveCamera(CameraUpdateFactory.newLatLng(circleCenter));
            mMap.animateCamera(CameraUpdateFactory.zoomTo(14));
            callToApi(true);
        }
       updatePos(mLastLocation);
    }

    @SuppressWarnings("MissingPermission")
    @Override
    public void onConnected(@Nullable Bundle bundle) {
        LocationRequest mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(30000); // 3 seconds
        mLocationRequest.setFastestInterval(30000); // 3 seconds
        mLocationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);
        LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
        mLastLocation = LocationServices.FusedLocationApi.getLastLocation(
                mGoogleApiClient);

    }

    @Override
    public void onConnectionSuspended(int i) {
        Log.e("Lol","Je passe suspend");
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Log.e("Lol","Je passe failed");
    }

    @Override
    public void onPlaceSelected(Place place) {
        Log.e("toz", "Place Selected: " + place.getName());
        circleCenter = place.getLatLng();
        callToApi(true);
        mMap.animateCamera(CameraUpdateFactory.newLatLng(place.getLatLng()),2000,null);
    }

    @Override
    public void onError(Status status) {
        Log.e("Error", "onError: Status = " + status.toString());
    }

    public HashMap<Station,Marker> getCachedStation() {
        return cachedStation;
    }

}












