/*
 * TODO: Decide how images are gonna be stored and set the appropriate datatype for eventPoster.
 *
 * Event Dataclass.
 * The structure of this class should be similar to the schema used for Event Documents
 * on firebase. This class contains all the information for displaying a single event in
 * the app. One Event has only one organizer associated with it, but one organizer can be
 * associated with multiple events.
 *
 * EventStatus Enum
 * Events can only have one status at any given time. Events with status that are not listed
 * in EventStatus enum should be supported and will lead to undefined behavior.
 */


package com.example.pygmyhippo.common;

import java.util.ArrayList;

/**
 * Our event class
 * TODO:
 *  - Use a builder for initialization
 *  - connect to the database
 *  - add generation of hashdata and qr code
 * @author James, Griffin
 */
public class Event {

    private String eventID;
    private String organiserID;
    private ArrayList<Entrant> entrants;

    private String title;
    private String location;
    private String date;
    private String time;

    private String description;
    private String cost;
    private String eventPoster;

    private int eventLimitCount;
    private int eventWinnersCount;
    private Boolean enableGeolocation;

    private EventStatus eventStatus;

    public enum EventStatus {
        cancelled("cancelled"),
        ongoing("ongoing");

        public final String value;

        EventStatus(String value) {
            this.value = value;
        }
    }

    public Event() {}

    public Event(String title, String location, String date, String time, EventStatus eventStatus) {
        this.title = title;
        this.location = location;
        this.date = date;
        this.time = time;
        this.eventStatus = eventStatus;
    }

    public String getEventID() {
        return eventID;
    }

    public void setEventID(String eventID) {
        this.eventID = eventID;
    }

    public String getOrganiserID() {
        return organiserID;
    }

    public void setOrganiserID(String organiserID) {
        this.organiserID = organiserID;
    }

    public ArrayList<Entrant> getEntrants() {
        return entrants;
    }

    public void setEntrants(ArrayList<Entrant> entrants) {
        this.entrants = entrants;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getCost() {
        return cost;
    }

    public void setCost(String cost) {
        this.cost = cost;
    }

    public String getEventPoster() {
        // FIXME: how are we going to handle images since this would have to return a link to image on firestore
        return eventPoster;
    }

    public void setEventPoster(String eventPoster) {
        // FIXME: Need to think about how were going to upload images sicne they will be stored in Firestore
        this.eventPoster = eventPoster;
    }

    public int getEventLimitCount() {
        return eventLimitCount;
    }

    public void setEventLimitCount(int eventLimitCount) {
        this.eventLimitCount = eventLimitCount;
    }

    public int getEventWinnersCount() {
        return eventWinnersCount;
    }

    public void setEventWinnersCount(int eventWinnersCount) {
        this.eventWinnersCount = eventWinnersCount;
    }

    public Boolean getGeolocation() {
        return enableGeolocation;
    }

    public void setGeolocation(Boolean enableGeolocation) {
        this.enableGeolocation = enableGeolocation;
    }


    public EventStatus getEventStatus() {
        return eventStatus;
    }

    public void setEventStatus(EventStatus eventStatus) {
        this.eventStatus = eventStatus;
    }

    public String getTitle() {
        return title;
    }
}

