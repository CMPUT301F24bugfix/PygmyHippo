package com.example.pygmyhippo.Organiser;
/*
The Unit tests for the organizer.EventFragment
Purposes:
    - To test the lottery draw in this fragment
Issues: None
 */

import static org.junit.Assert.assertEquals;

import com.example.pygmyhippo.common.Entrant;
import com.example.pygmyhippo.common.Event;
import com.example.pygmyhippo.organizer.EventFragment;

import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;

public class EventFragmentTest {
    private Event testEvent;
    private EventFragment eventFragment;

    @Before
    public void setUp() {
        // Make an instance of the fragment
        eventFragment = new EventFragment();

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
                Event.EventStatus.ongoing,
                true);
        testEvent.setEventWinnersCount(20);

        // Make an entrant array to add to the event
        for (int id = 0 ; id < 1000 ; id++) {
            String accountID = "account" + id;
            Entrant newEntrant = new Entrant(accountID, Entrant.EntrantStatus.waitlisted);
            testEvent.addEntrant(newEntrant);
        }

    }

    @Test
    public void testDrawWinnersListSize() {
        // Draw the winners
        eventFragment.drawWinners(testEvent);

        // Test that the list size stays the same
        assertEquals(1000, testEvent.getEntrants().size());
    }

    @Test
    public void testWinnerCount() {
        // Draw the winners
        eventFragment.drawWinners(testEvent);

        // This test is for checking that the right amount of entrants have invited statuses
        int invitedCount = 0;
        int waitlistCount = 0;

        for (int id = 0 ; id < 1000 ; id++) {
            if (testEvent.getEntrants().get(id).getEntrantStatus().value.equals("invited")) {
                invitedCount++;
            } else if (testEvent.getEntrants().get(id).getEntrantStatus().value.equals("waitlisted")) {
                waitlistCount++;
            }
        }
        assertEquals(20, invitedCount);
        assertEquals(980, waitlistCount);
    }

    @Test
    public void testFullWaitlist() {
        // This test is to check that if the waitlist is the same size as the lottery draw, then they
        // are all still updated

        // Draw the winners
        testEvent.setEventWinnersCount(1000);
        eventFragment.drawWinners(testEvent);

        int invitedCount = 0;
        int waitlistCount = 0;

        for (int id = 0 ; id < 1000 ; id++) {
            if (testEvent.getEntrants().get(id).getEntrantStatus().value.equals("invited")) {
                invitedCount++;
            } else if (testEvent.getEntrants().get(id).getEntrantStatus().value.equals("waitlisted")) {
                waitlistCount++;
            }
        }

        assertEquals(1000, invitedCount);
        assertEquals(0, waitlistCount);
    }

    @Test
    public void smallerWaitlist() {
        // Same as above but the waitlist is smaller than the winners count

        // Draw the winners
        testEvent.setEventWinnersCount(1200);
        eventFragment.drawWinners(testEvent);

        int invitedCount = 0;
        int waitlistCount = 0;

        for (int id = 0 ; id < 1000 ; id++) {
            if (testEvent.getEntrants().get(id).getEntrantStatus().value.equals("invited")) {
                invitedCount++;
            } else if (testEvent.getEntrants().get(id).getEntrantStatus().value.equals("waitlisted")) {
                waitlistCount++;
            }
        }

        assertEquals(1000, invitedCount);
        assertEquals(0, waitlistCount);
    }

    @Test
    public void testOnEmptyWaitlist() {
        // This test is for case that the waitlist is empty
        testEvent.setEntrants(new ArrayList<>());

        // Draw the winners
        eventFragment.drawWinners(testEvent);

        // Basically assert that nothing should happen
        assertEquals(0, testEvent.getEntrants().size());
    }

    @Test
    public void testSetAllLosers() {
        eventFragment.setLoserStatuses(testEvent);
        int loserCount = 0;
        for (int id = 0 ; id < 1000 ; id++) {
            if (testEvent.getEntrants().get(id).getEntrantStatus().value.equals("lost")) {
                loserCount++;
            }
        }

        assertEquals(1000, loserCount);
    }

    @Test
    public void testSetLosersAfterDraw() {
        eventFragment.drawWinners(testEvent);
        eventFragment.setLoserStatuses(testEvent);
        int loserCount = 0;
        int invitedCount = 0;
        for (int id = 0 ; id < 1000 ; id++) {
            if (testEvent.getEntrants().get(id).getEntrantStatus().value.equals("lost")) {
                loserCount++;
            } else if (testEvent.getEntrants().get(id).getEntrantStatus().value.equals("invited")) {
                invitedCount++;
            }
        }

        assertEquals(980, loserCount);
        assertEquals(20, invitedCount);
    }

    @Test
    public void testRedraw() {
        // Set some invited
        for (int id = 0 ; id < 5 ; id++) {
            testEvent.getEntrants().get(id).setEntrantStatus(Entrant.EntrantStatus.invited);
        }

        // Set some accepted
        for (int id = 5 ; id < 10 ; id++) {
            testEvent.getEntrants().get(id).setEntrantStatus(Entrant.EntrantStatus.accepted);
        }

        // Set half cancelled
        for (int id = 10 ; id < 20 ; id++) {
            testEvent.getEntrants().get(id).setEntrantStatus(Entrant.EntrantStatus.cancelled);
        }

        // Set some as rejected
        for (int id = 20 ; id < 28 ; id++) {
            testEvent.getEntrants().get(id).setEntrantStatus(Entrant.EntrantStatus.rejected);
        }
        // set the waitlisted to lost and event status to cancelled
        testEvent.setEventStatus(Event.EventStatus.cancelled);
        eventFragment.setLoserStatuses(testEvent);

        // Do the redraw
        eventFragment.drawWinners(testEvent);

        int invitedCount = 0;
        int lostCount = 0;
        int cancelledCount = 0;
        int acceptedCount = 0;
        int waitlistedCount = 0;
        int rejectedCount = 0;
        for (int id = 0 ; id < 1000 ; id++) {
            if (testEvent.getEntrants().get(id).getEntrantStatus().value.equals("invited")) {
                invitedCount++;
            } else if (testEvent.getEntrants().get(id).getEntrantStatus().value.equals("lost")) {
                lostCount++;
            } else if (testEvent.getEntrants().get(id).getEntrantStatus().value.equals("cancelled")) {
                cancelledCount++;
            } else if (testEvent.getEntrants().get(id).getEntrantStatus().value.equals("accepted")) {
                acceptedCount++;
            } else if (testEvent.getEntrants().get(id).getEntrantStatus().value.equals("waitlisted")) {
                waitlistedCount++;
            } else if (testEvent.getEntrants().get(id).getEntrantStatus().value.equals("rejected")) {
                rejectedCount++;
            }
        }

        assertEquals(15, invitedCount);
        assertEquals(5, acceptedCount);
        assertEquals(10, cancelledCount);
        assertEquals(962 , lostCount);
        assertEquals(8 , rejectedCount);
        assertEquals(0, waitlistedCount);
        assertEquals(1000, testEvent.getEntrants().size());
    }
}
