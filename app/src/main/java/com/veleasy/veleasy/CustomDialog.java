package com.veleasy.veleasy;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

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

    public CustomDialog(Activity a) {
        super(a);
        this.c = a;
        this.number = -1;
        this.favorited = false;
        this.favObject = null;
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

        if(((FavActivity) getContext()).isInFavs(number)) {
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
                    if(((FavActivity) getContext()).unFav(favObject)) {
                        favorited = false;
                        fav.setImageResource(R.mipmap.fav);
                    } else {
                        Toast.makeText(getContext(), "Sorry cannot add cannot unfavorite.", Toast.LENGTH_LONG).show();
                    }
                } else {
                    if(((FavActivity) getContext()).fav(favObject)) {
                        favorited = true;
                        fav.setImageResource(R.mipmap.favfull);
                    } else {
                        Toast.makeText(getContext(), "Sorry cannot add it to favorites.", Toast.LENGTH_LONG).show();
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

}
