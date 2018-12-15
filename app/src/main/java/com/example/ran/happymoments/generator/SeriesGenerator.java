package com.example.ran.happymoments.generator;

import android.content.Context;
import android.util.Log;

import com.example.ran.happymoments.common.RelativePositionVector;
import com.example.ran.happymoments.generator.face.Face;
import com.example.ran.happymoments.common.Position;
import com.example.ran.happymoments.generator.photo.Person;
import com.example.ran.happymoments.generator.photo.Photo;
import com.example.ran.happymoments.generator.series.PhotoSeries;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;



public class SeriesGenerator {

    private List<String> mInput;
    private List<String> mOutput;

    private List<Photo> mPhotosOutput;

    private List<Photo> mAllPhotos;

    private Context mContext;
    private FaceExtractor mFaceExtractor;

    private Ranker mRanker;
    private FaceMatcher mMatcher;

    private List<PhotoSeries> mAllSeries;


    public SeriesGenerator(Context context , List<String> imagesPath) {
        mContext = context;
        mInput = imagesPath;
        mAllPhotos = setPhotos(imagesPath);
        mFaceExtractor = new FaceExtractorMobileVision();
        mOutput = new ArrayList<>();
        mRanker = new Ranker();
        mMatcher = new FaceMatcher();
        mPhotosOutput = new ArrayList<>();
        mAllSeries = new ArrayList<>();
    }


    public List<String> detect() {

        ///////////////////// [SERIES PART] /////////////////////

        generateAllSeries();

        filterAllOnePhotoSeries(mAllSeries);

        printSeries(mAllSeries);

        ///////////////////// [NORMALIZE] /////////////////////

        normalizeVectors(mAllSeries);


        for (PhotoSeries series : mAllSeries) {
            for (Photo photo : series.getPhotos()) {
               for (Person person : photo.getPersons()) {
                   Log.i("TESTING" , "normalized angle=" + person.getVector().getAngle());
                   Log.i("TESTING" , "normalized distance=" + person.getVector().getDistance());
               }
            }
        }

        ///////////////////// [FACE CORRESPONDENCE & RANKING] /////////////////////
        Log.i("TESTING" , "MATCHING");

        for (PhotoSeries series : mAllSeries) {

            //match all persons in the series of photo by their id's
            mMatcher.matchPersons(series);

            //in each photo in each series set every person face importance to value between 0-1
            setImportanceFaces(series);

            //finding the highest ranked photo in series
            double highestRank = mRanker.rankPhoto(series.getPhoto(0));
            double currentRank;
            int highestRankedPhotoIndex = 0;

            for (int i = 1 ; i < series.getPhotos().size() ; i++) {
                currentRank = mRanker.rankPhoto(series.getPhoto(i));
                if (currentRank > highestRank) {
                    highestRank = currentRank;
                    highestRankedPhotoIndex = i;
                }
            }

            Photo p = series.getPhoto(highestRankedPhotoIndex);
            mOutput.add(p.getPath());
            mPhotosOutput.add(p);
        }

        return mOutput;
    }





    private void setImportanceFaces(PhotoSeries series) {

        Map<Integer , Double> personMaxFaceSize = new HashMap<>();
        double currentSize , maxSize;
        int key;

        for (Photo photo : series.getPhotos()) {

            for (Person person : photo.getPersons()) {
                key = person.getId();
                currentSize = person.getFaceSize();

                if (personMaxFaceSize.containsKey(key)) {
                    maxSize = personMaxFaceSize.get(key);

                    if (person.getFaceSize() > maxSize) {
                        personMaxFaceSize.put(key , currentSize);
                    }
                } else {
                    personMaxFaceSize.put(key , currentSize);
                }
            }
        }

        //now we have each person max face size in a series
        //need to calculate person importance in each photo

        for (Photo photo : series.getPhotos()) {
            for (Person person : photo.getPersons()) {
                double importance = person.getFaceSize() / personMaxFaceSize.get(person.getId()) ;
                person.setImportance(importance);
            }
        }
    }


    private void generateAllSeries() {
        Map <Integer , List<Photo>> numOfFacesMap = new HashMap<>();
        List<Face> faces;

        for (Photo photo : mAllPhotos){
            faces = mFaceExtractor.detectFaces(mContext, photo.getPath());

            if (!faces.isEmpty()) {
                saveFacesData(photo , faces);
                addPhotoToMap(numOfFacesMap , photo , faces.size());
            }
        }


        for (Map.Entry<Integer, List<Photo>> entry : numOfFacesMap.entrySet()) {
            if (entry.getValue().size() == 1) {
                mPhotosOutput.addAll(entry.getValue());
            } else {
                mAllSeries.addAll(generateSeriesByFeatures(entry.getValue()));
            }
        }
    }



