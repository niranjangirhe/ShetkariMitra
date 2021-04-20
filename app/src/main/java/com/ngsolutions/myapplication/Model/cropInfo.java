package com.ngsolutions.myapplication.Model;

public class cropInfo {
    int cropCode;
    String cropName;
    String cropMarathi;
    String cropHindi;

    public cropInfo(int cropCode, String cropName, String cropMarathi, String cropHindi) {
        this.cropCode = cropCode;
        this.cropName = cropName;
        this.cropMarathi = cropMarathi;
        this.cropHindi = cropHindi;
    }

    public int getCropCode() {
        return cropCode;
    }

    public String getCropName() {
        return cropName;
    }

    public String getCropMarathi() {
        return cropMarathi;
    }

    public String getCropHindi() {
        return cropHindi;
    }
}
