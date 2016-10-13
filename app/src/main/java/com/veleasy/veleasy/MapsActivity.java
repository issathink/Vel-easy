package com.veleasy.veleasy;

import android.*;
import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.os.CountDownTimer;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
<<<<<<< HEAD
import com.google.android.gms.maps.model.CameraPosition;
=======
>>>>>>> 472ba0e2e54116e57d774c53d426d94fa43736fa
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback,GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, LocationListener,GoogleMap.OnCameraMoveListener {


    public static final int MY_PERMISSIONS_REQUEST_ACCES_FINE_LOCATION=123;
    private static final String URL = "http://opendata.paris.fr/api/records/1.0/search/?dataset=stations-velib-disponibilites-en-temps-reel";

    private boolean hashLocationPermission = false;
    private GoogleMap mMap;
    private GoogleApiClient mGoogleApiClient = null;
    private Location mLastLocation;
    private Marker user_PosMarker = null;
    private JsonObjectRequest jsonObjectRequest;


    private JsonObjectRequest jsonObjectRequest;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        jsonObjectRequest = new JsonObjectRequest
                (Request.Method.GET, URL, null, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.e("mba","Toz");
                        addMarkersToMap(response);
                    }
                }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e("mba","Tozzzz");

                    }
                });

        // Check if user gave the permission for Location
        int permissionCheck = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION);
        if(permissionCheck != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    MY_PERMISSIONS_REQUEST_ACCES_FINE_LOCATION);
        }else {
            hashLocationPermission = true;
            VolleySingleton.getInstance(this).addToRequestQueue(jsonObjectRequest);
            initMapAsync();
        }




    }

    public void initMapAsync(){
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    public void addMarkersToMap(JSONObject jsonObject) {
        try {
            JSONObject lol = (JSONObject) jsonObject.getJSONArray("records").get(0);
            JSONObject lol2 = (JSONObject) lol.get("fields");
            JSONArray lol3 = (JSONArray) lol2.get("position");
            Double l1 = (Double) lol3.get(0);
            Double l2 = (Double) lol3.get(1);
            Log.d("YOO",l1.toString());
            mMap.addMarker(new MarkerOptions().position(new LatLng(l1,l2)));
            mMap.moveCamera(CameraUpdateFactory.newLatLng(new LatLng(48.847786,2.3545948)));

            CircleOptions circleOptions = new CircleOptions()
                    .center(new LatLng(48.847786,2.3545948  ))
                    .strokeColor(0xff4285F4)
                    .fillColor(0x80C8D6EC)
                    .radius(500); // In meters
            Circle circle = mMap.addCircle(circleOptions);

        } catch (JSONException e) {
            e.printStackTrace();
            Log.e("Log", "Ouah ca marche aps");
        }

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
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setMyLocationEnabled(true);
        buildAndConnectGoogleApiClient();
        Log.e("toz","yo");
    }
    public synchronized  void buildAndConnectGoogleApiClient(){
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
        mGoogleApiClient.connect();
    }

    public void updatePos(Location l){
/*        LatLng pos;
        if(l!=null) {
            Log.e("Erreur","Loc -> lat "+l.getLatitude()+" et "+l.getLongitude());
            Toast.makeText(MapsActivity.this, "Loc -> lat "+l.getLatitude()+" et "+l.getLongitude(), Toast.LENGTH_LONG).show();
            pos = new LatLng(l.getLatitude(), l.getLongitude());
        } else {
            Log.e("Erreur","Localisation par defaut");
            pos = new LatLng(2, 2);
        }
        mMap.moveCamera(CameraUpdateFactory.newLatLng(pos));*/
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_ACCES_FINE_LOCATION: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Log.d("PERMIISSION","Jai la permission");
                    hashLocationPermission = true;
                    initMapAsync();

                    // Access the RequestQueue through your singleton class.
                    VolleySingleton.getInstance(this).addToRequestQueue(jsonObjectRequest);

                } else {
                    hashLocationPermission = false;
                    Log.d("PERMIISSION","Jai pas la permission");
                }
                return;

            }
        }
    }

    @SuppressWarnings("MissingPermission")
    @Override
    public void onLocationChanged(Location location) {
        LatLng pos;
        pos = new LatLng(location.getLatitude(), location.getLongitude());
        mLastLocation = location;
        Log.d("Log","Si tu marche cest cool");
        updatePos(mLastLocation);

    }
    @Override
    public void onConnected(@Nullable Bundle bundle) {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(30000); //3 seconds
        mLocationRequest.setFastestInterval(30000); //3 seconds
        mLocationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);
        LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);

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
    public void onCameraMove() {
        CameraPosition ss = mMap.getCameraPosition();
        Log.d("TOOZZ",ss.target.toString());
    }

}












