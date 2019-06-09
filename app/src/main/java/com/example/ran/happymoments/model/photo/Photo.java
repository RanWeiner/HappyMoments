package com.example.ran.happymoments.model.photo;

import android.support.media.ExifInterface;

import org.opencv.core.Mat;

import java.util.List;


public class Photo {
    private String mPath;
    private PhotoFeatures mFeatures;
    private ExifInterface mExif;
    private List<Person> mPersons;
    private double mRank;


    public Photo(String path, ExifInterface exifInterface, List<Person> persons , Mat histogram){
        mPath = path;
        mExif = exifInterface;
        mPersons = persons;
        mRank = 0;
        setPhotoFeatures(histogram);
    }


    public String getPath() {
        return this.mPath;
    }


    public void setPhotoFeatures(Mat histogram) {
        if (mExif != null){
            mFeatures = new PhotoFeatures(mExif , histogram);
        }
    }


    public PhotoFeatures getFeatures() {
        return mFeatures;
    }

    public ExifInterface getExif() {
        return mExif;
    }


    public List<Person> getPersons() {
        return mPersons;
    }


    public void addPerson(Person person) {
        this.mPersons.add(person);
    }

    public double getRank() {
        return mRank;
    }

    public void setRank(double mRank) {
        this.mRank = mRank;
    }

    public int getNumOfPersons() {
        return mPersons.size();
    }
}
