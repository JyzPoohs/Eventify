package com.example.eventify;

public class Event {
    private String eventName;
    private String eventDescription;
    private String eventTheme;
    private String eventLocation;
    private String eventDateTime;
    private String imageUrl;

    public Event(String eventName, String eventDescription, String eventTheme, String eventLocation, String eventDateTime) {
        this.eventName = eventName;
        this.eventDescription = eventDescription;
        this.eventTheme = eventTheme;
        this.eventLocation = eventLocation;
        this.eventDateTime = eventDateTime;
    }

    public Event(String imageUrl) {
        this.imageUrl = imageUrl;
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

    public String getEventTheme() {
        return eventTheme;
    }

    public void setEventTheme(String eventTheme) {
        this.eventTheme = eventTheme;
    }

    public String getEventLocation() {
        return eventLocation;
    }

    public void setEventLocation(String eventLocation) {
        this.eventLocation = eventLocation;
    }

    public String getEventDateTime() {
        return eventDateTime;
    }

    public void setEventDateTime(String eventDateTime) {
        this.eventDateTime = eventDateTime;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }
}
