package com.veleasy.veleasy;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback,GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, LocationListener {


    public static final int MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 123;
    private String URL = "http://opendata.paris.fr/api/records/1.0/search/?dataset=stations-velib-disponibilites-en-temps-reel";

    private GoogleMap mMap;
    private GoogleApiClient mGoogleApiClient = null;
    private Location mLastLocation;
    private JsonObjectRequest jsonObjectRequest;
    private LocationRequest mLocationRequest;
    private LatLng circle_Center = null;
    private HashMap<Station,Marker> cachedStation;
    private Circle circle;
    private boolean isShowingVelib = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        jsonObjectRequest = new JsonObjectRequest
                (Request.Method.GET, URL, null, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.e("mba",response.toString());
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
        }else {
            initMapAsync();
            VolleySingleton.getInstance(this).addToRequestQueue(jsonObjectRequest);
        }
        cachedStation = new HashMap<>();

    }
    public void preference(View v) {
        Toast.makeText(this, "Yo!", Toast.LENGTH_SHORT).show();
    }

    private void changeVolleyRequest() {
        URL = "http://opendata.paris.fr/api/records/1.0/search/?dataset=stations-velib-disponibilites-en-temps-reel&geofilter.distance="+circle_Center.latitude+"%2C"+circle_Center.longitude+"%2C400";
        jsonObjectRequest = new JsonObjectRequest
                (Request.Method.GET, URL, null, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.e("mba",response.toString());
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

            CircleOptions circleOptions = new CircleOptions().center(circle_Center).strokeColor(0xff4285F4).radius(400); // In meters
            if(mMap == null)
                Log.e("ERROR","Why map is null ?");
            circle = mMap.addCircle(circleOptions);

        } catch (JSONException e) {
            e.printStackTrace();
            Log.e("Log", "Ouah ca marche aps");
        }

    }

    public  void changeValueToShow(View v){
        Log.e("LOGS", (mMap == null) + " ");
        isShowingVelib = !isShowingVelib;
        if(isShowingVelib){
            circle.setStrokeColor(0xff4285F4);
            int bitmap = R.mipmap.arrow_b;
            for(Map.Entry<Station,Marker> entry : cachedStation.entrySet()){
                Integer numberToShow = entry.getKey().getAvailableBike();
                entry.getValue().setIcon(BitmapDescriptorFactory.fromBitmap(Tools.writeTextOnDrawable(this, bitmap ,numberToShow.toString())));
            }
        }else{
            circle.setStrokeColor(0xffFFA500);
            int bitmap = R.mipmap.arrow_o;
            for(Map.Entry<Station,Marker> entry : cachedStation.entrySet()){
                Integer numberToShow = entry.getKey().getAvailableBikeStand();
                entry.getValue().setIcon(BitmapDescriptorFactory.fromBitmap(Tools.writeTextOnDrawable(this, bitmap,numberToShow.toString())));
            }
        }
        // startActivity(new Intent());
    }



    /**
     * Allows to add a Marker on the googleMap from the data contained in the JsonObject
     * @param jsonObject
     *
     */
    public void addMarkerToMap(JSONObject jsonObject){
        JSONObject fields = null;
        try {
            fields = (JSONObject) jsonObject.get("fields");
            JSONArray position = (JSONArray) fields.get("position");
            LatLng pos =new LatLng((Double)position.get(0),(Double)position.get(1));
            Integer nbVelibDispo = (Integer) fields.get("available_bikes");
            String addressName = (String) fields.get("address");
            String tmpBanking = (String) fields.get("banking");
            boolean banking = tmpBanking.contains("True");
            Integer nbStandDispo = (Integer) fields.get("available_bike_stands");
            Integer nbStands = (Integer) fields.get("bike_stands");
            String status = (String) fields.get("status");
            Station st = new Station(status,nbStands,nbStandDispo,banking,nbVelibDispo,addressName,pos);
            Integer numberToShow;
            int bitmap;
            if(isShowingVelib) {
                numberToShow = st.getAvailableBike();
                bitmap = R.mipmap.arrow_b;
            }else {
                numberToShow = st.getAvailableBikeStand();
                bitmap = R.mipmap.arrow_o;
            }

             Marker m = mMap.addMarker(new MarkerOptions().position(pos)
                                .icon(BitmapDescriptorFactory.fromBitmap(Tools.writeTextOnDrawable(this, bitmap,numberToShow.toString()))));

            cachedStation.put(st,m);
        } catch (JSONException e) {
            e.printStackTrace();
            Log.d("MarkerOnMap","Error JSON");
        }
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
        mMap.setOnCameraIdleListener(new GoogleMap.OnCameraIdleListener() {
            @Override
            public void onCameraIdle() {
                LatLng newCameraPos = mMap.getCameraPosition().target;
                if(newCameraPos == null || circle_Center == null)
                    return;
                float[] results = new float[10];
                Location.distanceBetween(circle_Center.latitude,circle_Center.longitude,newCameraPos.latitude,newCameraPos.longitude,results);
                if(results[0] >= 350){
                    circle_Center = newCameraPos;
                    mMap.clear();
                    changeVolleyRequest();
                    VolleySingleton.getInstance(getBaseContext()).addToRequestQueue(jsonObjectRequest);
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
                    VolleySingleton.getInstance(this).addToRequestQueue(jsonObjectRequest);

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
        mLastLocation = location;
        if(circle_Center == null){
            circle_Center = pos;
            mMap.moveCamera(CameraUpdateFactory.newLatLng(circle_Center));
            mMap.animateCamera(CameraUpdateFactory.zoomTo(14), 2000, null);
        }
        updatePos(mLastLocation);
    }

    @SuppressWarnings("MissingPermission")
    @Override
    public void onConnected(@Nullable Bundle bundle) {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(30000); // 3 seconds
        mLocationRequest.setFastestInterval(30000); // 3 seconds
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

}












