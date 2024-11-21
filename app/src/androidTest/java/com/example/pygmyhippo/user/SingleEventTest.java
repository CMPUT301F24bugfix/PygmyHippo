package com.example.pygmyhippo.user;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.matcher.ViewMatchers.withId;

import android.Manifest;
import android.os.Bundle;

import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.rule.GrantPermissionRule;

import com.example.pygmyhippo.MainActivity;
import com.example.pygmyhippo.R;
import com.example.pygmyhippo.common.Account;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

/**
 * Test single event page
 * @author Katharine
 */
public class SingleEventTest {
    @Rule
    public GrantPermissionRule notificationPerm = GrantPermissionRule.grant(Manifest.permission.POST_NOTIFICATIONS);

    @Rule
    public GrantPermissionRule cameraPerm = GrantPermissionRule.grant(Manifest.permission.CAMERA);

    @Rule
    public ActivityScenarioRule<MainActivity> scenario = new ActivityScenarioRule<>(MainActivity.class);

    // click onto the qr code from home
    @Before
    public void setUp() {
        scenario.getScenario().onActivity(activity -> {
            NavController navController = Navigation.findNavController(activity, R.id.nav_host_fragment_activity_main);
            Bundle navArgs = new Bundle();
            Account account = new Account();
            account.setCurrentRole(Account.AccountRole.user);
            account.setName("Test");
            navArgs.putParcelable("signedInAccount", account);
            navArgs.putBoolean("useFirebase", false);
            navArgs.putBoolean("useNavigation", false);
            navArgs.putString("eventID", "0");
            navController.navigate(R.id.u_eventFragment, navArgs);
        });
    }

    @Test
    public void simpleTest() {
        onView(withId(R.id.u_registerButton)).perform(click());
    }

    // navigate to event page
//    @Test
//    public void testNavigateToEventPage() {
//        //onView(withId(R.id.u_scanQRButton)).perform(click());
//        onView(withId(R.id.u_eventNameView)).check(matches(isDisplayed()));
//    }
//
//    // click register
//    @Test
//    public void testRegisterForEvent() {
//        Event event = new Event();
//        Entrant entrant = new Entrant(
//                "123",
//                Entrant.EntrantStatus.invited
//        );
//        //onView(withId(R.id.u_scanQRButton)).perform(click());
//        // check to see that there is nothing in the entrants list
//        assertEquals(0, event.getEntrants().size());
//        onView(withId(R.id.u_registerButton)).perform(click());
//        // check to see if checkmark changes
//        onView(withText("✔")).check(matches(isDisplayed()));
//        // check to see if entrant is in event entrants list
//        event.addEntrant(entrant);
//        assertEquals(1, event.getEntrants().size());
//        // this means the method with hardcoding the entrant into eventfragment should work
//        // it just can't be tested right now due to the nature of creating an instance instead a test
//        // changes made to the fragment won't affect test instance (can only test for functionality)
//    }
//
//    @Test
//    public void testLeaveEvent() {
//        Event event = new Event();
//        Entrant entrant = new Entrant(
//                "123",
//                Entrant.EntrantStatus.invited
//        );
//        //onView(withId(R.id.u_scanQRButton)).perform(click());
//        assertEquals(0, event.getEntrants().size());
//        onView(withId(R.id.u_registerButton)).perform(click());
//        assertEquals(1, event.getEntrants().size());
//        onView(withText("✔")).check(matches(isDisplayed()));
//        event.addEntrant(entrant);
//        event.removeEntrant(entrant);
//        assertEquals(0, event.getEntrants().size());
//    }
//
//    @Test
//    public void testEnabledGeolocation() {
//        ArrayList<Entrant> entrants = new ArrayList<>();
//        Event event = new Event(
//                "Hippo Party",
//                "1",
//                "The Hippopotamus Society",
//                entrants,
//                "The Swamp",
//                "2024-10-31",
//                // TODO: there is a bit of an issue with aligning the time when it is shorter on the xml
//                "4:00 PM MST - 4:00 AM MST",
//                "Love hippos and a party? Love a party! Join a party!",
//                "$150.00",
//                "hippoparty.png",
//                Event.EventStatus.ongoing,
//                true
//        );
//        Entrant entrant = new Entrant(
//                "123",
//                Entrant.EntrantStatus.invited
//        );
//
//       //onView(withId(R.id.u_scanQRButton)).perform(click());
//        onView(withId(R.id.u_registerButton)).perform(click());
//        // TODO: double check to see if this actually works (hamcrest/matchers error)
//        onView(withText("event requires geolocation")).check(matches(isDisplayed()));
//        onView(withId(android.R.id.button1)).perform(click());
//        onView(withText("✔")).check(matches(isDisplayed()));
//    }
}
