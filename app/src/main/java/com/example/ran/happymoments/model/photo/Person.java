package com.example.ran.happymoments.model.photo;


import com.example.ran.happymoments.model.face.Face;

import org.opencv.core.Mat;

public class Person {
    private int id;
    private Face face;
    private double rank;
    private double importance;

    public Person(int id, Face face){
        this.face = face;
        this.id = id;
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
