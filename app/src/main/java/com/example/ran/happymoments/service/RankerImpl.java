package com.example.ran.happymoments.service;


import android.util.Log;

import com.example.ran.happymoments.common.AppConstants;
import com.example.ran.happymoments.model.face.Face;
import com.example.ran.happymoments.model.photo.Person;
import com.example.ran.happymoments.model.photo.Photo;

import java.util.List;

public class RankerImpl implements Ranker {

    public RankerImpl(){ }

    public double rankPhoto(Photo photo){
        int numOfPersons = photo.getPersons().size();
        double photoRank = 0;
        Log.i("Ranker" , "path=" + photo.getPath());
        rankPersons(photo.getPersons());

        for(int i = 0 ; i< numOfPersons ; i++)
            photoRank += photo.getPersons().get(i).getRank();

        photoRank /= numOfPersons;
        photo.setRank(photoRank);
        return photoRank;
    }


    private void rankPersons(List<Person> persons) {
        for (Person person : persons) {
            rankPerson(person);
            Log.i("Ranker" , "person position = "+person.getFace().getPosition() + ", person rank" + person.getRank());
        }
    }

    private void rankPerson(Person person) {
        double faceRank = calcFaceRank(person.getFace());
        double importance = person.getImportance();

        person.setRank(faceRank * importance);
    }



    //average between eyesOpen and smile -> multiply the result with the importance percentage
    private double calcFaceRank(Face face) {
        float eyesScore, smileScore;

        eyesScore = face.getEyes().getEyesOpenProbability() < 0 ? 0 :
                face.getEyes().getEyesOpenProbability()* 100;
        smileScore = face.getSmile().getSmilingProbability() < 0 ? 0 :
                face.getSmile().getSmilingProbability()* 100;
        Log.i("Ranker", "eyeScore = " +  eyesScore + " smileScore = " + smileScore);
        double eyesAndSmileScore = ((eyesScore * AppConstants.EYES_WEIGHT) + (smileScore * AppConstants.SMILE_WEIGHT));
        return eyesAndSmileScore;
    }

}
