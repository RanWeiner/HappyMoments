package com.example.ran.happymoments.model.face;

import com.example.ran.happymoments.common.Position;

import org.opencv.core.Mat;
import org.opencv.core.MatOfFloat;
import org.opencv.core.MatOfInt;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;

import java.util.Arrays;

public class Face {

    private Smile smile;
    private Eyes eyes;
    private Position position;
    private float width, height;

    public Face(){ }

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
