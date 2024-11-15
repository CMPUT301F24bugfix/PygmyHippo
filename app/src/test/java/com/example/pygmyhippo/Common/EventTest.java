package com.example.pygmyhippo.Common;
/*
The unit tests for the Event data class
Purpose:
    - To test the methods of the event class
Issues: No tests for trying to enter a negative limit amount (and for winner count)
 */
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;

import com.example.pygmyhippo.common.Entrant;
import com.example.pygmyhippo.common.Event;

import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;

public class EventTest {
    private Event testEvent;
    private ArrayList<Entrant> entrants;

    @Before
    public void setUp() {
        entrants = new ArrayList<>();
        entrants.add(new Entrant("account1", Entrant.EntrantStatus.invited));
        entrants.add(new Entrant("account2", Entrant.EntrantStatus.waitlisted));
        entrants.add(new Entrant("account3", Entrant.EntrantStatus.cancelled));
        entrants.add(new Entrant("account4", Entrant.EntrantStatus.accepted));

        testEvent = new Event(
                "event_title",
                "event1",
                "organiser1",
                entrants,
                "50th Street",
                "Oct 30th, 2024",
                "3am-6am",
                "Some description",
                "$20",
                "https//poster",
                Event.EventStatus.cancelled,
                true
        );
        testEvent.setEventLimitCount(10);
        testEvent.setEventWinnersCount(3);
    }

    @Test
    public void testGetEventTitle() {
        assertEquals("event_title", testEvent.getEventTitle());
    }

    @Test
    public void testSetEventTitle() {
        testEvent.setEventTitle("new_title");
        assertEquals("new_title", testEvent.getEventTitle());
    }

    @Test
    public void testGetEventID() {
        assertEquals("event1", testEvent.getEventID());
    }

    @Test
    public void testSetEventID() {
        testEvent.setEventID("event2");
        assertEquals("event2", testEvent.getEventID());
    }

    @Test
    public void testGetOrganiserID() {
        assertEquals("organiser1", testEvent.getOrganiserID());
    }

    @Test
    public void testSetOrganiserID() {
        testEvent.setOrganiserID("organiser2");
        assertEquals("organiser2", testEvent.getOrganiserID());
    }

    @Test
    public void testGetEntrants() {
        assertEquals(entrants, testEvent.getEntrants());
        assertEquals(4, testEvent.getEntrants().size());
    }

    @Test
    public void testHasEntrant() {
        Entrant hasEntrant = new Entrant("account1", Entrant.EntrantStatus.invited);
        Entrant noEntrant = new Entrant("account20", Entrant.EntrantStatus.invited);
        assertTrue(testEvent.hasEntrant(hasEntrant));
        assertFalse(testEvent.hasEntrant(noEntrant));
    }

    @Test
    public void testSetEntrants() {
        ArrayList<Entrant> newEntrants = new ArrayList<>();
        testEvent.setEntrants(newEntrants);
        assertEquals(newEntrants, testEvent.getEntrants());
    }

    @Test
    public void testAddEntrant() {
        Entrant addEntrant = new Entrant("account20", Entrant.EntrantStatus.invited);
        assertEquals(4, testEvent.getEntrants().size());
        assertFalse(testEvent.hasEntrant(addEntrant));
        testEvent.addEntrant(addEntrant);
        assertTrue(testEvent.hasEntrant(addEntrant));
        assertEquals(5, testEvent.getEntrants().size());
    }

    @Test
    public void testRemoveEntrant() {
        Entrant reEntrant = new Entrant("account2", Entrant.EntrantStatus.waitlisted);
        assertEquals(4, testEvent.getEntrants().size());
        assertTrue(testEvent.hasEntrant(reEntrant));
        testEvent.removeEntrant(reEntrant);
        assertFalse(testEvent.hasEntrant(reEntrant));
        assertEquals(3, testEvent.getEntrants().size());
    }

    @Test
    public void testGetLocation() {
        assertEquals("50th Street", testEvent.getLocation());
    }

    @Test
    public void testSetLocation() {
        testEvent.setLocation("new Street");
        assertEquals("new Street", testEvent.getLocation());
    }

    @Test
    public void testGetDate() {
        assertEquals("Oct 30th, 2024", testEvent.getDate());
    }

