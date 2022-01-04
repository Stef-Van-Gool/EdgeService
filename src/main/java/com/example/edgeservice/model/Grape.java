package com.example.edgeservice.model;

public class Grape {
    //Variables
    private int id;
    private String grapeName;
    private String region;
    private String country;

    //Constructors
    public Grape() {}

    public Grape(String grapeName, String region, String country) {
        setGrapeName(grapeName);
        setRegion(region);
        setCountry(country);
    }

    //Getters and setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getGrapeName() {
        return grapeName;
    }

    public void setGrapeName(String grapeName) {
        this.grapeName = grapeName;
    }

    public String getRegion() {
        return region;
    }

    public void setRegion(String region) {
        this.region = region;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }
}
