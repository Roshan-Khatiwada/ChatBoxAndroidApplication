package com.example.chatbox.Model;

import java.util.Date;

public class VideoModel {

    // Fields to represent video information
    private Date timestamp;
    private String title;
    private String username;
    private String vurl; // Video URL


    // Default constructor required for Firebase


    public VideoModel(Date timestamp, String title, String username, String vurl) {
        this.timestamp = timestamp;
        this.title = title;
        this.username = username;
        this.vurl = vurl;
    }
VideoModel() {
    }

    public Date getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getVurl() {
        return vurl;
    }

    public void setVurl(String vurl) {
        this.vurl = vurl;
    }

}