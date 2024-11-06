package com.example.pygmyhippo.Organiser;


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
                // TODO: there is a bit of an issue with aligning the time when it is shorter on the xml
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
}
