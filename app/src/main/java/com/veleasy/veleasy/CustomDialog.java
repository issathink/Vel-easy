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
    private ImageView fav, unFav;
    private TextView text;

    public CustomDialog(Activity a) {
        super(a);
        this.c = a;
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
        unFav = (ImageView) findViewById(R.id.unfav_button);
        findViewById(R.id.relative).setOnClickListener(this);

        fav.setOnClickListener(this);
        unFav.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.fav_button:
                Toast.makeText(getContext(), "Fav", Toast.LENGTH_SHORT).show();
                break;
            case R.id.unfav_button:
            //    Uri gmmIntentUri = Uri.parse("geo:0,0?q="+text.getText());
                Uri gmmIntentUri = Uri.parse("google.navigation:q="+text.getText()+"&mode=w");
                Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
                mapIntent.setPackage("com.google.android.apps.maps");
                c.startActivity(mapIntent);
                break;
            default:
                cancel();
        }
    }
}