    @Test
    public void testSetDate() {
        testEvent.setDate("Oct 31st, 2024");
        assertEquals("Oct 31st, 2024", testEvent.getDate());
    }

    @Test
    public void testGetTime() {
        assertEquals("3am-6am", testEvent.getTime());
    }

    @Test
    public void testSetTime() {
        testEvent.setTime("12pm");
        assertEquals("12pm", testEvent.getTime());
    }

    @Test
    public void testGetDescription() {
        assertEquals("Some description", testEvent.getDescription());
    }

    @Test
    public void testSetDescription() {
        testEvent.setDescription("Some new description");
        assertEquals("Some new description", testEvent.getDescription());
    }

    @Test
    public void testGetCost() {
        assertEquals("$20", testEvent.getCost());
    }

    @Test
    public void testSetCost() {
        testEvent.setCost("$30");
        assertEquals("$30", testEvent.getCost());
    }

    @Test
    public void testGetPoster() {
        assertEquals("https//poster", testEvent.getEventPoster());
    }

    @Test
    public void testSetPoster() {
        testEvent.setEventPoster("https//new");
        assertEquals("https//new", testEvent.getEventPoster());
    }

    @Test
    public void testGetEventLimit() {
        assertEquals(10, testEvent.getEventLimitCount());
    }

    @Test
    public void testSetEventLimit() {
        // TODO: Test for negative limit
        testEvent.setEventLimitCount(5);
        assertEquals(5, testEvent.getEventLimitCount());
    }

    @Test
    public void testGetWinnerCount() {
        assertEquals(3, testEvent.getEventWinnersCount());
    }

    @Test
    public void testSetWinnerCount() {
        // TODO: Test against negative arguments when we implement checks to keep this value positive
        testEvent.setEventWinnersCount(9);
        assertEquals(9, testEvent.getEventWinnersCount());
    }

    @Test
    public void testGetGeolocation() {
        assertEquals(true, testEvent.getGeolocation());
    }

    @Test
    public void testSetGeolocation() {
        testEvent.setGeolocation(false);
        assertEquals(false, testEvent.getGeolocation());
    }

    @Test
    public void testGetEventStatus() {
        assertEquals("cancelled", testEvent.getEventStatus().value);
    }

    @Test
    public void testSetEventStatus() {
        testEvent.setEventStatus(Event.EventStatus.ongoing);
        assertEquals("ongoing", testEvent.getEventStatus().value);
    }

    @Test
    public void testgeneratingHashdata(){
        testEvent.tryGenerateHashcode();
        int validhash = testEvent.getEventID().hashCode();
        assertEquals(testEvent.getHashcode(), validhash);
    }

    @Test
    public void testValidatingHashdata(){
        assertFalse(testEvent.isValidHashcode());
        testEvent.tryGenerateHashcode();
        assertTrue(testEvent.isValidHashcode());
    }


    @Test
    public void testGetCurrentWinners() {
        assertEquals(2, (int) testEvent.getCurrentWinners());
        testEvent.addEntrant(new Entrant("account5", Entrant.EntrantStatus.accepted));

        assertEquals(3, (int) testEvent.getCurrentWinners());
        testEvent.addEntrant(new Entrant("account6", Entrant.EntrantStatus.waitlisted));

        assertEquals(3, (int) testEvent.getCurrentWinners());
        testEvent.addEntrant(new Entrant("account7", Entrant.EntrantStatus.invited));

        assertEquals(4, (int) testEvent.getCurrentWinners());
        testEvent.addEntrant(new Entrant("account8", Entrant.EntrantStatus.cancelled));
        assertEquals(4, (int) testEvent.getCurrentWinners());

        // Remove all the entrants with "invited" or "accepted"
        testEvent.removeEntrant(new Entrant("account5", Entrant.EntrantStatus.accepted));
        testEvent.removeEntrant(new Entrant("account7", Entrant.EntrantStatus.invited));
        testEvent.removeEntrant(new Entrant("account1", Entrant.EntrantStatus.invited));
        testEvent.removeEntrant(new Entrant("account4", Entrant.EntrantStatus.accepted));

        assertEquals(0, (int) testEvent.getCurrentWinners());
    }

