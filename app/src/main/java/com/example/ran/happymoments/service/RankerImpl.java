package com.example.ran.happymoments.service;


import android.util.Log;

import com.example.ran.happymoments.common.AppConstants;
import com.example.ran.happymoments.model.face.Face;
import com.example.ran.happymoments.model.photo.Person;
import com.example.ran.happymoments.model.photo.Photo;
import com.google.android.gms.vision.face.Landmark;

import java.util.List;

public class RankerImpl implements Ranker {

    public RankerImpl(){ }

    private double photoRank;


    public double rankPhoto(Photo photo){
        photoRank = 0;
        Log.i("Rank","Photo path: " + photo.getPath());
        int numOfPersons = photo.getPersons().size();

        photo.getPersons().stream().map(this::rankPerson).forEach( p-> photoRank += p);
        photoRank /= numOfPersons;
        photo.setRank(photoRank);
        Log.i("Rank", "Photo rank: " + photoRank);
        return photoRank;
    }

    //average between eyesOpen and smile -> multiply the result with the importance percentage
    private double rankPerson(Person person) {
        double faceRank = calcFaceRank(person.getFace());
        double importance = person.getImportance();
        double rank = faceRank*importance;
        person.setRank(rank);
        return rank;
    }


    private double calcFaceRank(Face face) {
        float eyesScore, smileScore;

        eyesScore = face.getEyes().getEyesOpenProbability() < 0 ? 0 :
                face.getEyes().getEyesOpenProbability()* 100;
        smileScore = face.getSmile().getSmilingProbability() < 0 ? 0 :
                face.getSmile().getSmilingProbability()* 100;
        Log.i("Ranker", "eyeScore = " +  eyesScore + " smileScore = " + smileScore);
        return ((eyesScore * AppConstants.EYES_WEIGHT) + (smileScore * AppConstants.SMILE_WEIGHT));
    }

}
