package com.veleasy.veleasy;

import android.util.Log;

import com.google.android.gms.maps.model.LatLng;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by dasilva.vic on 14/10/2016.
 */

public class Station {
    private String status;
    private int bikeStands;
    private int availableBikeStand;
    private boolean banking;
    private int availableBike;
    private String addressName;
    private LatLng position;

    public Station(String status, int bikeStands, int availableBikeStand, boolean banking, int availableBike, String addressName, LatLng position) {
        this.status = status;
        this.bikeStands = bikeStands;
        this.availableBikeStand = availableBikeStand;
        this.banking = banking;
        this.availableBike = availableBike;
        this.addressName = addressName;
        this.position = position;
    }

    public String isStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public int getBikeStands() {
        return bikeStands;
    }

    public void setBikeStands(int bikeStands) {
        this.bikeStands = bikeStands;
    }

    public int getAvailableBikeStand() {
        return availableBikeStand;
    }

    public void setAvailableBikeStand(int availableBikeStand) {
        this.availableBikeStand = availableBikeStand;
    }

    public boolean isBanking() {
        return banking;
    }

    public void setBanking(boolean banking) {
        this.banking = banking;
    }

    public int getAvailableBike() {
        return availableBike;
    }

    public void setAvailableBike(int availableBike) {
        this.availableBike = availableBike;
    }

    public String getAddressName() {
        return addressName;
    }

    public void setAddressName(String addressName) {
        this.addressName = addressName;
    }

    public LatLng getPosition() {
        return position;
    }

    public void setPosition(LatLng position) {
        this.position = position;
    }

    public static Station getStation(JSONObject jsonObject) {
        try {
            JSONObject fields = (JSONObject) jsonObject.get("fields");
            JSONArray position = (JSONArray) fields.get("position");
            LatLng pos =new LatLng((Double)position.get(0),(Double)position.get(1));
            Integer nbVelibDispo = (Integer) fields.get("available_bikes");
            String addressName = (String) fields.get("address");
            String tmpBanking = (String) fields.get("banking");
            boolean banking = tmpBanking.contains("True");
            Integer nbStandDispo = (Integer) fields.get("available_bike_stands");
            Integer nbStands = (Integer) fields.get("bike_stands");
            String status = (String) fields.get("status");

            return new Station(status,nbStands,nbStandDispo,banking,nbVelibDispo,addressName,pos);
        } catch (JSONException e) {
            e.printStackTrace();
            Log.d("MarkerOnMap","Error JSON");
        }
        return null;
    }

}
