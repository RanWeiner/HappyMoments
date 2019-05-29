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

    public double rankPhoto(Photo photo){
        int numOfPersons = photo.getPersons().size();
        double photoRank = 0;
        Log.i("Ranker" , "path=" + photo.getPath());
        rankPersons(photo.getPersons());

        for(int i = 0 ; i< numOfPersons ; i++)
            photoRank += photo.getPersons().get(i).getRank();



//        Log.i("FACES", "Image Path: " + photo.getPath() + " has " + photo.getNumOfPersons()+" faces");
        for(Person f : photo.getPersons()){
//            float eyesScore = f.getFace().getEyes().getEyesOpenProbability() < 0 ? 0 :
//                    f.getFace().getEyes().getEyesOpenProbability()* 100;
//            float smileScore = f.getFace().getSmile().getSmilingProbability() < 0 ? 0 :
//                    f.getFace().getSmile().getSmilingProbability()* 100;
//            Log.i("FACES", "Smile = " + smileScore +
//                    " Eyes = " + eyesScore);
//            Log.i("FACES", "Person #" + f.getId() + " Smile = " + f.getFace().getSmile().getSmilingProbability() +
//                    " Eyes = " + f.getFace().getEyes().getEyesOpenProbability() + " Importance = " + f.getImportance() + " Total: " + f.getRank());


//            Log.i("FACES", "Person #" + f.getId()+"("+f.getFace().getPosition().getX()+" , "+f.getFace().getPosition().getY()+") " + " isSmiling = " + f.getFace().getSmile().isSmiling()
//            + " areEyesOpen = " + f.getFace().getEyes().areEyesOpen());


        }

        photoRank /= numOfPersons;
        photo.setRank(photoRank);
        Log.i("Rank","Photo path: " + photo.getPath() + " Rank: " + photoRank);
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
