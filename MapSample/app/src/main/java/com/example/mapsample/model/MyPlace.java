package com.example.mapsample.model;

import com.google.android.gms.maps.model.LatLng;
import com.google.gson.JsonObject;

/**
 * Created by HenryHo on 2017/5/9.
 */

public class MyPlace {
    String placeId;
    LatLng latLng;
    String name;
    String address;
    String icon;
    JsonObject openingHours;

    public MyPlace() {}

    public MyPlace(String placeId, LatLng latlng, String name, String address, String icon, JsonObject openingHours) {
        if (placeId != null) {
            this.placeId = placeId;
        }
        if(latlng != null) {
            this.latLng = latlng;
        }
        if (name != null) {
            this.name = name;
        }
        if (address != null) {
            this.address = address;
        }
        if (icon != null) {
            this.icon = icon;
        }
        if (openingHours != null) {
            this.openingHours = openingHours;
        }
    }

    public String getPlaceId() {
        return placeId;
    }

    public void setPlaceId(String placeId) {
        this.placeId = placeId;
    }

    public LatLng getLatLng() {
        return latLng;
    }

    public void setLatLng(LatLng latLng) {
        this.latLng = latLng;
    }

    public String getName() {
        return name;
    }

    public void setName (String name) {
        this.name = name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getIcon () {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public JsonObject getOpeningHours() {
        return openingHours;
    }

    public void setOpeningHours(JsonObject openingHours) {
        this.openingHours = openingHours;
    }
}
