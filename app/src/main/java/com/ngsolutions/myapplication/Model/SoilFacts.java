package com.ngsolutions.myapplication.Model;

public class SoilFacts {
    private String Type;
    private String H1,H2,H3,H4,H5;
    private String A1,A2,A3,A4,A5;

    public SoilFacts()
    {

    }
    public SoilFacts(String type, String h1, String h2, String h3, String h4, String h5, String a1, String a2, String a3, String a4, String a5) {
        Type = type;
        H1 = h1;
        H2 = h2;
        H3 = h3;
        H4 = h4;
        H5 = h5;
        A1 = a1;
        A2 = a2;
        A3 = a3;
        A4 = a4;
        A5 = a5;
    }
    public String getType()
    {
        return Type;
    }

    public String getH1() {
        return H1;
    }

    public String getH2() {
        return H2;
    }

    public String getH3() {
        return H3;
    }

    public String getH4() {
        return H4;
    }

    public String getH5() {
        return H5;
    }

    public String getA1() {
        return A1;
    }

    public String getA2() {
        return A2;
    }

    public String getA3() {
        return A3;
    }

    public String getA4() {
        return A4;
    }

    public String getA5() {
        return A5;
    }
}
