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
 *
 * Issues: Somehow two different attributes came up for event title, will have to refactor
 */


package com.example.pygmyhippo.common;

import java.util.ArrayList;

/**
 * Our event class
 * TODO:
 *  - Use a builder for initialization
 *  - add generation of hashdata and qr code (storing too)
 * @author James, Griffin, Katharine
 */
public class Event {
    private String eventTitle;
    private String eventID;
    private String organiserID;
    private ArrayList<Entrant> entrants;

    private String title; //deprecated
    private String location;
    private String date;
    private String time;

    private String description;
    private String cost;
    private String eventPoster;

    private int eventLimitCount;
    private int eventWinnersCount;
    private Boolean enableGeolocation;


    private int hashcode;

    private EventStatus eventStatus;

    public enum EventStatus {
        cancelled("cancelled"),
        ongoing("ongoing");

        public final String value;

        EventStatus(String value) {
            this.value = value;
        }
    }

    public Event(String eventTitle, String location, String date, String time, EventStatus eventStatus) {
        this.eventTitle = eventTitle;
        this.location = location;
        this.date = date;
        this.time = time;
        this.eventStatus = eventStatus;
    }


    // constructor to make event
    public Event() {this.entrants = new ArrayList<>();}

    public Event(String eventTitle, String eventID, String organiserID, ArrayList<Entrant> entrants, String location,
                 String date, String time, String description, String cost, String eventPoster,
                 EventStatus eventStatus, Boolean enableGeolocation) {
        this.eventTitle = eventTitle;
        this.eventID = eventID;
        this.organiserID = organiserID;
        this.entrants = entrants;
        this.location = location;
        this.date = date;
        this.time = time;
        this.description = description;
        this.cost = cost;
        this.eventPoster = eventPoster;
        this.eventStatus = eventStatus;
        this.enableGeolocation = enableGeolocation;
    }

    /**
     * This method will generate the hashcode of the Event.
     * returns boolean of if successful
     *
     * @return boolean
     */
    public boolean tryGenerateHashcode(){
        if(!eventID.isEmpty()){
            hashcode = eventID.hashCode();
            return true;
        }
        return false;
    }

    /**
     * This method will validate the hashcode of the Event.
     *
     * @return boolean
     */
    public boolean isValidHashcode(){
        if(!eventID.isEmpty()){
            int comphashcode = eventID.hashCode();
            if(comphashcode == hashcode){
                return true;
            }
        }
        return false;
    }

    /**
     * This method will return the hashcode of the Event
     * @return hashcode
     */
    public int getHashcode() {
        return hashcode;
    }

    /**
     * This method will return the enableGeolocation of the Event
     * @return enableGeolocation
     */
    public Boolean getEnableGeolocation() {
        return enableGeolocation;
    }

    /**
     * This method will set the enableGeolocation of the Event
     * @param enableGeolocation
     */
    public void setEnableGeolocation(Boolean enableGeolocation) {
        this.enableGeolocation = enableGeolocation;
    }

    /**
     * This method will return the text title of the Event
     * @return eventTitle
     */
    public String getEventTitle() {
        return eventTitle;
    }

    /**
     * This method will update the event title
     * @param eventTitle The new title
     */
    public void setEventTitle(String eventTitle) {
        this.eventTitle = eventTitle;
    }

    /**
     * This method will return the ID of the event (corresponds with firebase ID"
     * @return eventID
     */
    public String getEventID() {
        return eventID;
    }

    /**
     * This method will update the ID of the event
     * @param eventID The new ID
     */
    public void setEventID(String eventID) {
        this.eventID = eventID;
    }

    /**
     * This method will get the ID of the organiser who made this event
     * @return organiserID
     */
    public String getOrganiserID() {
        return organiserID;
    }

    /**
     * This method will set the ID of the organiser who made the event
     * @param organiserID The organiser's ID
     */
    public void setOrganiserID(String organiserID) {
        this.organiserID = organiserID;
    }

    /**
     * This method will return the list of entrants who joined the waitlist of the event
     * @return entrants
     */
    public ArrayList<Entrant> getEntrants() {
        return entrants;
    }

