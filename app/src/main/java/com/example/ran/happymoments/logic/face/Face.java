package com.example.ran.happymoments.logic.face;

import com.example.ran.happymoments.common.Position;

public class Face {

    private Smile smile;
    private Eyes eyes;
    private Position position;
    private float width, height;


    public Face(){

    }


    public Face(Position position, float width, float height , Smile smile , Eyes eyes) {

        this.position = position;
        this.width = width;
        this.height = height;
        this.smile = smile;
        this.eyes = eyes;
    }



    public Smile getSmile() {
        return smile;
    }

    public Eyes getEyes() {
        return eyes;
    }
//    public double getDistanceFromGravityCenter() {
//        return distanceFromGravityCenter;
//    }
//
//    public void setDistanceFromTotalFacesCenter(double distanceFromGravityCenter) {
//        this.distanceFromGravityCenter = distanceFromGravityCenter;
//    }
//
//    public double getAngleFromGravityCenter() {
//        return angleFromGravityCenter;
//    }
//
//    public void setAngleFromTotalFacesCenter(double angleFromGravityCenter) {
//        this.angleFromGravityCenter = angleFromGravityCenter;
//    }

    public Position getPosition() {
        return this.position;
    }

    public void setPosition(Position position) {
        this.position = position;
    }

    public float getWidth() {
        return this.width;
    }

    public void setWidth(float width) {
        this.width = width;
    }

    public float getHeight() {
        return this.height;
    }

    public void setHeight(float height) {
        this.height = height;
    }

    public float getSize() {
        return this.width * this.height;
    }

}
