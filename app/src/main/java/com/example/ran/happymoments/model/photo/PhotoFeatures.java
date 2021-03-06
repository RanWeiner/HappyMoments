package com.example.ran.happymoments.model.photo;

import android.support.media.ExifInterface;
import android.util.Log;

import com.example.ran.happymoments.common.AppConstants;

import org.opencv.core.Mat;
import org.opencv.core.MatOfFloat;
import org.opencv.core.MatOfInt;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;

import static android.support.constraint.Constraints.TAG;

public class PhotoFeatures {

    private Calendar dateTime;
    private PhotoLocation photoLocation;
    private Mat histogram;
    private String orientation;


    public PhotoFeatures(ExifInterface exifInterface , Mat histogram) {
        setDate(exifInterface);
        setPhotoLocation(exifInterface);
        setOrientation(exifInterface);
        setHistogram(histogram);
    }


    private void setHistogram(Mat histogram) {
        this.histogram = histogram;
    }

    private void setOrientation(ExifInterface exifInterface) {
        this.orientation = exifInterface.getAttribute(ExifInterface.TAG_ORIENTATION);
    }


    private void setPhotoLocation(ExifInterface exifInterface) {
        float[] coordinates = {0, 0};
        if (exifInterface.getLatLong(coordinates)) {
            this.photoLocation = new PhotoLocation(coordinates[0], coordinates[1]);
        } else {
            this.photoLocation = null;
        }
    }

    private void setDate(ExifInterface exifInterface) {

        String dateString = exifInterface.getAttribute(ExifInterface.TAG_DATETIME);
        if (dateString != null) {
            this.dateTime = StringToCalendar(dateString);
        }
    }

    public Calendar getDateTime() {
        return dateTime;
    }

    public PhotoLocation getPhotoLocation() {
        return photoLocation;
    }

    public Mat getHistogram() {
        return histogram;
    }

    public String getOrientation() {
        return orientation;
    }

    private Calendar StringToCalendar(String dateString) {
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat format = new SimpleDateFormat("yyyy:MM:dd HH:mm:ss");
        try {
            calendar.setTime(format.parse(dateString));
        } catch (ParseException e) {
            Log.d("Exception:", "Parsing went wrong! " + dateString);
            e.printStackTrace();
        }
        return calendar;
    }


    public double calcDistanceDiffByMeters (PhotoFeatures other){

            if (photoLocation == null && other.getPhotoLocation() == null) {
                return 0;
            }

            if (photoLocation == null || other.getPhotoLocation() == null) {
                return 1;
            }

            double distance = calcDistanceBetweenLocations(photoLocation, other.getPhotoLocation());

            return (distance >= AppConstants.MAX_DISTANCE_DIFF) ? 1 : distance / AppConstants.MAX_DISTANCE_DIFF;

        }


        public double calcTimeDiffBySeconds (PhotoFeatures other){
            if (this.dateTime == null && other.getDateTime() == null) {
                return 0;
            }
            if (this.dateTime == null || other.getDateTime() == null) {
                return 1;
            }
            double diff = Math.abs(this.getDateTime().getTimeInMillis() - other.getDateTime().getTimeInMillis());
            diff = (diff /1000); //millis to seconds
            return (diff >= AppConstants.MAX_SECONDS_DIFF) ? 1 : diff / AppConstants.MAX_SECONDS_DIFF;
        }


        public double calcDistanceBetweenLocations (PhotoLocation p1, PhotoLocation p2){
            double theta = p1.getLongitude() - p2.getLongitude();
            double dist = Math.sin(deg2rad(p1.getLatitude())) * Math.sin(deg2rad(p2.getLatitude())) + Math.cos(deg2rad(p1.getLatitude())) * Math.cos(deg2rad(p2.getLatitude())) * Math.cos(deg2rad(theta));
            dist = Math.acos(dist);
            dist = rad2deg(dist);
            dist = dist * 60 * 1.1515;
            dist = dist * 1.609344;
            return dist * 1000;
        }


        public double rad2deg ( double rad){
            return (rad * 180 / Math.PI);
        }


        public double deg2rad ( double deg){
            return (deg * Math.PI / 180.0);
        }

}
