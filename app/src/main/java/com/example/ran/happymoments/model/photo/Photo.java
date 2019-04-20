package com.example.ran.happymoments.model.photo;

import android.support.media.ExifInterface;

import java.util.List;


public class Photo {
    private String mPath;
    private PhotoFeatures mFeatures;
    private ExifInterface mExif;
    private List<Person> mPersons;
    private double mRank;


    public Photo(String path, ExifInterface exifInterface, List<Person> persons){
        mPath = path;
        mExif = exifInterface;
        mPersons = persons;
        mRank = 0;
        setPhotoFeatures();
    }



    //just for trying!!!
    public Photo(String path, PhotoFeatures features, List<Person> persons) {
        mPath = path;
        mFeatures = features;
        mPersons = persons;
    }




    public String getPath() {
        return this.mPath;
    }


    public void setPhotoFeatures() {
        if (mExif != null){
            mFeatures = new PhotoFeatures(mPath , mExif);
        }
    }


    public boolean similarTo(Photo other){
       // return mFeatures.compareFeatures(other.getFeatures());
        return mFeatures.compareFeatures(other.getFeatures());
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
