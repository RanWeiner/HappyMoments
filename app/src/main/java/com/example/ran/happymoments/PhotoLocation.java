package com.example.ran.happymoments;

public class PhotoLocation {
    float latitude, longitude;


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
