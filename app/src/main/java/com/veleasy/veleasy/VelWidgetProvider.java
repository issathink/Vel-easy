package com.veleasy.veleasy;

import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.widget.RemoteViews;


import org.json.JSONObject;

/**
 * Created by Moussa on 18/10/2016.
 */

public class VelWidgetProvider extends AppWidgetProvider {

    private String info_label;
    private String info_title;
    private SharedPreferences prefs;

    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {

        prefs = PreferenceManager.getDefaultSharedPreferences(context);

        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.velwidget_layout);
        prefs.getString("",info_title);
        prefs.getString("",info_label);
        // méthode permettant de mettre à jour les textViews
        views.setTextViewText(R.id.label,info_label);
        views.setTextViewText(R.id.title,info_title);
    }
}
