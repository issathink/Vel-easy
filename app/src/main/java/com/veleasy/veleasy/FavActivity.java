package com.veleasy.veleasy;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageButton;

public class FavActivity extends AppCompatActivity {

    SharedPreferences shared;
    ImageButton del1, del2, del3, del4, del5;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fav);

        shared = getSharedPreferences(Tools.FAV, Context.MODE_PRIVATE);
        del1 = 

    }


}
