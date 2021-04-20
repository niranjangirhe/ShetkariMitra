package com.ngsolutions.myapplication.HelperClasses.HomeAddapter;

public class FeaturedHelperClass {
    int image;
    String temp, description, day;

    public FeaturedHelperClass(int image, String temp, String description, String day) {
        this.image = image;
        this.temp = temp;
        this.description = description;
        this.day = day;

    }

    public int getImage() {
        return image;
    }

    public String getTemp() {
        return temp;
    }

    public String getDescription() {
        return description;
    }

    public String getDay() {
        return day;
    }
}
