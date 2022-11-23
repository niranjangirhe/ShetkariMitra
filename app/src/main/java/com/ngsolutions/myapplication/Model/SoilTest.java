package com.ngsolutions.myapplication.Model;

public class SoilTest {
    public String link,user;
    public float OC,N,P,K;
    public long systemType;


    public SoilTest(String link, String user, float OC, float n, float p, float k, long systemType) {
        this.link = link;
        this.user = user;
        this.OC = OC;
        N = n;
        P = p;
        K = k;
        this.systemType = systemType;
    }
}