    private void saveFacesData(Photo photo, List<Face> faces) {

        if(faces.size() > 1) {
            setTotalFacesCenterInPhoto(photo, faces);

            for (Face face : faces) {
                double angle = face.getPosition().calcAngle(photo.getTotalFacesCenter());
                double dist = face.getPosition().calcEuclidDistance(photo.getTotalFacesCenter());

                photo.addPerson(new Person(face, new RelativePositionVector(angle, dist)));
            }
        } else {
            photo.addPerson(new Person(faces.get(0), new RelativePositionVector(0,0)));
        }
    }


    private void printSeries(List<PhotoSeries> seriesList) {
        for (PhotoSeries series : seriesList) {
            Log.i("TESTING" , "Series ID= " + series.getId());

            for (Photo photo : series.getPhotos()) {
                Log.i("TESTING" , "path= " + photo.getPath());
            }
        }
    }


    private void normalizeVectors(List<PhotoSeries> allSeries) {

        double maxDistance;

        for (PhotoSeries series: allSeries) {

            maxDistance = series.calcMaxDistanceToFacesCenter();

            Log.i("TESTING" , "maxDistance=" + maxDistance);

            for (Photo photo: series.getPhotos()) {

                for (Person person: photo.getPersons()) {

                    person.normalizeVector(maxDistance);
                }
            }
        }
    }




    private void setTotalFacesCenterInPhoto(Photo photo, List<Face> faces) {
        Position centerGravity;
        centerGravity = calcFacesCenterGravity(faces);
        photo.setTotalFacesCenter(centerGravity);
    }


    private void addPhotoToMap(Map<Integer,List<Photo>> map, Photo photo, int key) {
        List<Photo> photos = map.get(key);

        //not exist in map
        if (photos == null) {
            photos = new ArrayList<Photo>();
            photos.add(photo);
            map.put(key, photos);
        } else {
            //exist in map
            photos.add(photo);
        }
    }



    private Position calcFacesCenterGravity(List<Face> faces) {
        double sumX = 0 , sumY = 0;
        int size = faces.size();

        for (Face face : faces) {
            sumX += face.getPosition().getX();
            sumY += face.getPosition().getY();
        }
        return new Position(sumX/size , sumY/size);
    }


    //returning series containing only one photo
    private void filterAllOnePhotoSeries(List<PhotoSeries> allSeries) {

        ArrayList <PhotoSeries> photosToBeRemoved = new ArrayList<>();

        for (int i = 0; i < allSeries.size() ; i++) {

            if (allSeries.get(i).getPhotos().size() == 1) {
                mPhotosOutput.add(allSeries.get(i).getPhoto(0));
//                mOutput.add(allSeries.get(i).getPhoto(0).getPath());
                photosToBeRemoved.add(allSeries.get(i));
            }
        }
        allSeries.removeAll(photosToBeRemoved);
    }



    private List<Photo> setPhotos(List<String> mImagesPath) {

        List<Photo> photos = new ArrayList<>();

        for (String path : mImagesPath) {
            photos.add( new Photo(path));
        }
        return photos;
    }



    //this function create series list from the initial photos
    public List<PhotoSeries> generateSeriesByFeatures(List<Photo> photos) {

        List<PhotoSeries> foundSeries =  new ArrayList<>();;
        boolean hasFoundSeries;
        PhotoSeries photoSeries = new PhotoSeries();

        if (photos.isEmpty()) {
            return null;
        }

        photoSeries.addPhoto(photos.get(0));
        foundSeries.add(photoSeries);

        for (int i = 1 ; i < photos.size() ; i++){

            hasFoundSeries = false;

            for (int j = 0 ; j < foundSeries.size() ; j++) {

                if (foundSeries.get(j).getPhoto(0).similarTo( photos.get(i))){
                    foundSeries.get(j).addPhoto( photos.get(i));
                    hasFoundSeries = true;
                    break;
                }
            }
            if (!hasFoundSeries){
                PhotoSeries newPhotoSeries = new PhotoSeries();
                newPhotoSeries.addPhoto( photos.get(i));
                foundSeries.add(newPhotoSeries);
            }
        }
        return foundSeries;
    }
}
