package com.example.ran.happymoments.service;

import android.content.Context;
import android.support.media.ExifInterface;
import android.util.Log;

import com.example.ran.happymoments.common.AppConstants;
import com.example.ran.happymoments.model.face.Face;
import com.example.ran.happymoments.model.photo.Person;
import com.example.ran.happymoments.model.photo.Photo;
import com.example.ran.happymoments.model.series.PhotoSeries;
import com.example.ran.happymoments.service.detector.FaceDetector;
import com.example.ran.happymoments.service.detector.MobileVision;

import org.opencv.core.Mat;
import org.opencv.core.MatOfFloat;
import org.opencv.core.MatOfInt;
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

    private FaceMatcher mMatcher;
    private Ranker mRanker;


    public SeriesGeneratorImpl(Context context) {
        mContext = context;
        mFaceDetector = new MobileVision(context);
        mPhotosOutputPath = new ArrayList<>();
        mMatcher = new FaceMatcherImpl();
        mRanker = new RankerImpl();
    }


    @Override
    public List<String> detect(List<String> inputPhotosPath) {

        if(inputPhotosPath.size() == 0)
            return inputPhotosPath;

        List<PhotoSeries> seriesList = generateAllSeries(inputPhotosPath);

        for (PhotoSeries series : seriesList) {


            //match all persons in the series of photo by their id's
            mMatcher.matchPersons(series);

            Log.i("CHECKING...", "Series: " + series.getId() + " has " + series.getNumOfPhotos() + " photos");
            for(Photo p: series.getPhotos()){
                Log.i("CHECKING...", "--> Path: " + p.getPath() + " Faces found: " + p.getNumOfPersons() );
                for(Person per : p.getPersons()) {
                    Log.i("CHECKING...", "Person #" + per.getId() + ": (" + per.getFace().getPosition().getX() + "," +
                            per.getFace().getPosition().getY() + ") , width: "+ per.getFace().getWidth() +
                            "height: " + per.getFace().getHeight());
                }
            }

            //in each photo in each series set every person face importance to value between 0-1
            setImportanceFaces(series);

            //finding the highest ranked photo in series
            int highestRankedPhotoIndex = 0;
            double highestRank = mRanker.rankPhoto(series.getPhoto(highestRankedPhotoIndex));
            double currentRank;
            Log.i("SCORE" , "photo= " +series.getPhoto(0).getPath()+  ", rank= " + highestRank);

            for (int i = 1 ; i < series.getPhotos().size() ; i++) {
                currentRank = mRanker.rankPhoto(series.getPhoto(i));
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

        Map<Integer , Double> personMaxFaceSizeMap = new HashMap<>();
        double currentSize , maxSize;
        int key;

        for (Photo photo : series.getPhotos()) {

            for (Person person : photo.getPersons()) {
                key = person.getId();
                currentSize = person.getFaceSize();

                if (personMaxFaceSizeMap.containsKey(key)) {
                    maxSize = personMaxFaceSizeMap.get(key);

                    if (person.getFaceSize() > maxSize) {
                        personMaxFaceSizeMap.put(key , currentSize);
                    }
                } else {
                    personMaxFaceSizeMap.put(key , currentSize);
                }
            }
        }

        //now we have each person max face size in a series
        //need to calculate person importance in each photo

        for (Photo photo : series.getPhotos()) {
            for (Person person : photo.getPersons()) {
                double importance = person.getFaceSize() / personMaxFaceSizeMap.get(person.getId()) ;
                person.setImportance(importance);
                Log.i("FACEIMPORTANCE" , photo.getPath() + ", person #"+ person.getId()+": " + person.getFace().getPosition().toString()+", importance= " + importance);
            }
        }
    }


    private List<PhotoSeries> generateAllSeries(List<String> inputPhotosPath) {
        List<PhotoSeries> rv;
        List<Photo> photos = new ArrayList<>();

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

//            Log.i("FACES", "Path: " + path);

            faces = mFaceDetector.detectFaces(mContext, path ,orientation);

//            Log.i("FACES", "Image Path: " + path + " has " + faces.size()+" faces");
//            for(Face f : faces){
//                Log.i("FACES", "Smile = " + f.getSmile().getSmilingProbability()+
//                " Eyes = " + f.getEyes().getEyesOpenProbability());
//            }

            if (!faces.isEmpty()) { //TODO null check
                Person[] persons = new Person[faces.size()];

                for (int i = 0 ; i < faces.size() ; i++) {
                    persons[i] = new Person(i , faces.get(i));
                }

                Mat hist = calcHistogram(path);
                photos.add(new Photo(path ,exifInterface, Arrays.asList(persons),hist));
            }
            else {
                Log.i("SERIES", path + " has no faces!");
            }
        }

        rv = generateSeriesByFeatures(photos);

        filterAllOnePhotoSeries(rv);

        return rv;
    }


    private Mat calcHistogram(String imagePath) {
        Mat img = Imgcodecs.imread(imagePath, Imgcodecs.CV_LOAD_IMAGE_GRAYSCALE);
        Mat histogram = new Mat();
        MatOfFloat ranges = new MatOfFloat(0f, 256f);
        MatOfInt histSize = new MatOfInt(256);
        Imgproc.calcHist(Arrays.asList(img), new MatOfInt(0), new Mat(), histogram, histSize, ranges);
        return histogram;
    }





    private void printSeries(List<PhotoSeries> seriesList) {
        for (PhotoSeries series : seriesList) {
            Log.i("TESTING" , "Series ID= " + series.getId());

            for (Photo photo : series.getPhotos()) {
                Log.i("TESTING" , "path= " + photo.getPath());
            }
        }
    }






    private void filterAllOnePhotoSeries(List<PhotoSeries> seriesList) {

        ArrayList <PhotoSeries> photosToBeRemoved = new ArrayList<>();

        for (int i = 0; i < seriesList.size() ; i++) {

            if (seriesList.get(i).getNumOfPhotos() == 1) {
                mPhotosOutputPath.add(seriesList.get(i).getPhoto(0).getPath());
                photosToBeRemoved.add(seriesList.get(i));
                Log.i("CHECKING...", "### Series #"+ seriesList.get(i).getId() + " contains only 1 photo: " +
                        seriesList.get(i).getPhoto(0).getPath() + " Faces found: "+ seriesList.get(i).getPhoto(0).getNumOfPersons());
            }
        }
        seriesList.removeAll(photosToBeRemoved);
    }




    public List<PhotoSeries> generateSeriesByFeatures(List<Photo> photos) {

        List<PhotoSeries> foundSeries =  new ArrayList<>();
        boolean hasFoundSeries;
        PhotoSeries photoSeries = new PhotoSeries();

        if (photos.isEmpty()) {
            return foundSeries;
        }

        photoSeries.addPhoto(photos.get(0));
        foundSeries.add(photoSeries);

        for (int i = 1 ; i < photos.size() ; i++){

            hasFoundSeries = false;

            for (int j = 0 ; j < foundSeries.size() ; j++) {

                if (arePhotosClosedByFeaturesDistance(foundSeries.get(j).getPhoto(0), photos.get(i))) {
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

        for(PhotoSeries ps: foundSeries) {
            Log.i("SERIES", "==> Series #" + ps.getId() + " has " + ps.getNumOfPhotos() + " photos");
            for (Photo p : ps.getPhotos()) {
                Log.i("SERIES", p.getPath());
            }
        }
        return foundSeries;
    }



    public boolean arePhotosClosedByFeaturesDistance(Photo p1 , Photo p2){

        double diffBySeconds = p1.getFeatures().calcTimeDiffBySeconds(p2.getFeatures());
        double diffByHistogram = Imgproc.compareHist(p1.getFeatures().getHistogram(), p2.getFeatures().getHistogram(), Imgproc.CV_COMP_BHATTACHARYYA);
        double diffByMeters = p1.getFeatures().calcDistanceDiffByMeters(p2.getFeatures());


        double total = Math.sqrt((diffBySeconds * diffBySeconds)
                + (diffByHistogram * diffByHistogram)
                + (diffByMeters * diffByMeters));

        Log.i("DIFF", "==> DifBySec = " + diffBySeconds + " DifByMeters = " + diffByMeters + " DifByHist = " + diffByHistogram );
        Log.i("DIFF", "Total = " + total);
        return (total <= AppConstants.SIMILARITY_THRESHOLD);
    }



}
