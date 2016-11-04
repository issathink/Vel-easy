package com.veleasy.veleasy;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by issa on 18/10/2016.
 */

public class CustomDialog extends Dialog implements View.OnClickListener {

    private Activity c;
    private ImageView fav, showRoute;
    private TextView text;
    private int number;
    private boolean favorited;
    private String name;
    private FavObject favObject;
    private ArrayList<FavObject> favs;
    private int favSize;
    private SharedPreferences shared;

    public CustomDialog(Activity a) {
        super(a);
        this.c = a;
        this.number = -1;
        this.favorited = false;
        this.favObject = null;
        shared = c.getSharedPreferences(Tools.FAV, Context.MODE_PRIVATE);
        List<String> favNames = new ArrayList<>(shared.getStringSet(Tools.FAV_NAMES, new HashSet<String>()));
        List<String> favNumbers = new ArrayList<>(shared.getStringSet(Tools.FAV_NUMBERS, new HashSet<String>()));
        favs = new ArrayList<>();
        for(int i = 0; i < favNames.size(); i++)
            favs.add(new FavObject(favNames.get(i), Integer.parseInt(favNumbers.get(i))));
        favSize = favs.size();
    }

    public void init(int number, String name) {
        this.number = number;
        this.name = name;
        favObject = new FavObject(name, number);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.custom_dialog);
        WindowManager.LayoutParams wmlp = getWindow().getAttributes();
        wmlp.width = android.view.WindowManager.LayoutParams.MATCH_PARENT;
        wmlp.height = android.view.WindowManager.LayoutParams.WRAP_CONTENT;
        setCanceledOnTouchOutside(true);
        text = (TextView) findViewById(R.id.textview);
        fav = (ImageView) findViewById(R.id.fav_button);
        showRoute = (ImageView) findViewById(R.id.show_route_button);
        findViewById(R.id.relative).setOnClickListener(this);

        if(isInFavs(number)) {
            fav.setImageResource(R.mipmap.favfull);
            favorited = true;
        }

        fav.setOnClickListener(this);
        showRoute.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.fav_button:
                // Toast.makeText(getContext(), "Fav", Toast.LENGTH_SHORT).show();
                if(favorited) {
                    if(unFav(favObject)) {
                        favorited = false;
                        fav.setImageResource(R.mipmap.fav);
                    } else {
                        Toast.makeText(getContext(), "Sorry unexpected error try again later.", Toast.LENGTH_LONG).show();
                    }
                } else {
                    if(fav(favObject)) {
                        favorited = true;
                        fav.setImageResource(R.mipmap.favfull);
                    } else {
                        Toast.makeText(getContext(), "Sorry you can't have more than 5 favorite stations.", Toast.LENGTH_LONG).show();
                    }
                }
                break;
            case R.id.show_route_button:
                // Uri gmmIntentUri = Uri.parse("geo:0,0?q="+text.getText());
                Uri gmmIntentUri = Uri.parse("google.navigation:q="+text.getText()+"&mode=b");
                Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
                mapIntent.setPackage("com.google.android.apps.maps");
                c.startActivity(mapIntent);
                break;
            default:
                cancel();
        }
    }


    public boolean isInFavs(int number) {
        if(favSize <= 0)
            return false;
        for(FavObject o: favs)
            if(o.getNumber() == number)
                return true;
        return false;
    }

    public boolean fav(FavObject favObject) {
        if(favSize >= 5)
            return false;
        favs.add(favObject);

        Set<String> names = new HashSet<>();
        Set<String> numbers = new HashSet<>();
        for(int i=0; i<favs.size(); i++) {
            names.add(favs.get(i).getName());
            numbers.add("" + favs.get(i).getNumber());
        }
        shared.edit().putStringSet(Tools.FAV_NAMES, names).apply();
        shared.edit().putStringSet(Tools.FAV_NUMBERS, numbers).apply();
        favSize = favs.size();

        return true;
    }

    public boolean unFav(FavObject favObject) {
        int index = -1;
        for(int i=0; i<favs.size(); i++)
            if(favObject.getNumber().intValue() == favs.get(i).getNumber().intValue())
                index = i;
        Log.e("WHAT", "unFav don't find: " + favObject + " i: " + index);
        if(index != -1) {
            favs.remove(index);
            favSize = favs.size();
            Set<String> names = new HashSet<>();
            Set<String> numbers = new HashSet<>();
            for(FavObject f: favs) {
                names.add(f.getName());
                numbers.add(f.getNumber()+ "");
            }
            shared.edit().putStringSet(Tools.FAV_NAMES, names).apply();
            shared.edit().putStringSet(Tools.FAV_NUMBERS, numbers).apply();
            return true;
        }
        return false;
    }

}
