package com.example.chatbox.Model;

import java.sql.Timestamp;

public class UserModel {
    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public Timestamp getCreatedTimeStamp() {
        return createdTimeStamp;
    }

    public void setCreatedTimeStamp(Timestamp createdTimeStamp) {
        this.createdTimeStamp = createdTimeStamp;
    }

    String username;
    String phone;
    Timestamp createdTimeStamp;

    public UserModel(String username, String phone, Timestamp createdTimeStamp) {
        this.username = username;
        this.phone = phone;
        this.createdTimeStamp = createdTimeStamp;
    }
}
