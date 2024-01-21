package com.example.eventify;

public class EventInvitation {
    private String eventKey;
    private String username;
    private String userId;
    private String eventOrganizer;
    private String eventName;
    private String eventLocation;
    private String eventLocationImg;
    private String eventDescription;
    private String textMessage;
    private String voiceMessage;
    private String eventStart;
    private String eventEnd;
    private String eventType;

    public EventInvitation(String eventKey, String username, String userId, String eventOrganizer, String eventName, String eventLocation, String eventLocationImg, String eventDescription, String textMessage, String voiceMessage,String eventStart,String eventEnd,String eventType) {
        this.eventKey = eventKey;
        this.username = username;
        this.userId = userId;
        this.eventOrganizer = eventOrganizer;
        this.eventName = eventName;
        this.eventLocation = eventLocation;
        this.eventLocationImg = eventLocationImg;
        this.eventDescription = eventDescription;
        this.textMessage = textMessage;
        this.voiceMessage = voiceMessage;
        this.eventStart=eventStart;
        this.eventEnd=eventEnd;
        this.eventType=eventType;
    }

    public String getEventKey() {
        return eventKey;
    }

    public String getEventType() {
        return eventType;
    }

    public void setEventType(String eventType) {
        this.eventType = eventType;
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

    public void setEventKey(String eventKey) {
        this.eventKey = eventKey;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEventLocation() {
        return eventLocation;
    }

    public void setEventLocation(String eventLocation) {
        this.eventLocation = eventLocation;
    }

    public String getEventLocationImg() {
        return eventLocationImg;
    }

    public void setEventLocationImg(String eventLocationImg) {
        this.eventLocationImg = eventLocationImg;
    }

    public String getEventDescription() {
        return eventDescription;
    }

    public void setEventDescription(String eventDescription) {
        this.eventDescription = eventDescription;
    }

    public String getTextMessage() {
        return textMessage;
    }

    public void setTextMessage(String textMessage) {
        this.textMessage = textMessage;
    }

    public String getVoiceMessage() {
        return voiceMessage;
    }

    public void setVoiceMessage(String voiceMessage) {
        this.voiceMessage = voiceMessage;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getEventOrganizer() {
        return eventOrganizer;
    }

    public void setEventOrganizer(String eventOrganizer) {
        this.eventOrganizer = eventOrganizer;
    }

    public String getEventName() {
        return eventName;
    }

    public void setEventName(String eventName) {
        this.eventName = eventName;
    }

}
