package com.example.eventify;

public class User {
    private String userId;
    private String userName;
    private String contactNum;
    private String email;

    private boolean selected;

    public User(String userName) {
        this.userName = userName;
        this.selected = false;
    }
    public String getUserId() {
        return userId;
    }

    public String getUserName() {
        return userName;
    }

    public User(String userId, String userName, String contactNum, String email) {
        this.userId = userId;
        this.userName = userName;
        this.contactNum = contactNum;
        this.email = email;
    }

    public String getContactNum() {
        return contactNum;
    }

    public String getEmail() {
        return email;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public void setContactNum(String contactNum) {
        this.contactNum = contactNum;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }

    public boolean isSelected() {
        return selected;
    }
}
