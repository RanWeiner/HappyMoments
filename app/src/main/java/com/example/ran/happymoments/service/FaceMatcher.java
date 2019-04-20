package com.example.ran.happymoments.service;


import com.example.ran.happymoments.common.Position;
import com.example.ran.happymoments.model.photo.Person;
import com.example.ran.happymoments.model.series.PhotoSeries;

import java.util.ArrayList;
import java.util.List;

public class FaceMatcher {


    public FaceMatcher() {

    }

    public static void matchPersons(PhotoSeries series) {
        List<Person> basePersons , anyPersons;
        basePersons = series.getPhoto(0).getPersons();
        int numOfPhotos = series.getNumOfPhotos();

        for (int photoIdx = 1 ; photoIdx < numOfPhotos ; photoIdx++) {
            anyPersons = series.getPhoto(photoIdx).getPersons();
            findMatch(basePersons , anyPersons);
        }
    }

    private static void findMatch(List<Person> basePersons, List<Person> anyPersons) {
        List<Person> anyPersonsCopy = new ArrayList<Person>(anyPersons);
        int numOfPersons = basePersons.size();

        for (int i = 0 ; i < numOfPersons ; i++) {
            int index = findMinDistance(basePersons.get(i) , anyPersonsCopy);
            anyPersons.get(index).setId(basePersons.get(i).getId());
            anyPersonsCopy.remove(index);
        }
    }

    private static int findMinDistance(Person person, List<Person> anyPersons) {
        double currentDistance , minDistance = Double.MAX_VALUE;
        Position anyPosition,basePosition = person.getFace().getPosition();
        int rv = 0;
        int numPersons = anyPersons.size();

        for (int i = 0 ; i < numPersons ; i++) {
            anyPosition = anyPersons.get(i).getFace().getPosition();
            currentDistance = Math.sqrt(Math.pow( basePosition.getX() - anyPosition.getX(),2)
                                    + Math.pow( basePosition.getY() - anyPosition.getY(),2));
            if (currentDistance < minDistance) {
                minDistance = currentDistance;
                rv = i;
            }
        }
        return rv;
    }



}
