package com.example.pygmyhippo.Organiser;

/**
*This Class is used to test the list filtering methods used in ViewEntrantsFragment
*Author: Kori
 */

import static org.junit.Assert.assertEquals;

import com.example.pygmyhippo.common.Entrant;
import com.example.pygmyhippo.common.Event;
import com.example.pygmyhippo.organizer.ViewEntrantsFragment;

import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;

public class ViewEntrantsFragmentTest {
    private Event testEvent;
    private ViewEntrantsFragment viewEntrantsFragment;

    @Before
    public void setUp() {
        // Make an instance of the fragment
        viewEntrantsFragment = new ViewEntrantsFragment();

        // Make an example Event object
        testEvent = new Event(
                "Hippo Party",
                "event1",
                "The Hippopotamus Society",
                new ArrayList<>(),
                "The Swamp",
                "2024-10-31",
                "4:00 PM MST - 4:00 AM MST",
                "Love hippos and a party? Love a party! Join a party! We have lots of really cool hippos I'm sure you'd love to meet! There will be food, games, and all sorts of activities you could imagine! It's almost worth the price to see Moo Deng and his buddies!",
                "$150.00",
                "hippoparty.png",
                Event.EventStatus.cancelled,
                true);
        testEvent.setEventWinnersCount(20);

    }

    @Test
    public void onlyWaitlistTest() {
        testEvent.setEventStatus(Event.EventStatus.ongoing);

        // Fill the event with just waitlisted for now
        for (int id = 0 ; id < 150 ; id++) {
            String accountID = "account" + id;
            Entrant newEntrant = new Entrant(accountID, Entrant.EntrantStatus.waitlisted);
            testEvent.addEntrant(newEntrant);
        }

        // Test when only entrants are the waitlisted ones
        ArrayList<Entrant> filterList = new ArrayList<Entrant>();
        filterList = viewEntrantsFragment.setEntrantWaitList(testEvent.getEntrants(), testEvent.getEventStatus().value);
        assertEquals(150, filterList.size());
        // Check that they are all actually waitlisted
        for (int id = 0 ; id < 150 ; id++) {
            assertEquals("waitlisted", filterList.get(id).getEntrantStatus().value);
        }

        filterList = viewEntrantsFragment.setEntrantInvited(testEvent.getEntrants());
        assertEquals(0, filterList.size());

        filterList = viewEntrantsFragment.setEntrantCancelled(testEvent.getEntrants());
        assertEquals(0, filterList.size());

        filterList = viewEntrantsFragment.setEntrantAccepted(testEvent.getEntrants());
        assertEquals(0, filterList.size());
    }

    @Test
    public void onlyInvitedTest() {
        // Fill the event with just waitlisted for now
        for (int id = 0 ; id < 100 ; id++) {
            String accountID = "account" + id;
            Entrant newEntrant = new Entrant(accountID, Entrant.EntrantStatus.invited);
            testEvent.addEntrant(newEntrant);
        }

        // Test when only entrants are the waitlisted ones
        ArrayList<Entrant> filterList = new ArrayList<Entrant>();
        filterList = viewEntrantsFragment.setEntrantWaitList(testEvent.getEntrants(), testEvent.getEventStatus().value);
        assertEquals(0, filterList.size());

        filterList = viewEntrantsFragment.setEntrantInvited(testEvent.getEntrants());
        assertEquals(100, filterList.size());
        // Check that they are all actually Invited
        for (int id = 0 ; id < 100 ; id++) {
            assertEquals("invited", filterList.get(id).getEntrantStatus().value);
        }

        filterList = viewEntrantsFragment.setEntrantCancelled(testEvent.getEntrants());
        assertEquals(0, filterList.size());

        filterList = viewEntrantsFragment.setEntrantAccepted(testEvent.getEntrants());
        assertEquals(0, filterList.size());
    }

    @Test
    public void onlyCancelledTest() {
        // Fill the event with just waitlisted for now
        for (int id = 0 ; id < 200 ; id++) {
            String accountID = "account" + id;
            Entrant newEntrant = new Entrant(accountID, Entrant.EntrantStatus.cancelled);
            testEvent.addEntrant(newEntrant);
        }

        // Test when only entrants are the waitlisted ones
        ArrayList<Entrant> filterList = new ArrayList<Entrant>();
        filterList = viewEntrantsFragment.setEntrantWaitList(testEvent.getEntrants(), testEvent.getEventStatus().value);
        assertEquals(0, filterList.size());

        filterList = viewEntrantsFragment.setEntrantInvited(testEvent.getEntrants());
        assertEquals(0, filterList.size());

        filterList = viewEntrantsFragment.setEntrantCancelled(testEvent.getEntrants());
        assertEquals(200, filterList.size());
        // Check that they are all actually Cancelled
        for (int id = 0 ; id < 200 ; id++) {
            assertEquals("cancelled", filterList.get(id).getEntrantStatus().value);
        }

        filterList = viewEntrantsFragment.setEntrantAccepted(testEvent.getEntrants());
        assertEquals(0, filterList.size());
    }

