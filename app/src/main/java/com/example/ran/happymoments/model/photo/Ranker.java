package com.example.ran.happymoments.model.photo;


import android.util.Log;

import com.example.ran.happymoments.common.AppConstants;
import com.example.ran.happymoments.model.face.Face;

import java.util.List;

public class Ranker {

    public Ranker(){ }

    public static double rankPhoto(Photo photo){
        int numOfPersons = photo.getPersons().size();
        double photoRank = 0;
        Log.i("PersonXXX" , "path=" + photo.getPath());
        rankPersons(photo.getPersons());

        for(int i = 0 ; i< numOfPersons ; i++)
            photoRank += photo.getPersons().get(i).getRank();

        photoRank /= numOfPersons;
        photo.setRank(photoRank);
        return photoRank;
    }


    private static void rankPersons(List<Person> persons) {
        for (Person person : persons) {
            rankPerson(person);
            Log.i("PersonXXX" , "person position = "+person.getFace().getPosition() + ", person rank" + person.getRank());
        }
    }

    private static void rankPerson(Person person) {
        double faceRank = calcFaceRank(person.getFace());
        double importance = person.getImportance();

        person.setRank(faceRank * importance);
    }



    //average between eyesOpen and smile -> multiply the result with the importance percentage
    public static double calcFaceRank(Face face) {
        float eyesScore, smileScore;

        eyesScore = face.getEyes().getEyesOpenProbability() * 100;
        smileScore = face.getSmile().getSmilingProbability() * 100;
        double eyesAndSmileScore = ((eyesScore * AppConstants.EYES_WEIGHT) + (smileScore * AppConstants.SMILE_WEIGHT));
        return eyesAndSmileScore;
    }

}
