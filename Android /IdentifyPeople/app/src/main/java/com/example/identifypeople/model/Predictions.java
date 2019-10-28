package com.example.identifypeople.model;

import java.util.ArrayList;

public class Predictions {
    private ArrayList<String>predictions;
    private boolean success;


    public ArrayList<String> getPredictions() {
        return predictions;
    }

    public boolean isSuccess() {
        return success;
    }
}
