package com.example.trafficscotland;

import android.graphics.Bitmap;

import com.google.android.gms.maps.model.LatLng;

public class MyMarkerData {
    LatLng latLng;
    String title;
    Bitmap bitmap;

    public LatLng getLatLng() {
        return latLng;
    }

    public void setLatLng(LatLng latLng) {
        this.latLng = latLng;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Bitmap getBitmap() {
        return bitmap;
    }

    public void setBitmap(Bitmap bitmap) {
        this.bitmap = bitmap;
    }
}