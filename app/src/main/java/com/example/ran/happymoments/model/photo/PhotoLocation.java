package com.example.ran.happymoments.model.photo;

public class PhotoLocation {
    private float latitude, longitude;


    public PhotoLocation(float latitude , float longitude){
        this.latitude = latitude;
        this.longitude = longitude;
    }


    public float getLatitude() {
        return latitude;
    }

    public float getLongitude() {
        return longitude;
    }

}
