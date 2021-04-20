package com.ngsolutions.myapplication.Model;

import android.net.Uri;

public class MessageModel {
    String Name,Message,index,time;
    Boolean SorR,haveImage=false;
    Uri uri=null;

    public MessageModel(String name, String message, Boolean sorR, Uri uri, Boolean haveImage,String index,String time) {
        Name = name;
        Message = message;
        SorR = sorR;
        this.uri = uri;
        this.haveImage = haveImage;
        this.index = index;
        this.time = time;
    }

    public MessageModel(String name, String message, Boolean sorR, String index,String time) {
        Name = name;
        Message = message;
        SorR = sorR;
        this.index = index;
        this.time = time;
    }

    public String getTime() {
        return time;
    }

    public String getIndex() {
        return index;
    }

    public Boolean getHaveImage() {
        return haveImage;
    }

    public Uri getUri() {
        return uri;
    }

    public void setHaveImage(Boolean haveImage) {
        this.haveImage = haveImage;
    }

    public void setUri(Uri uri) {
        this.uri = uri;
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public String getMessage() {
        return Message;
    }

    public void setMessage(String message) {
        Message = message;
    }

    public Boolean getSorR() {
        return SorR;
    }

    public void setSorR(Boolean sorR) {
        SorR = sorR;
    }
}
