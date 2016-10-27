package com.veleasy.veleasy;

import android.content.Context;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONObject;

import java.util.List;

/**
 * Created by issa on 25/10/2016.
 */

public class FavAdapter extends ArrayAdapter<FavObject> {

    private List<FavObject> objects;
    private String url = "http://opendata.paris.fr/api/records/1.0/search/?dataset=stations-velib-disponibilites-en-temps-reel&q=number:";


    public FavAdapter(Context context, int resource, List<FavObject> objects) {
        super(context, resource, objects);
        this.objects = objects;
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate(R.layout.fav_item, null);
            ViewHolder holder = new ViewHolder();
            holder.textView = (TextView) convertView.findViewById(R.id.favText);
            holder.imageButton = (ImageButton) convertView.findViewById(R.id.favDel);
            holder.nbPlaces = (TextView) convertView.findViewById(R.id.nbPlaces);
            holder.nbVelib = (TextView) convertView.findViewById(R.id.nbVelib);
            holder.parking = (ImageView) convertView.findViewById(R.id.parking);
            holder.velib = (ImageView) convertView.findViewById(R.id.velib);
            convertView.setTag(holder);
        }

        final ViewHolder holder = (ViewHolder) convertView.getTag();
        final FavObject item = getItem(position);

        url += item.getNumber();

        holder.textView.setText(item.getName());
        holder.nbVelib.setText(item.getNbVelib());
        holder.nbPlaces.setText(item.getNbPlaces());
        holder.imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getContext(), "Deleting " + item, Toast.LENGTH_SHORT).show();
                Log.e("DELETE FAV", "deleting " + item);
                objects.remove(item);
                ((FavActivity) getContext()).deleteFav(item);
                ((FavActivity) getContext()).updateFavs(objects);
            }
        });

        new JsonObjectRequest
                (Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        ((FavActivity)getContext()).upateFav(item, Station.getStation(response));
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e("mba", "TozLose");
                    }
                });

        return convertView;
    }

    private static class ViewHolder {
        private TextView textView, nbPlaces, nbVelib;
        private ImageButton imageButton;
        private ImageView parking, velib;
    }

}
