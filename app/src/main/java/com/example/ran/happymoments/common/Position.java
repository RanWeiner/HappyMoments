package com.example.ran.happymoments.common;

public class Position {
    double x , y;

    public Position() {
        this.x = 0;
        this.y = 0;
    }

    public Position(double x , double y) {
        this.x = x;
        this.y = y;
    }

    public void setX(double x) {
        this.x = x;
    }

    public void setY(double y) {
        this.y = y;
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }



    //return the euclidean distance from target
    public double calcEuclidDistance(Position targetPosition) {
        double x ,y;

        x = this.x - targetPosition.x;
        y = this.y - targetPosition.y;

        return Math.sqrt(x*x + y*y);
    }

    @Override
    public String toString() {
        return "(" + x + "," +y +")";
    }
}
