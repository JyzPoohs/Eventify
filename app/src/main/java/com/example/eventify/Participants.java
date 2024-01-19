package com.example.eventify;

public class Participants {
    private String username;
    private String eventKey;
    private String userID;
    private String guestKey;
    private String contactNumber;

    public void setUsername(String username) {
        this.username = username;
    }

    public Participants(String username,String contactNumber) {
        this.username = username;
        this.contactNumber = contactNumber;
    }

    public String getGuestKey() {
        return guestKey;
    }

    public void setGuestKey(String guestKey) {
        this.guestKey = guestKey;
    }

    public String getUsername() {
        return username;
    }

    public String getContactNumber() {
        return contactNumber;
    }

    public String getEventKey() {
        return eventKey;
    }

    public void setEventKey(String eventKey) {
        this.eventKey = eventKey;
    }

    public Participants(String username, String eventKey, String userID, String guestKey, String contactNumber) {
        this.username = username;
        this.eventKey = eventKey;
        this.userID = userID;
        this.guestKey = guestKey;
        this.contactNumber = contactNumber;
    }

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public void setContactNumber(String contactNumber) {
        this.contactNumber = contactNumber;
    }
}
