package com.example.eventify;

public class Participants {
    private String username;
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


    public void setContactNumber(String contactNumber) {
        this.contactNumber = contactNumber;
    }
}
