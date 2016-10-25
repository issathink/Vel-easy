package com.veleasy.veleasy;

import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.RemoteViews;


import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONObject;

/**
 * Created by Moussa on 18/10/2016.
 */

public class VelWidgetProvider extends AppWidgetProvider {

    private String[] info_label;
    private String[] info_title;
    private String nb_prefs;
    private SharedPreferences prefs;
    private RemoteViews views;
    private JsonObjectRequest jsonObjectRequest;
    private String URL = "http://opendata.paris.fr/api/records/1.0/search/?dataset=stations-velib-disponibilites-en-temps-reel&geofilter.distance=48.855221%2C2.347919%2C400";


    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {

        prefs = PreferenceManager.getDefaultSharedPreferences(context);
        views = new RemoteViews(context.getPackageName(), R.layout.velwidget_layout);


        prefs.getString("FAV_COUNT",nb_prefs);
        if(nb_prefs == null)return;

       // prefs.getString("",info_title);
       // prefs.getString("",info_label);

        jsonObjectRequest = new JsonObjectRequest
                (Request.Method.GET, URL, null, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        callApi(response);
                    }
                }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e("mba","TozLose");

                    }
                });


        // méthode permettant de mettre à jour les textViews
      //  views.setTextViewText(R.id.label,info_label);
      //  views.setTextViewText(R.id.title,info_title);
    }


    public void callApi(JSONObject jsonObject){
    /*    try{

        }
*/    }
}
