package com.ngsolutions.myapplication.Model;

public class MyQueryModel {
    String CropName;
    String Message;
    String ReplyForum;

    public MyQueryModel(String cropName, String message, String replyForum) {
        CropName = cropName;
        Message = message;
        ReplyForum = replyForum;
    }

    public String getCropName() {
        return CropName;
    }

    public String getMessage() {
        return Message;
    }

    public String getReplyForum() {
        return ReplyForum;
    }
}
