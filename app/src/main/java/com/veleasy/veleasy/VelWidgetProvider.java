package com.veleasy.veleasy;

import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.widget.RemoteViews;
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

/**
 * Created by Moussa on 18/10/2016.
 */

public class VelWidgetProvider extends AppWidgetProvider {

    private String[] info_pos;
    private String[] info_num;
    private SharedPreferences prefs;
    private RemoteViews views;
    private JsonObjectRequest jsonObjectRequest;
    private String URL_principale = "http://opendata.paris.fr/api/records/1.0/search/?dataset=stations-velib-disponibilites-en-temps-reel&q=number:";

    @Override
    public void onReceive(Context context, Intent intent) {
        // TODO Auto-generated method stub
        super.onReceive(context, intent);
        Log.d("Widgetz","azeaze");
        Bundle extras = intent.getExtras();
        if(extras!=null) {
            Log.d("Widgetz","azeazzaee");

            AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
            ComponentName thisAppWidget = new ComponentName(context.getPackageName(), VelWidgetProvider.class.getName());
            int[] appWidgetIds = appWidgetManager.getAppWidgetIds(thisAppWidget);

            onUpdate(context, appWidgetManager, appWidgetIds);
        }
    }
    AppWidgetManager appMock = null;
    int[] appId= null;
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        Log.d("Wdiget","toz");
        appMock = appWidgetManager;
        appId = appWidgetIds;
        prefs = context.getSharedPreferences(Tools.FAV, Context.MODE_PRIVATE);
        views = new RemoteViews(context.getPackageName(), R.layout.velwidget_layout);

        List<String> favNames = new ArrayList<>(prefs.getStringSet(Tools.FAV_NAMES, new HashSet<String>()));
        List<String> favNumbers = new ArrayList<>(prefs.getStringSet(Tools.FAV_NUMBERS, new HashSet<String>()));

        if(favNames.size() == 0 || favNumbers.size()==0)return;
        info_pos = new String[2];
        info_num = new String[2];

       for(int i=0;i<2;i++){
           info_pos[i] = favNames.get(i);
           info_num[i] = favNumbers.get(i);
           Log.d("test"+i,info_pos[i]);
           Log.d("test"+i,info_num[i]);
           Toast.makeText(context,"dfeefe",Toast.LENGTH_LONG);
       }



        views.setTextViewText(R.id.fav1,info_pos[0]);
        views.setTextViewText(R.id.fav2,info_pos[1]);

        String Urlloc1 = URL_principale+info_num[0];
        String Urlloc2 = URL_principale+info_num[1];
        jsonObjectRequest = new JsonObjectRequest
                (Request.Method.GET, Urlloc1, null, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            JSONArray jsonArray = response.getJSONArray("records");
                            JSONObject obj = (JSONObject)jsonArray.get(0);
                            if(jsonArray.length()>0) {
                                JSONObject fields = obj.getJSONObject("fields");
                                Log.d("fields",fields.toString());
                                String nbStandDispo = fields.get("available_bike_stands")+"";
                                Log.d("nbStandDispo",nbStandDispo);
                                String nbStands = fields.get("bike_stands")+"";
                                Log.d("nbStands",nbStands);
                                views.setTextViewText(R.id.nbVelib1, nbStandDispo);
                                views.setTextViewText(R.id.nbPlace1, nbStands);
                                for(int i=0;i<appId.length;i++)
                                    appMock.updateAppWidget(appId[i], views);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e("mba", "TozLose");

                    }
                });
        VolleySingleton.getInstance(context).addToRequestQueue(jsonObjectRequest);
        jsonObjectRequest = new JsonObjectRequest
                (Request.Method.GET, Urlloc2, null, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            JSONArray jsonArray = response.getJSONArray("records");
                            JSONObject obj = (JSONObject)jsonArray.get(0);
                            if(jsonArray.length()>0) {
                                JSONObject fields = obj.getJSONObject("fields");
                                Log.d("fields",fields.toString());
                                String nbStandDispo = fields.get("available_bike_stands")+"";
                                Log.d("nbStandDispo",nbStandDispo);
                                String nbStands = fields.get("bike_stands")+"";
                                Log.d("nbStands",nbStands);
                                views.setTextViewText(R.id.nbVelib2, nbStandDispo);
                                views.setTextViewText(R.id.nbPlace2, nbStands);
                                for(int i=0;i<appId.length;i++)
                                    appMock.updateAppWidget(appId[i], views);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e("mba", "TozLose");

                    }
                });
        VolleySingleton.getInstance(context).addToRequestQueue(jsonObjectRequest);

        for(int i=0;i<appWidgetIds.length;i++)
            appWidgetManager.updateAppWidget(appWidgetIds[i], views);
        // méthode permettant de mettre à jour les textViews
        //  views.setTextViewText(R.id.label,info_label);
        //  views.setTextViewText(R.id.title,info_title);
    }
}
