package com.example.ran.happymoments.service;


import android.util.Log;

import com.example.ran.happymoments.common.Position;
import com.example.ran.happymoments.model.photo.Person;
import com.example.ran.happymoments.model.photo.Photo;
import com.example.ran.happymoments.model.series.PhotoSeries;

import org.opencv.imgproc.Imgproc;

import java.util.ArrayList;
import java.util.List;

public class FaceMatcherImpl implements FaceMatcher {


    public FaceMatcherImpl() {

    }


//    public void matchPersons(PhotoSeries series) {
//        List<Person> basePersons , anyPersons;
//        basePersons = series.getPhoto(0).getPersons();
//        int numOfPhotos = series.getNumOfPhotos();
//
//        for (int photoIdx = 1 ; photoIdx < numOfPhotos ; photoIdx++) {
//            anyPersons = series.getPhoto(photoIdx).getPersons();
//            FaceMatcherImpl.findMatch(basePersons , anyPersons);
//        }
//    }

    public void matchPersons(PhotoSeries series) {

        Photo basePhoto = series.getMaxNumOfPersonsPhoto();

        for(Person person : basePhoto.getPersons()) {

            for (Photo p : series.getPhotos()) {
                if (basePhoto != p) {
                    matchBasePersonToMinDistance(person , p.getPersons());
                }
            }
        }
    }

    private void matchBasePersonToMinDistance(Person basePerson, List<Person> otherPersons) {
        double currentDistance;
        for (Person p: otherPersons) {

            currentDistance = calculateDistance(basePerson , p);

            if (currentDistance < p.getMinDistanceFromBase()) {
                p.setMinDistanceFromBase(currentDistance);
                p.setId(basePerson.getId());
            }
        }
    }

    private double calculateDistance(Person basePerson, Person otherPerson) {

        Position basePosition = basePerson.getFace().getPosition();
        Position otherPosition = otherPerson.getFace().getPosition();

        return Math.sqrt(Math.pow( basePosition.getX() - otherPosition.getX(),2)
                + Math.pow( basePosition.getY() - otherPosition.getY(),2));
    }

}
