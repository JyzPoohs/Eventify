package com.example.eventify;

public class Feedback {
    private String message;
    private String eventKey;
    private String username;

    public Feedback() {
    }
    public Feedback(String message, String eventKey, String username) {
        this.message = message;
        this.eventKey = eventKey;
        this.username = username;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getEventKey() {
        return eventKey;
    }

    public void setEventKey(String eventKey) {
        this.eventKey = eventKey;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}
