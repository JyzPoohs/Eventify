package com.example.eventify;

public class Event {
    private String userId;
    private String eventKey;
    private String eventName;
    private String eventDescription;
    private String eventType;
    private String eventLocation;
    private String eventStart;
    private String eventEnd;
    private String imageUrl;

    public Event() {
    }

    public Event(String eventKey, String userId, String eventName, String eventDescription, String eventType, String eventLocation, String eventStart, String eventEnd) {
        this.userId = userId;
        this.eventKey = eventKey;
        this.eventName = eventName;
        this.eventDescription = eventDescription;
        this.eventType = eventType;
        this.eventLocation = eventLocation;
        this.eventStart = eventStart;
        this.eventEnd = eventEnd;
    }

    public Event(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getEventKey() {
        return eventKey;
    }

    public void setEventKey(String eventKey) {
        this.eventKey = eventKey;
    }

    public String getEventName() {
        return eventName;
    }

    public void setEventName(String eventName) {
        this.eventName = eventName;
    }

    public String getEventDescription() {
        return eventDescription;
    }

    public void setEventDescription(String eventDescription) {
        this.eventDescription = eventDescription;
    }

    public String getEventType() {
        return eventType;
    }

    public void setEventType(String eventType) {
        this.eventType = eventType;
    }

    public String getEventLocation() {
        return eventLocation;
    }

    public void setEventLocation(String eventLocation) {
        this.eventLocation = eventLocation;
    }

    public String getEventStart() {
        return eventStart;
    }

    public void setEventStart(String eventStart) {
        this.eventStart = eventStart;
    }

    public String getEventEnd() {
        return eventEnd;
    }

    public void setEventEnd(String eventEnd) {
        this.eventEnd = eventEnd;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }
}
