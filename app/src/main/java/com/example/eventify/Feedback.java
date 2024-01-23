package com.example.eventify;

public class Feedback {
    private String message;
    private String eventKey;
//    private String username;
    private String userId;
    private String feedbackId;

    public Feedback() {
    }

    public Feedback(String message, String eventKey, String userId, String feedbackId) {
        this.message = message;
        this.eventKey = eventKey;
        this.userId = userId;
        this.feedbackId = feedbackId;
    }

    public String getFeedbackId() {
        return feedbackId;
    }

    public void setFeedbackId(String feedbackId) {
        this.feedbackId = feedbackId;
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

//    public String getUsername() {
//        return username;
//    }
//
//    public void setUsername(String username) {
//        this.username = username;
//    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }
}
