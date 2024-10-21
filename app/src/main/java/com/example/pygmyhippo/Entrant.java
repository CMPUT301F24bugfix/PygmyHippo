
package com.example.pygmyhippo;

/**
 * Entrant is a dataclass which contains relevant information for relating an User account to an
 * Event. Entrant follows a similar schema to how they are stored in firebase. Multiple Entrants
 * will be composed inside of a Event with a singular Entrant linking an Account with their
 * current status relating to the Event. Statuses for entrants are conveyed used EntrantStatus
 * enum below.
 *
 * @author James Fu
 */
public class Entrant extends Account{

    private String accountID;

    private EntrantStatus entrantStatus;

    public Entrant(String accountID, String name, String pronouns, String phoneNumber, String emailAddress, String deviceID, String profilePicture, String location, boolean receiveNotifications, boolean enableGeolocation) {
        super(accountID, name, pronouns, phoneNumber, emailAddress, deviceID, profilePicture, location, receiveNotifications, enableGeolocation);
    }

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
}