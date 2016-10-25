package com.veleasy.veleasy;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

public class FavActivity extends AppCompatActivity {

    SharedPreferences shared;
    ListView listView;
    FavAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fav);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        shared = getSharedPreferences(Tools.FAV, Context.MODE_PRIVATE);
        listView = (ListView) findViewById(R.id.favList);
        adapter = new FavAdapter(this, R.layout.fav_item, getFavs());
        listView.setAdapter(adapter);
        int val = shared.getInt(Tools.FAV_COUNT, 0);

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
        ArrayList<FavObject> list = new ArrayList<>();
        list.add(new FavObject("Paris 5", 5));
        list.add(new FavObject("Paris 6", 6));
        return list;
    }

    public void updateFavs(List<FavObject> list) {
        for(FavObject obj: list)
            Log.e("UPDATE FAVS", obj.toString());
    }

    public void deleteFav(FavObject item) {
        adapter.remove(item);
    }
}
