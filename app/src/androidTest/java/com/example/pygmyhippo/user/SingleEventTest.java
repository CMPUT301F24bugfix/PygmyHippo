package com.example.pygmyhippo.user;

/**
 * Test single event page
 * @author Katharine
 */
public class SingleEventTest {
//    @Rule
//    public GrantPermissionRule mRuntimePermissionRule = GrantPermissionRule.grant(Manifest.permission.POST_NOTIFICATIONS);
//
//    @Rule
//    public ActivityScenarioRule<MainActivity> scenario = new ActivityScenarioRule<MainActivity>(MainActivity.class);
//
//    // click onto the qr code from home
//    @Before
//    public void setUp() {
//        onView(withId(R.id.u_scanQR_menu_item)).perform(click());
//    }
//
//    // navigate to event page
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
