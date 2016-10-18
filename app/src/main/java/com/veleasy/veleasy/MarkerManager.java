package com.veleasy.veleasy;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.util.Log;
import android.widget.TextView;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.Map;

/**
 * Created by issa on 18/10/2016.
 */

public class MarkerManager implements GoogleMap.OnMarkerClickListener {

    private Context context;
    private Activity activity;

    public MarkerManager(Context context, Activity activity) {
        this.context = context;
        this.activity = activity;
    }

    @Override
    public boolean onMarkerClick(Marker marker) {
        Log.d("LOGS", marker.getPosition().toString());
        CustomDialog customDialog = new CustomDialog(activity);
        customDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        customDialog.show();

        for (Map.Entry<Station, Marker> entry : ((MapsActivity) activity).getCachedStation().entrySet()) {
            LatLng st = entry.getValue().getPosition();
            if (st.latitude == marker.getPosition().latitude && st.longitude == marker.getPosition().longitude)
                ((TextView) customDialog.findViewById(R.id.textview)).setText(entry.getKey().getAddressName());
        }

        return true;
    }

    public static MarkerOptions getNewMarker(Context context, LatLng pos, int bitmapId, Integer numberToShow) {
        return new MarkerOptions().position(pos)
                .icon(BitmapDescriptorFactory
                        .fromBitmap(Tools.writeTextOnDrawable(context, bitmapId, numberToShow.toString())));
    }

}
