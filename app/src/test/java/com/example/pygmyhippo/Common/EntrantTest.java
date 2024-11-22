package com.example.pygmyhippo.Common;

/*
The Unit tests for the data class entrant
Purpose:
    - To test the getters and setters for entrants
Issues:
    - None
 */
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

import com.example.pygmyhippo.common.Entrant;

import org.junit.Before;
import org.junit.Test;

public class EntrantTest {
    private Entrant testEntrant;

    @Before
    public void setUp() {
        testEntrant = new Entrant("account1", Entrant.EntrantStatus.waitlisted);
    }

    @Test
    public void testGetAccountID() {
        assertEquals("account1", testEntrant.getAccountID());
    }

    @Test
    public void testSetAccountID() {
        testEntrant.setAccountID("account2");
        assertEquals("account2", testEntrant.getAccountID());
    }

    @Test
    public void testGetEntrantStatus() {
        assertEquals("waitlisted", testEntrant.getEntrantStatus().value);
    }

    @Test
    public void testSetEntrantStatusInvited() {
        testEntrant.setEntrantStatus(Entrant.EntrantStatus.invited);
        assertEquals("invited", testEntrant.getEntrantStatus().value);
    }

    @Test
    public void testSetEntrantStatusAccepted() {
        testEntrant.setEntrantStatus(Entrant.EntrantStatus.accepted);
        assertEquals("accepted", testEntrant.getEntrantStatus().value);
    }

    @Test
    public void testSetEntrantStatusCancelled() {
        testEntrant.setEntrantStatus(Entrant.EntrantStatus.cancelled);
        assertEquals("cancelled", testEntrant.getEntrantStatus().value);
    }

    @Test
    public void testLongitude() {
        assertEquals(null, testEntrant.getLongitude());

        double longitude = -77.771630;

        // Test setting negative value
        testEntrant.setLongitude(longitude);
        assertEquals(longitude, testEntrant.getLongitude(), 0.000001);

        // Test setting positive value
        longitude = 48.167404;
        testEntrant.setLongitude(longitude);
        assertEquals(longitude, testEntrant.getLongitude(), 0.000001);

        // Test setting zero value
        longitude = 0;
        testEntrant.setLongitude(longitude);
        assertEquals(longitude, testEntrant.getLongitude(), 0.000001);
    }

    @Test
    public void testLatitude() {
        assertEquals(null, testEntrant.getLatitude());

        Double latitude = -77.771630;

        // Test setting negative value
        testEntrant.setLatitude(latitude);
        assertEquals(latitude, testEntrant.getLatitude(), 0.000001);

        // Test setting positive value
        latitude = 48.167404;
        testEntrant.setLatitude(latitude);
        assertEquals(latitude, testEntrant.getLatitude(), 0.000001);

        // Test setting zero value
        latitude = 0.0;
        testEntrant.setLatitude(latitude);
        assertEquals(latitude, testEntrant.getLatitude(), 0.000001);
    }
}
