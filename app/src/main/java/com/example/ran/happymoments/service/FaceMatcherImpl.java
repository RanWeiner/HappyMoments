package com.example.ran.happymoments.service;


import com.example.ran.happymoments.common.Position;
import com.example.ran.happymoments.common.Utils;
import com.example.ran.happymoments.model.photo.Person;
import com.example.ran.happymoments.model.photo.Photo;
import com.example.ran.happymoments.model.series.PhotoSeries;
import java.util.HashSet;
import java.util.List;
import java.util.Set;


public class FaceMatcherImpl implements FaceMatcher {


    public FaceMatcherImpl() {}


    public void matchPersons(PhotoSeries series) {
        Photo basePhoto = series.getMaxNumOfPersonsPhoto();

        for (Photo p : series.getPhotos()){

            if (p != basePhoto) {
                if (p.getNumOfPersons() == basePhoto.getNumOfPersons()) {
                    matchPersonsWhereNumOfPersonsIsEqual(basePhoto , p);
                } else {
                    matchPersonsWhereNumOfPersonsIsLesser(basePhoto , p);
                }
            }
        }
    }



    private void matchPersonsWhereNumOfPersonsIsEqual(Photo basePhoto, Photo photo) {
        for (int i = 0 ; i < basePhoto.getNumOfPersons() ; i++) {
            photo.getPersons().get(i).setId(basePhoto.getPersons().get(i).getId());
        }
    }



    private void matchPersonsWhereNumOfPersonsIsLesser(Photo basePhoto, Photo p) {
        Set<Integer> selectedIds = new HashSet<>();

        for (Person person : p.getPersons()) {
            int id = matchToMinDistance(person , basePhoto.getPersons(), selectedIds);
            selectedIds.add(id);
        }
    }

    private int matchToMinDistance(Person person, List<Person> basePersons, Set<Integer> selectedIds) {
        double currentDistance;
        double minDistance = Double.MAX_VALUE;
        int matchId = -1;

        for (Person p: basePersons) {
            if (!selectedIds.contains(p.getId())) {
                currentDistance = Utils.calculateDistance(person.getFace().getPosition() , p.getFace().getPosition());
                if (currentDistance < minDistance) {
                    minDistance = currentDistance;
                    matchId = p.getId();
                }
            }
        }
        person.setId(matchId);
        return matchId;
    }



}
