package com.ngsolutions.myapplication.Mics;

import android.os.AsyncTask;

import java.io.File;
public class PostData extends AsyncTask< String, Void, Void > {
    File image;
    public void setImage(File image)
    {
        this.image = image;
    }

    protected Void doInBackground(String...params) {
        // Create a new HttpClient and Post Header



        return null;
    }
}
