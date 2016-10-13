package com.veleasy.veleasy;

import android.*;
import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Typeface;
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
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback,GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, LocationListener {


    public static final int MY_PERMISSIONS_REQUEST_ACCES_FINE_LOCATION=123;
    private String URL = "http://opendata.paris.fr/api/records/1.0/search/?dataset=stations-velib-disponibilites-en-temps-reel";

    private GoogleMap mMap;
    private GoogleApiClient mGoogleApiClient = null;
    private Location mLastLocation;
    private JsonObjectRequest jsonObjectRequest;
    private LocationRequest mLocationRequest;
    private LatLng circle_Center = null;

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
                    MY_PERMISSIONS_REQUEST_ACCES_FINE_LOCATION);
        }else {
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
            JSONArray jsonArray = (JSONArray) jsonObject.getJSONArray("records");
            int n = jsonArray.length();
            for(int i = 0 ; i < n ; i++)
                addMarkerToMap((JSONObject) jsonArray.get(i));
            CircleOptions circleOptions = new CircleOptions()
                    .center(circle_Center)
                    .strokeColor(0xff4285F4)
                   // .fillColor(0x60C8D6EC)
                    .radius(400); // In meters
            Circle circle = mMap.addCircle(circleOptions);

        } catch (JSONException e) {
            e.printStackTrace();
            Log.e("Log", "Ouah ca marche aps");
        }

    }
    public void addMarkerToMap(JSONObject jsonObject){
        JSONObject fields = null;
        try {
            fields = (JSONObject) jsonObject.get("fields");
            JSONArray position = (JSONArray) fields.get("position");
            Double l1 = (Double) position.get(0);
            Double l2 = (Double) position.get(1);
            Integer nbVelibDispo = (Integer) fields.get("available_bikes");
            Log.e("lol",nbVelibDispo.toString());
             mMap.addMarker(new MarkerOptions().position(new LatLng(l1,l2))
                    .icon(BitmapDescriptorFactory.fromBitmap(writeTextOnDrawable(R.mipmap.arrow, nbVelibDispo.toString()))));
        } catch (JSONException e) {
            e.printStackTrace();
            Log.d("MarkerOnMap","Error JSON");
        }
    }
    private Bitmap writeTextOnDrawable(int drawableId, String text) {

        Bitmap bm = BitmapFactory.decodeResource(getResources(), drawableId)
                .copy(Bitmap.Config.ARGB_8888, true);

        Typeface tf = Typeface.create("Helvetica", Typeface.BOLD);

        Paint paint = new Paint();
        paint.setStyle(Paint.Style.FILL);
        paint.setColor(Color.WHITE);
        paint.setTypeface(tf);
        paint.setTextAlign(Paint.Align.CENTER);
        paint.setTextSize(convertToPixels(getBaseContext(), 11));

        Rect textRect = new Rect();
        paint.getTextBounds(text, 0, text.length(), textRect);

        Canvas canvas = new Canvas(bm);

        //If the text is bigger than the canvas , reduce the font size
        if(textRect.width() >= (canvas.getWidth() - 4))     //the padding on either sides is considered as 4, so as to appropriately fit in the text
            paint.setTextSize(convertToPixels(getBaseContext(), 7));        //Scaling needs to be used for different dpi's

        //Calculate the positions
        int xPos = (canvas.getWidth() / 2) - 0;     //-2 is for regulating the x position offset

        //"- ((paint.descent() + paint.ascent()) / 2)" is the distance from the baseline to the center.
        int yPos = (int) ((canvas.getHeight() / 2) - ((paint.descent() + paint.ascent()) / 2)) - 4 ;

        canvas.drawText(text, xPos, yPos, paint);

        return  bm;
    }



    public static int convertToPixels(Context context, int nDP){
        final float conversionScale = context.getResources().getDisplayMetrics().density;
        return (int) ((nDP * conversionScale) + 0.5f) ;
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
                LatLng new_CameraPos = mMap.getCameraPosition().target;
                if(new_CameraPos == null || circle_Center == null)
                    return;
                float[] results = new float[10];
                Location.distanceBetween(circle_Center.latitude,circle_Center.longitude,new_CameraPos.latitude,new_CameraPos.longitude,results);
                if(results[0] >= 350){
                    circle_Center = new_CameraPos;
                    mMap.clear();
                    changeVolleyRequest();
                    VolleySingleton.getInstance(getBaseContext()).addToRequestQueue(jsonObjectRequest);

                }
            }

            
        });
    }
    public synchronized  void buildAndConnectGoogleApiClient(){
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
        mGoogleApiClient.connect();
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

    public void updatePos(Location l){
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_ACCES_FINE_LOCATION: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Log.d("PERMIISSION","Jai la permission");
                    initMapAsync();

                    // Access the RequestQueue through your singleton class.
                    VolleySingleton.getInstance(this).addToRequestQueue(jsonObjectRequest);

                } else {
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
        if(circle_Center == null){
            circle_Center = pos;
            mMap.moveCamera(CameraUpdateFactory.newLatLng(circle_Center));
            mMap.animateCamera(CameraUpdateFactory.zoomTo(14),2000,null);
        }
        updatePos(mLastLocation);

    }
    @SuppressWarnings("MissingPermission")
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

}












