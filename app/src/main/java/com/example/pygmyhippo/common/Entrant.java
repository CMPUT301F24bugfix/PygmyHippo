
package com.example.pygmyhippo.common;
/*
This class models Entrants for an event
Purposes:
    - Act as a reference to the Account dataclass
    - Hold the current status for an event
Issues: None
 */

import androidx.annotation.Nullable;

/**
 * Entrant is a dataclass which contains relevant information for relating an User account to an
 * Event. Entrant follows a similar schema to how they are stored in firebase. Multiple Entrants
 * will be composed inside of a Event with a singular Entrant linking an Account with their
 * current status relating to the Event. Statuses for entrants are conveyed used EntrantStatus
 * enum below.
 * TODO:
 *  - Use a builder for initialization
 * @author James Fu, Griffin, Katharine
 */
public class Entrant {
    private String accountID;
    private EntrantStatus entrantStatus;

    @Nullable
    private EntrantStatus notifiedStatus;

    private Double longitude;
    private Double latitude;

    public enum EntrantStatus {
        waitlisted("waitlisted"),
        cancelled("cancelled"),
        invited("invited"),
        accepted("accepted"),
        rejected("rejected");

        public final String value;

        EntrantStatus(String value) {
            this.value = value;
        }
    };

    /**
     * This method returns the Account ID used to reference this entrant's corresponding account
     * @return accountID
     */
    public String getAccountID() {
        return accountID;
    }

    /**
     * This method will set the corresponding account ID for the entrant
     * @param accountID The corresponding accountID
     */
    public void setAccountID(String accountID) {
        this.accountID = accountID;
    }

    /**
     * This method will return the status of an entrant given in their event
     * @return entrantStatus
     */
    public EntrantStatus getEntrantStatus() {
        return entrantStatus;
    }

    /**
     * This method will alter the status of the entrant of an event
     * @param entrantStatus The new status
     */
    public void setEntrantStatus(EntrantStatus entrantStatus) {
        this.entrantStatus = entrantStatus;
    }

    public Entrant() {}

    public Entrant(String accountID, EntrantStatus entrantStatus) {
        this.accountID = accountID;
        this.entrantStatus = entrantStatus;
    }

    /**
     * Get the longitude location of the entrant when they signed up
     * @return The longitude they were at when they signed up for the event
     */
    public Double getLongitude() {
        return longitude;
    }

    /**
     * Get the latitude location of the entrant when they signed up
     * @return The latitude they were at when they signed up for the event
     */
    public Double getLatitude() {
        return latitude;
    }

    /**
     * This method will set the longitude of the entrant when they sign up for an event
     * @param longitude Their longitude location when they signed up
     */
    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }

    /**
     * This method will set the latitude of the entrant when they sign up for an event
     * @param latitude Their latitude location when they signed up
     */
    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    @Nullable
    public EntrantStatus getNotifiedStatus() {
        return notifiedStatus;
    }

    public void setNotifiedStatus(@Nullable EntrantStatus notifiedStatus) {
        this.notifiedStatus = notifiedStatus;
    }
}