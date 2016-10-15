package com.veleasy.veleasy;

import com.google.android.gms.maps.model.LatLng;

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
}
