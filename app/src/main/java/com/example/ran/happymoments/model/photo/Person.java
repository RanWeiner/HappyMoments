package com.example.ran.happymoments.model.photo;


import com.example.ran.happymoments.model.face.Face;

public class Person {

//    private static int idGenerator = 0;
    private int id;
    Face face;
    double rank;
    double importance;


    public Person(int id , Face face){
//        this.id = ++idGenerator;
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
