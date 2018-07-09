package com.android.nfc.smartpay_v3.Classes;

import com.google.android.gms.maps.model.LatLng;

/**
 * Created by Fifty on 6/7/2018.
 */

public class Companey {
    String Name;
    int Type;
    LatLng CompaneyLocation;

    public Companey(String name, int type, LatLng companeyLocation) {
        Name = name;
        Type = type;
        CompaneyLocation = companeyLocation;
    }

    public Companey() {

    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public int getType() {
        return Type;
    }

    public void setType(int type) {
        Type = type;
    }

    public LatLng getCompaneyLocation() {
        return CompaneyLocation;
    }

    public void setCompaneyLocation(LatLng companeyLocation) {
        CompaneyLocation = companeyLocation;
    }
}
