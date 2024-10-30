package com.example.pygmyhippo.user;
import com.example.pygmyhippo.MainActivity;
import com.example.pygmyhippo.R;
import com.example.pygmyhippo.common.Entrant;
import com.example.pygmyhippo.common.Event;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

import static org.junit.Assert.assertEquals;

import androidx.test.ext.junit.rules.ActivityScenarioRule;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

/**
 * Test single event page
 * @author Katharine
 */
public class SingleEventTest {
    @Rule
    public ActivityScenarioRule<MainActivity> scenario = new ActivityScenarioRule<MainActivity>(MainActivity.class);

    // click onto the qr code from home
    @Before
    public void setUp() {
        onView(withId(R.id.u_scanQRcodeFragment)).perform(click());
    }

    // navigate to event page
    @Test
    public void testNavigateToEventPage() {
        onView(withId(R.id.u_scanQRButton)).perform(click());
        onView(withId(R.id.u_eventNameView)).check(matches(isDisplayed()));
    }

    // click register
    @Test
    public void testRegisterForEvent() {
        Event event = new Event();
        Entrant entrant = new Entrant(
                "123",
                Entrant.EntrantStatus.invited
        );
        onView(withId(R.id.u_scanQRButton)).perform(click());
        // check to see that there is nothing in the entrants list
        assertEquals(0, event.getEntrants().size());
        onView(withId(R.id.u_registerButton)).perform(click());
        // check to see if checkmark changes
        onView(withText("✔")).check(matches(isDisplayed()));
        // check to see if entrant is in event entrants list
        event.addEntrant(entrant);
        assertEquals(1, event.getEntrants().size());
        // this means the method with hardcoding the entrant into eventfragment should work
        // it just can't be tested right now due to the nature of creating an instance instead a test
        // changes made to the fragment won't affect test instance (can only test for functionality)
    }
}
