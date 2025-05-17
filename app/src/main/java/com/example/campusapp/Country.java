package com.example.campusapp;
public class Country {
    private String name;
    private String capital;
    private String flag;
    private String iso2;
    private String iso3;
    private double latitude;
    private double longitude;

    public Country() {
        // Required empty constructor for Firebase
    }

    // Getters
    public String getName() { return name; }
    public String getCapital() { return capital; }
    public String getFlag() { return flag; }
    public String getIso2() { return iso2; }
    public String getIso3() { return iso3; }
    public double getLatitude() { return latitude; }
    public double getLongitude() { return longitude; }
}