package com.example.edgeservice.model;

import java.util.ArrayList;
import java.util.List;

public class FilledGrapeWine {
    //variables
    private Grape grape;
    private Wine wine;

    //Constructors
    public FilledGrapeWine() {
    }

    public FilledGrapeWine(Grape grape, Wine wine) {
        setGrape(grape);
        setWine(wine);
    }

    //Getters and setters
    public Grape getGrape() {
        return grape;
    }

    public void setGrape(Grape grape) {
        this.grape = grape;
    }

    public Wine getWine() {
        return wine;
    }

    public void setWine(Wine wine) {
        this.wine = wine;
    }
}
