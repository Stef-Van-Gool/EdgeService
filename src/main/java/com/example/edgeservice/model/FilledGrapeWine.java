package com.example.edgeservice.model;

import java.util.ArrayList;
import java.util.List;

public class FilledGrapeWine {
    //variables
    private String grapeName;
    private List<Wine> wines;

    //Constructors
    public FilledGrapeWine(Grape grape, List<Wine> wines) {
        setGrapeName(grape.getGrapeName());
        setWines(wines);
    }

    public FilledGrapeWine(Grape grape, Wine wine) {
        setGrapeName(grape.getGrapeName());
        wines.add(wine);
        setWines(wines);
    }

    //Getters and setters
    public String getGrapeName() {
        return grapeName;
    }

    public void setGrapeName(String grapeName) {
        this.grapeName = grapeName;
    }

    public List<Wine> getWines() {
        return wines;
    }

    public void setWines(List<Wine> wines) {
        this.wines = wines;
    }
}
