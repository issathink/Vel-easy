package com.veleasy.veleasy;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class FavActivity extends AppCompatActivity {

    SharedPreferences shared;
    ListView listView;
    FavAdapter adapter;
    ArrayList<FavObject> favs;
    int favSize;
    private JsonObjectRequest jsonObjectRequest;
    private String url = "http://opendata.paris.fr/api/records/1.0/search/?dataset=stations-velib-disponibilites-en-temps-reel&q=number:";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fav);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        favs = new ArrayList<>();
        shared = getSharedPreferences(Tools.FAV, Context.MODE_PRIVATE);
        listView = (ListView) findViewById(R.id.favList);
        favs = getFavs();
        adapter = new FavAdapter(this, R.layout.fav_item, favs);
        listView.setAdapter(adapter);
        favSize = favs.size();
        String s = "";

        for (int i = 0; i < favs.size(); i++) {
            s = url + favs.get(i).getNumber();
            jsonObjectRequest = new JsonObjectRequest
                    (Request.Method.GET, s, null, new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            try {
                                JSONArray jsonArray = response.getJSONArray("records");
                                if (jsonArray.length() > 0) {
                                    Log.e("RESPONSE", "Response: " + Station.getStation((JSONObject) jsonArray.get(0)).getAvailableBike());
                                    upateFav(Station.getStation((JSONObject) jsonArray.get(0)));
                                }
                            } catch (JSONException e) {
                                Log.e("ERROR", "Why " + e.getMessage());
                            }
                        }
                    }, new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            Log.e("mba", "TozLose");
                            Toast.makeText(getApplicationContext(), "" + error, Toast.LENGTH_SHORT).show();
                        }
                    });
            VolleySingleton.getInstance(getApplicationContext()).addToRequestQueue(jsonObjectRequest);
        }

        if (favSize <= 0)
            ((TextView) findViewById(R.id.favText)).setText(R.string.no_favorite_message);
        else
            findViewById(R.id.favText).setVisibility(View.GONE);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public ArrayList<FavObject> getFavs() {
        List<String> favNames = new ArrayList<>(shared.getStringSet(Tools.FAV_NAMES, new HashSet<String>()));
        List<String> favNumbers = new ArrayList<>(shared.getStringSet(Tools.FAV_NUMBERS, new HashSet<String>()));
        favs = new ArrayList<>();
        for (int i = 0; i < favNames.size(); i++)
            favs.add(new FavObject(favNames.get(i), Integer.parseInt(favNumbers.get(i))));
        return favs;
    }

    public void updateFavs(List<FavObject> list) {
        Set<String> favNames = new HashSet<>();
        Set<String> favNumbers = new HashSet<>();

        // Toast.makeText(getApplicationContext(), "s Update", Toast.LENGTH_SHORT).show();
        for (FavObject obj : list) {
            Log.e("UPDATE FAVS", obj.toString());
            favNames.add(obj.getName());
            favNumbers.add(obj.getNumber() + "");
        }
        favSize = list.size();
        shared.edit().putStringSet(Tools.FAV_NAMES, favNames).apply();
        shared.edit().putStringSet(Tools.FAV_NUMBERS, favNumbers).apply();
    }

    public void deleteFav(FavObject item) {
        adapter.remove(item);
        favs.remove(item);
        updateFavs(favs);
    }

    public void upateFav(Station station) {
        // Toast.makeText(getApplicationContext(), "Update fav", Toast.LENGTH_SHORT).show();
        for (FavObject f : favs) {
            if (f.getNumber().intValue() == station.getNumber().intValue()) {
                f.setNbPlaces(station.getAvailableBikeStand());
                f.setNbVelib(station.getAvailableBike());
            }
        }
        adapter.notifyDataSetChanged();
    }

}
