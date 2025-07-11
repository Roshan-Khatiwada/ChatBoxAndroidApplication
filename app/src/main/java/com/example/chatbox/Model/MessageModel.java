package com.example.chatbox.Model;

import android.util.Log;

import com.google.firebase.Timestamp;

public class MessageModel {
    public static final String MESSAGE_TYPE_TEXT = "text";
    public static final String MESSAGE_TYPE_IMAGE = "image";

    private String message;
    private String senderId;
    private Timestamp timestamp;
    private String messageType; // Added for distinguishing between text and image messages
    private String imageUrl; // Added to store the URL of the image
    private String profilepicuri; // Added to store the URL of the profile picture

    public MessageModel() {
        // Required empty constructor
    }

    public MessageModel(String message, String senderId, Timestamp timestamp, String messageType, String imageUrl) {
        this.message = message;
        this.senderId = senderId;
        this.timestamp = timestamp;
        this.messageType = messageType;
        this.imageUrl = imageUrl;

    }


    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getSenderId() {
        return senderId;
    }

    public void setSenderId(String senderId) {
        this.senderId = senderId;
    }

    public Timestamp getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Timestamp timestamp) {
        this.timestamp = timestamp;
    }

    public String getMessageType() {
        return messageType;
    }

    public void setMessageType(String messageType) {
        this.messageType = messageType;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

}
