package com.example.ran.happymoments.model.photo;


import com.example.ran.happymoments.model.face.Face;

import org.opencv.core.Mat;

public class Person {

//    private static int idGenerator = 0;
    private int id;
    private Face face;
    private double rank;
    private double importance;
    private Mat faceHist;


    public Person(int id, Face face, Mat faceHist){
        this.face = face;
        this.id = id;
        this.faceHist = faceHist;
    }


    public Mat getFaceHist() {
        return faceHist;
    }

    public void setFaceHist(Mat faceHist) {
        this.faceHist = faceHist;
    }

    public Face getFace() {
        return face;
    }

    public void setFace(Face face) {
        this.face = face;
    }

    public double getFaceSize() {
        return this.face.getSize();
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }


    public void setRank(double rank) {
        this.rank = rank;
    }

    public double getRank() {
        return rank;
    }

    public void setImportance(double importance) {
        this.importance = importance;
    }

    public double getImportance() {
        return this.importance;
    }
}