    @Test
    public void onlyAcceptedTest() {
        // Fill the event with just waitlisted for now
        for (int id = 0 ; id < 180 ; id++) {
            String accountID = "account" + id;
            Entrant newEntrant = new Entrant(accountID, Entrant.EntrantStatus.accepted);
            testEvent.addEntrant(newEntrant);
        }

        // Test when only entrants are the waitlisted ones
        ArrayList<Entrant> filterList = new ArrayList<Entrant>();
        filterList = viewEntrantsFragment.setEntrantWaitList(testEvent.getEntrants(), testEvent.getEventStatus().value);
        assertEquals(0, filterList.size());

        filterList = viewEntrantsFragment.setEntrantInvited(testEvent.getEntrants());
        assertEquals(0, filterList.size());

        filterList = viewEntrantsFragment.setEntrantCancelled(testEvent.getEntrants());
        assertEquals(0, filterList.size());

        filterList = viewEntrantsFragment.setEntrantAccepted(testEvent.getEntrants());
        assertEquals(180, filterList.size());
        // Check that they are all actually accepted
        for (int id = 0 ; id < 100 ; id++) {
            assertEquals("accepted", filterList.get(id).getEntrantStatus().value);
        }
    }

    @Test
    public void MixedStatusesTest() {
        // Simulate mixed list once event is closed
        testEvent.setEventStatus(Event.EventStatus.cancelled);
        int id = 0;
        for (id = 0 ; id < 5 ; id++) {
            String accountID = "account" + id;
            Entrant newEntrant = new Entrant(accountID, Entrant.EntrantStatus.lost);
            testEvent.addEntrant(newEntrant);
        }
        for ( ; id < 11 ; id++) {
            String accountID = "account" + id;
            Entrant newEntrant = new Entrant(accountID, Entrant.EntrantStatus.invited);
            testEvent.addEntrant(newEntrant);
        }
        for ( ; id < 18 ; id++) {
            String accountID = "account" + id;
            Entrant newEntrant = new Entrant(accountID, Entrant.EntrantStatus.cancelled);
            testEvent.addEntrant(newEntrant);
        }
        for ( ; id < 26 ; id++) {
            String accountID = "account" + id;
            Entrant newEntrant = new Entrant(accountID, Entrant.EntrantStatus.accepted);
            testEvent.addEntrant(newEntrant);
        }

        // Throw some rejected in there, they shouldn't show up in any of the filters
        for ( ; id < 30 ; id++) {
            String accountID = "account" + id;
            Entrant newEntrant = new Entrant(accountID, Entrant.EntrantStatus.rejected);
            testEvent.addEntrant(newEntrant);
        }

        // Test when only entrants are the lost ones
        ArrayList<Entrant> filterList = new ArrayList<Entrant>();
        filterList = viewEntrantsFragment.setEntrantWaitList(testEvent.getEntrants(), testEvent.getEventStatus().value);
        assertEquals(5, filterList.size());

        // Check that they are all actually lost
        for (id = 0 ; id < 5 ; id++) {
            assertEquals("lost", filterList.get(id).getEntrantStatus().value);
        }

        filterList = viewEntrantsFragment.setEntrantInvited(testEvent.getEntrants());
        assertEquals(6, filterList.size());

        // Check that they are all actually Invited
        for (id = 0 ; id < 6 ; id++) {
            assertEquals("invited", filterList.get(id).getEntrantStatus().value);
        }

        filterList = viewEntrantsFragment.setEntrantCancelled(testEvent.getEntrants());
        assertEquals(7, filterList.size());

        // Check that they are all actually cancelled
        for (id = 0 ; id < 7 ; id++) {
            assertEquals("cancelled", filterList.get(id).getEntrantStatus().value);
        }

        filterList = viewEntrantsFragment.setEntrantAccepted(testEvent.getEntrants());
        assertEquals(8, filterList.size());
        // Check that they are all actually accepted
        for (id = 0 ; id < 8 ; id++) {
            assertEquals("accepted", filterList.get(id).getEntrantStatus().value);
        }
    }

    @Test
    public void testWaitlistOnClosedEvent() {
        testEvent.setEventStatus(Event.EventStatus.cancelled);

        // Fill the event with just lost for now
        for (int id = 0 ; id < 150 ; id++) {
            String accountID = "account" + id;
            Entrant newEntrant = new Entrant(accountID, Entrant.EntrantStatus.lost);
            testEvent.addEntrant(newEntrant);
        }

        // Test when only entrants should be in the waitlist filter (have lost status)
        ArrayList<Entrant> filterList = new ArrayList<Entrant>();
        filterList = viewEntrantsFragment.setEntrantWaitList(testEvent.getEntrants(), testEvent.getEventStatus().value);
        assertEquals(150, filterList.size());
        // Check that they are all actually lost
        for (int id = 0 ; id < 150 ; id++) {
            assertEquals("lost", filterList.get(id).getEntrantStatus().value);
        }

        filterList = viewEntrantsFragment.setEntrantInvited(testEvent.getEntrants());
        assertEquals(0, filterList.size());

        filterList = viewEntrantsFragment.setEntrantCancelled(testEvent.getEntrants());
        assertEquals(0, filterList.size());

        filterList = viewEntrantsFragment.setEntrantAccepted(testEvent.getEntrants());
        assertEquals(0, filterList.size());
    }
}
