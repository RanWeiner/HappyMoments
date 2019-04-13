package com.example.ran.happymoments.model.face;

public class Eyes {

    public final static float EYES_OPEN_PROBABILITY =  0.5f;

    private float leftEyeOpenProbability, rightEyeOpenProbability;

    public Eyes(float leftEyeOpenProbability, float rightEyeOpenProbability) {
        this.leftEyeOpenProbability = leftEyeOpenProbability;
        this.rightEyeOpenProbability = rightEyeOpenProbability;
    }


    public float getLeftEyeOpenProbability() {
        return leftEyeOpenProbability;
    }

    public void setLeftEyeOpenProbability(float leftEyeOpenProbability) {
        this.leftEyeOpenProbability = leftEyeOpenProbability;
    }

    public float getRightEyeOpenProbability() {
        return rightEyeOpenProbability;
    }

    public void setRightEyeOpenProbability(float rightEyeOpenProbability) {
        this.rightEyeOpenProbability = rightEyeOpenProbability;
    }

    public float getEyesOpenProbability(){
        return (getLeftEyeOpenProbability() + getRightEyeOpenProbability()) / 2;
    }


    public boolean areEyesOpen(){
        if (leftEyeOpenProbability > EYES_OPEN_PROBABILITY
                && rightEyeOpenProbability > EYES_OPEN_PROBABILITY ) {
            return true;
        }
        return false;
    }
}