    @Test
    public void testHasAvailability() {
        ArrayList<Entrant> newEntrants = new ArrayList<>();
        newEntrants.add(new Entrant("account1", Entrant.EntrantStatus.waitlisted));
        newEntrants.add(new Entrant("account2", Entrant.EntrantStatus.waitlisted));
        newEntrants.add(new Entrant("account3", Entrant.EntrantStatus.waitlisted));
        newEntrants.add(new Entrant("account4", Entrant.EntrantStatus.waitlisted));

        testEvent.setEntrants(newEntrants);
        assertTrue(testEvent.hasAvailability());

        newEntrants = new ArrayList<>();
        newEntrants.add(new Entrant("account1", Entrant.EntrantStatus.waitlisted));
        newEntrants.add(new Entrant("account2", Entrant.EntrantStatus.invited));
        newEntrants.add(new Entrant("account3", Entrant.EntrantStatus.invited));
        newEntrants.add(new Entrant("account4", Entrant.EntrantStatus.invited));

        testEvent.setEntrants(newEntrants);
        assertFalse(testEvent.hasAvailability());

        newEntrants = new ArrayList<>();
        newEntrants.add(new Entrant("account1", Entrant.EntrantStatus.waitlisted));
        newEntrants.add(new Entrant("account2", Entrant.EntrantStatus.invited));
        newEntrants.add(new Entrant("account3", Entrant.EntrantStatus.cancelled));
        newEntrants.add(new Entrant("account4", Entrant.EntrantStatus.invited));

        testEvent.setEntrants(newEntrants);
        assertTrue(testEvent.hasAvailability());

        newEntrants = new ArrayList<>();
        newEntrants.add(new Entrant("account1", Entrant.EntrantStatus.waitlisted));
        newEntrants.add(new Entrant("account2", Entrant.EntrantStatus.cancelled));
        newEntrants.add(new Entrant("account3", Entrant.EntrantStatus.cancelled));
        newEntrants.add(new Entrant("account4", Entrant.EntrantStatus.invited));

        testEvent.setEntrants(newEntrants);
        assertTrue(testEvent.hasAvailability());

        newEntrants = new ArrayList<>();
        newEntrants.add(new Entrant("account1", Entrant.EntrantStatus.invited));
        newEntrants.add(new Entrant("account2", Entrant.EntrantStatus.cancelled));
        newEntrants.add(new Entrant("account3", Entrant.EntrantStatus.cancelled));
        newEntrants.add(new Entrant("account4", Entrant.EntrantStatus.cancelled));

        testEvent.setEntrants(newEntrants);
        assertTrue(testEvent.hasAvailability());

        newEntrants = new ArrayList<>();
        newEntrants.add(new Entrant("account1", Entrant.EntrantStatus.cancelled));
        newEntrants.add(new Entrant("account2", Entrant.EntrantStatus.cancelled));
        newEntrants.add(new Entrant("account3", Entrant.EntrantStatus.cancelled));
        newEntrants.add(new Entrant("account4", Entrant.EntrantStatus.cancelled));

        testEvent.setEntrants(newEntrants);
        assertTrue(testEvent.hasAvailability());
    }

    @Test
    public void testGetNumberWaitlisted() {
        assertEquals(1, (int) testEvent.getNumberWaitlisted());

        testEvent.addEntrant(new Entrant("account5", Entrant.EntrantStatus.waitlisted));
        assertEquals(2, (int) testEvent.getNumberWaitlisted());

        testEvent.removeEntrant(new Entrant("account5", Entrant.EntrantStatus.waitlisted));
        assertEquals(1, (int) testEvent.getNumberWaitlisted());

        testEvent.removeEntrant(new Entrant("account5", Entrant.EntrantStatus.invited));
        assertEquals(1, (int) testEvent.getNumberWaitlisted());
        testEvent.removeEntrant(new Entrant("account6", Entrant.EntrantStatus.cancelled));
        assertEquals(1, (int) testEvent.getNumberWaitlisted());
        testEvent.removeEntrant(new Entrant("account7", Entrant.EntrantStatus.accepted));
        assertEquals(1, (int) testEvent.getNumberWaitlisted());
    }
}
