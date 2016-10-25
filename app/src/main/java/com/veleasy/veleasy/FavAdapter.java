package com.veleasy.veleasy;

import android.content.Context;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

/**
 * Created by issa on 25/10/2016.
 */

public class FavAdapter extends ArrayAdapter<FavObject> {

    private List<FavObject> objects;

    public FavAdapter(Context context, int resource, List<FavObject> objects) {
        super(context, resource, objects);
        this.objects = objects;
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if(convertView == null) {
            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate(R.layout.fav_item, null);
            ViewHolder holder = new ViewHolder();
            holder.textView = (TextView) convertView.findViewById(R.id.favText);
            holder.imageButton = (ImageButton) convertView.findViewById(R.id.favDel);
            convertView.setTag(holder);
        }

        final ViewHolder holder = (ViewHolder)convertView.getTag();
        final FavObject item = getItem(position);

        holder.textView.setText(item.getName());
        holder.imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getContext(), "Deleting " + item, Toast.LENGTH_SHORT).show();
                Log.e("DELETE FAV" , "deleting " + item);
                objects.remove(item);
                ((FavActivity)getContext()).deleteFav(item);
                ((FavActivity)getContext()).updateFavs(objects);
            }
        });

        return convertView;
    }

    private static class ViewHolder {
        public TextView textView;
        public ImageButton imageButton;
    }

}
