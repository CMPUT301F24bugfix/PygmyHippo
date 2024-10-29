
package com.example.pygmyhippo.common;

/**
 * This is just the Entrant class that works with the viewEntrants Fragment for TESTING PURPOSES
 * Everything currently is the same as entrant but with added fields of name, email, and phone number (and constructor)
 * TODO:
 *  - Finalize how we want to set up the actual entrants class so that it connects well with the database
 *  - Once that is done, delete this file and format viewEntrantsFragment and EntrantArrayAdapter to work with it
 */
public class TESTEntrant {

    private String eventID;
    private String accountID;
    private String name;
    private String emailAddress;
    private String phoneNumber;
    private String profilePicture;
    private EntrantStatus entrantStatus;


    public enum EntrantStatus {
        waitlisted("waitlisted"),
        cancelled("cancelled"),
        invited("invited"),
        accepted("accepted");

        public final String value;

        EntrantStatus(String value) {
            this.value = value;
        }
    };

    public TESTEntrant(String eventID, String accountID, String name, String emailAddress, String phoneNumber) {
        this.eventID = eventID;
        this.accountID = accountID;
        this.name = name;
        this.emailAddress = emailAddress;
        this.phoneNumber = phoneNumber;
    }

    public String getAccountID() {
        return accountID;
    }

    public void setAccountID(String accountID) {
        this.accountID = accountID;
    }

    public EntrantStatus getEntrantStatus() {
        return entrantStatus;
    }

    public void setEntrantStatus(EntrantStatus entrantStatus) {
        this.entrantStatus = entrantStatus;
    }

    public String getName() {
        return name;
    }

    public String getEmailAddress() {
        return emailAddress;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setEmailAddress(String emailAddress) {
        this.emailAddress = emailAddress;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getEventID() {
        return eventID;
    }

    public void setEventID(String eventID) {
        this.eventID = eventID;
    }
}