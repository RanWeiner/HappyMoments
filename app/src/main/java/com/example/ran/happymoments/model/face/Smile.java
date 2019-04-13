package com.example.ran.happymoments.model.face;

public class Smile {

    public final static float SMILING_ACCEPT_PROBABILITY =  0.5f;

    private float smilingProbability;

    public Smile(float smilingProbability){
        this.smilingProbability = smilingProbability;
    }

    public float getSmilingProbability() {
        return smilingProbability;
    }

    public void setSmilingProbability(float smilingProbability) {
        this.smilingProbability = smilingProbability;
    }

    public boolean isSmiling(){
        if (smilingProbability > SMILING_ACCEPT_PROBABILITY) {
            return true;
        }
        return false;
    }
}
