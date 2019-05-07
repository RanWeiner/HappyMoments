package com.example.ran.happymoments.service;

import android.content.Context;
import android.support.media.ExifInterface;
import android.util.Log;

import com.example.ran.happymoments.model.face.Face;
import com.example.ran.happymoments.model.photo.Person;
import com.example.ran.happymoments.model.photo.Photo;
import com.example.ran.happymoments.model.series.PhotoSeries;
import com.example.ran.happymoments.service.detector.FaceDetector;
import com.example.ran.happymoments.service.detector.MobileVision;

import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfFloat;
import org.opencv.core.MatOfInt;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class SeriesGeneratorImpl implements SeriesGenerator {

    private Context mContext;
    private FaceDetector mFaceDetector;
    private List<String> mPhotosOutputPath;


    public SeriesGeneratorImpl(Context context) {
        mContext = context;
        mFaceDetector = new MobileVision(context);
        mPhotosOutputPath = new ArrayList<>();
    }


    @Override
    public List<String> detect(List<String> inputPhotosPath) {

        if(inputPhotosPath.size() == 0)
            return inputPhotosPath;

        List<PhotoSeries> seriesList = generateAllSeries(inputPhotosPath);

//        printSeries(seriesList);


        for (PhotoSeries series : seriesList) {

            Log.i("CHECKING...", "Series: " + series.getId() + " has " + series.getNumOfPhotos() + " photos");
            for(Photo p: series.getPhotos()){
                Log.i("CHECKING...", "--> Path: " + p.getPath() + " Faces found: " + p.getNumOfPersons() );
                for(Person per : p.getPersons()) {
                    Log.i("CHECKING...", "Person #" + per.getId() + ": (" + per.getFace().getPosition().getX() + "," +
                    per.getFace().getPosition().getY() + ") , width: "+ per.getFace().getWidth() +
                    "height: " + per.getFace().getHeight());
                }
            }

            //match all persons in the series of photo by their id's
            FaceMatcher.matchPersons(series);

            //in each photo in each series set every person face importance to value between 0-1
            setImportanceFaces(series);

            //finding the highest ranked photo in series
            int highestRankedPhotoIndex = 0;
            double highestRank = Ranker.rankPhoto(series.getPhoto(highestRankedPhotoIndex));
            double currentRank;
            Log.i("SCORE" , "photo= " +series.getPhoto(0).getPath()+  ", rank= " + highestRank);

            for (int i = 1 ; i < series.getPhotos().size() ; i++) {
                currentRank = Ranker.rankPhoto(series.getPhoto(i));
                Log.i("SCORE" , "photo= " +series.getPhoto(i).getPath()+  ", rank= " + currentRank);
                if (currentRank > highestRank) {
                    highestRank = currentRank;
                    highestRankedPhotoIndex = i;
                }
            }
            mPhotosOutputPath.add(series.getPhoto(highestRankedPhotoIndex).getPath());
        }

        return mPhotosOutputPath;
    }

    @Override
    public void releaseResources() {
        mFaceDetector.release();
    }


    //set importance of face in each photo between 0-1
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
                Log.i("FACEIMPORTANCE" , photo.getPath() + ", person = " + person.getFace().getPosition().toString()+", importance= " + importance);
            }
        }
    }


    private List<PhotoSeries> generateAllSeries(List<String> inputPhotosPath) {
        List<PhotoSeries> rv = new ArrayList<>();
        Map <Integer , List<Photo>> map = new HashMap<>();

        List<Face> faces;
        ExifInterface exifInterface = null;
        String orientation = null;

        for (String path : inputPhotosPath) {
            try {
                exifInterface = new ExifInterface(path);
                orientation = exifInterface.getAttribute(ExifInterface.TAG_ORIENTATION);
            } catch (IOException e) {
                e.printStackTrace();
            }

            faces = mFaceDetector.detectFaces(mContext, path ,orientation);

            if (!faces.isEmpty()) {
                Person[] persons = new Person[faces.size()];
                Mat[] facesHist = calcFacesHistogram(path , faces);

                for (int i = 0 ; i < faces.size() ; i++) {
                    persons[i] = new Person(i , faces.get(i) , facesHist[i]);
                }

                Photo photo = new Photo(path ,exifInterface, Arrays.asList(persons));
                addPhotoToMap(map , photo , persons.length);
            }
        }

        for (Map.Entry<Integer, List<Photo>> entry : map.entrySet()) {
            if (entry.getValue().size() == 1) {
//                PhotoSeries photo = new PhotoSeries();
//                photo.addPhoto(entry.getValue().get(0));
//                mPhotosOutputPath.add(photo.getPhoto(0).getPath());
                mPhotosOutputPath.add(entry.getValue().get(0).getPath());
            } else {

                rv.addAll(generateSeriesByFeatures(entry.getValue()));
//                rv.addAll(generateSeriesByFeatures(entry.getValue()));
            }
        }

        filterAllOnePhotoSeries(rv);
        return rv;
    }

    private Mat[] calcFacesHistogram(String path, List<Face> faces) {

        Mat src = Imgcodecs.imread(path, Imgcodecs.CV_LOAD_IMAGE_GRAYSCALE);
        Mat[] rv = new Mat[faces.size()];

        for (int i = 0 ; i < faces.size() ; i++) {
            rv[i] = new Mat();
            Point topLeft = new Point(faces.get(i).getPosition().getX(),faces.get(i).getPosition().getY());
            Point bottomRight = new Point(faces.get(i).getPosition().getX() + faces.get(i).getWidth(),faces.get(i).getPosition().getY() + faces.get(i).getHeight());

            Mat mask = new Mat(src.rows(), src.cols(), CvType.CV_8U, Scalar.all(0));
            Imgproc.rectangle(mask,topLeft,bottomRight,new Scalar(255) , -1);

            Mat cropped = new Mat();
            src.copyTo( cropped, mask );
            MatOfFloat ranges = new MatOfFloat(0f, 256f);
            MatOfInt histSize = new MatOfInt(256);
            Imgproc.calcHist(Arrays.asList(src), new MatOfInt(0), cropped, rv[i], histSize, ranges);

            Mat m = new Mat();
            cropped.convertTo(m , CvType.CV_32F);
            rv[i] = m ;
        }
        return rv;
    }


    private void printSeries(List<PhotoSeries> seriesList) {
        for (PhotoSeries series : seriesList) {
            Log.i("TESTING" , "Series ID= " + series.getId());

            for (Photo photo : series.getPhotos()) {
                Log.i("TESTING" , "path= " + photo.getPath());
            }
        }
    }



    private void addPhotoToMap(Map<Integer,List<Photo>> map, Photo photo, int key) {
        List<Photo> photos = map.get(key);

        //not exist in map
        if (photos == null) {
            photos = new ArrayList<>();
            photos.add(photo);
            map.put(key, photos);
        } else {
            //exist in map
            photos.add(photo);
        }
    }


    private void filterAllOnePhotoSeries(List<PhotoSeries> seriesList) {

        ArrayList <PhotoSeries> photosToBeRemoved = new ArrayList<>();

        for (int i = 0; i < seriesList.size() ; i++) {

            if (seriesList.get(i).getNumOfPhotos() == 1) {
                mPhotosOutputPath.add(seriesList.get(i).getPhoto(0).getPath());
                photosToBeRemoved.add(seriesList.get(i));
                Log.i("CHECKING...", "### Series #"+ seriesList.get(i).getId() + " contains only 1 photo: " +
                        seriesList.get(i).getPhoto(0).getPath());
            }
        }
        seriesList.removeAll(photosToBeRemoved);
    }




    public List<PhotoSeries> generateSeriesByFeatures(List<Photo> photos) {

        List<PhotoSeries> foundSeries =  new ArrayList<>();
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
