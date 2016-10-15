package com.veleasy.veleasy;

import com.google.android.gms.maps.model.LatLng;

/**
 * Created by issa on 14/10/2016.
 */

public class Station {
    private Integer nbStands, nbStandDispo, nbVelibDispo;
    private String status, addressName;
    private boolean banking;
    LatLng pos;

    public Station(String status, Integer nbStands, Integer nbStandDispo, boolean banking, Integer nbVelibDispo, String addressName, LatLng pos) {

    }

    public Integer getAvailableBike() {
        return nbStandDispo;
    }

    public Integer getAvailableBikeStand() {
        return nbStandDispo;
    }


}