    /**
     * This method will check if an event has a certain entrant (based on accountID ONLY,
     * because there should not be the same entrant with different statuses in an event)
     * @param entrant The entrant to check for
     * @return true if the entrant is in the event, false otherwise
     */
    public Boolean hasEntrant(Entrant entrant) {
        if (this.entrants != null) {
            // Go through the whole list of entrants until you find one with matching attributes
            for (int index = 0; index < entrants.size(); index++) {
                if (entrant.getAccountID().equals(entrants.get(index).getAccountID())) {
                    // If the attributes are equal, then the list has the entrant
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * This method will set the list of entrants in the waitlist for this given event
     * @param entrants The new list of entrants
     */
    public void setEntrants(ArrayList<Entrant> entrants) {
        this.entrants = entrants;
    }

    /**
     * This method will add an entrant to the event's waitlist
     * @param entrant The entrant to add
     */
    public void addEntrant(Entrant entrant) {
        if (this.entrants == null) {
            this.entrants = new ArrayList<>();
        }
        this.entrants.add(entrant);
    }

    /**
     * This method will remove the given entrant from the event's waitlist
     * @param entrant The entrant to remove
     */
    public void removeEntrant(Entrant entrant) {
        if (entrants != null) {
            // If the attributes are in the entrants list, then the list has the entrant, so remove it
            this.entrants.removeIf(i -> (entrant.getAccountID().equals(i.getAccountID()) && i.getEntrantStatus().value.equals(entrant.getEntrantStatus().value)));
        }
    }

    /**
     * This method will return the event location
     * @return location
     */
    public String getLocation() {
        return location;
    }

    /**
     * This method will set the event location
     * @param location The new location
     */
    public void setLocation(String location) {
        this.location = location;
    }

    /**
     * This method will get the event's date
     * @return date
     */
    public String getDate() {
        return date;
    }

    /**
     * This method will set the event's date
     * @param date The date to set it to
     */
    public void setDate(String date) {
        this.date = date;
    }

    /**
     * This method will return the time of the event
     * @return time
     */
    public String getTime() {
        return time;
    }

    /**
     * This method will change the time of the event
     * @param time The new time
     */
    public void setTime(String time) {
        this.time = time;
    }

    /**
     * This method will return the event description
     * @return description
     */
    public String getDescription() {
        return description;
    }

    /**
     * This method will change the description of the event
     * @param description The new description
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * This method will return the event price
     * @return cost
     */
    public String getCost() {
        return cost;
    }

    /**
     * This method will update the event price
     * @param cost The new cost
     */
    public void setCost(String cost) {
        this.cost = cost;
    }

    /**
     * This method will return the link to the event poster
     * @return the poster link
     */
    public String getEventPoster() {
        return eventPoster;
    }

    /**
     * This method will update the event poster link
     * @param eventPoster The new poster link
     */
    public void setEventPoster(String eventPoster) {
        this.eventPoster = eventPoster;
    }

    /**
     * This method will return the max limit of entrants for the event
     * @return eventLimitCount
     */
    public int getEventLimitCount() {
        return eventLimitCount;
    }

    /**
     * This method will set the max limit of entrants allowed in the event (note limit is optional)
     * @param eventLimitCount The new limit
     * TODO: Make checks to ensure the limit amount is positive
     */
    public void setEventLimitCount(int eventLimitCount) {
        this.eventLimitCount = eventLimitCount;
    }

    /**
     * This method will return the number of lottery winners for this event
     * @return eventWinnersCount
     */
    public int getEventWinnersCount() {
        return eventWinnersCount;
    }

    /**
     * This method will set the number of winners for the lottery in the event
     * @param eventWinnersCount The new number of lottery winners
     * TODO: Set checks to make sure amount is > 0
     */
    public void setEventWinnersCount(int eventWinnersCount) {
        this.eventWinnersCount = eventWinnersCount;
    }

    /**
     * This method will return if the event has geolocation enabled or not
     * @return enableGeolocation
     */
    public Boolean getGeolocation() {
        return enableGeolocation;
    }

    /**
     * This method will update the geolocation status
     * @param enableGeolocation true to enable geolocation, false otherwise
     */
    public void setGeolocation(Boolean enableGeolocation) {
        this.enableGeolocation = enableGeolocation;
    }

    /**
     * This method will return the status of the event (whether the lottery is closed or not)
     * @return eventStatus
     */
    public EventStatus getEventStatus() {
        return eventStatus;
    }

    /**
     * This method will update the status of the event
     * @param eventStatus the new event status
     */
    public void setEventStatus(EventStatus eventStatus) {
        this.eventStatus = eventStatus;
    }

    /**
     * This method will count all the entrants that are invited or accepted an invitation and return it.
     * Used to check if there are still open spots available for a lottery redraw
     * @return The number of event spots already taken
     */
    public Integer getCurrentWinners() {
        Integer count = 0;

        // Go through the list of entrants
        for (int index = 0; index < entrants.size(); index++) {
            if (entrants.get(index).getEntrantStatus().value.equals("invited") || entrants.get(index).getEntrantStatus().value.equals("accepted")) {
                // Increase the count if the entrant status is invited or accepted
                count++;
            }
        }
        return count;
    }

    /**
     * This method will check if the event has open positions for a lottery redraw or not
     * @return true if there are open spots, false otherwise
     */
    public Boolean hasAvailability() {
        if (getCurrentWinners() < eventWinnersCount) {
            // Open spots available
            return true;
        }
        return false;
    }

    /**
     * This method will return the number of entrants in the event that are waitlisted
     * @return The number of waitlisted entrants
     */
    public Integer getNumberWaitlisted() {
        Integer waitlistCount = 0;
        for (int index = 0; index < entrants.size(); index++) {
            if (entrants.get(index).getEntrantStatus().value.equals("waitlisted")) {
                waitlistCount++;
            }
        }
        return waitlistCount;
    }

    /**
     * This method will return the number of entrants in the event that are lost
     * @return The number of waitlisted entrants
     */
    public Integer getNumberLost() {
        Integer lostCount = 0;
        for (int index = 0; index < entrants.size(); index++) {
            if (entrants.get(index).getEntrantStatus().value.equals("lost")) {
                lostCount++;
            }
        }
        return lostCount;
    }

    /**
     * This method will return the entrant with the given ID
     * @param accountID The Id of the entrant we want
     * @return The entrant with the matching ID, return null otherwise
     */
    public Entrant getEntrant(String accountID) {
        for (int index = 0; index < entrants.size(); index++) {
            if (entrants.get(index).getAccountID().equals(accountID)) {
                // Found the entrant, so return it
                return entrants.get(index);
            }
        }
        return null;
    }

}

