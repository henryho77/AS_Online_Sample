package com.example.mapsample.model;

import com.google.android.gms.maps.model.LatLng;
import com.google.gson.JsonObject;

/**
 * Created by HenryHo on 2017/5/8.
 */

public class OldPlace {

    String id;
    LatLng latLng;
    JsonObject openingHours;

    public OldPlace() {}

    public OldPlace(String id, LatLng latlng, JsonObject openingHours) {
        if (id != null) {
            this.id = id;
        }

        if(latlng != null) {
            this.latLng = latlng;
        }

        if (openingHours != null) {
            this.openingHours = openingHours;
        }
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public LatLng getLatLng() {
        return latLng;
    }

    public void setLatLng(LatLng latLng) {
        this.latLng = latLng;
    }

    public JsonObject getOpeningHours() {
        return openingHours;
    }

    public void setOpeningHours(JsonObject openingHours) {
        this.openingHours = openingHours;
    }
}
