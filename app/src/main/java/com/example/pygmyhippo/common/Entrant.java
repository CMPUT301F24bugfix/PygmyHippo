
package com.example.pygmyhippo.common;

/**
 * Entrant is a dataclass which contains relevant information for relating an User account to an
 * Event. Entrant follows a similar schema to how they are stored in firebase. Multiple Entrants
 * will be composed inside of a Event with a singular Entrant linking an Account with their
 * current status relating to the Event. Statuses for entrants are conveyed used EntrantStatus
 * enum below.
 * TODO:
 *  - Use a builder for initialization
 *  - connect to the database
 * @author James Fu, Griffin
 */
public class Entrant {

    private String accountID;
    private String eventID;
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
    public Entrant() {}

    public Entrant(String accountID, EntrantStatus entrantStatus) {
        this.accountID = accountID;
        this.entrantStatus = entrantStatus;
    }

    public Entrant(String eventID, String accountID, String name, String emailAddress, String phoneNumber) {
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

    public String getEventID() {
        return eventID;
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

    public String getProfilePicture() {
        return profilePicture;
    }

    public void setEventID(String eventID) {
        this.eventID = eventID;
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

    public void setProfilePicture(String profilePicture) {
        this.profilePicture = profilePicture;
    }
}