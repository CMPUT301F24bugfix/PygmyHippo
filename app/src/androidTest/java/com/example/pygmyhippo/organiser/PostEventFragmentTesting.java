package com.example.pygmyhippo.organiser;

import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.LargeTest;

import org.junit.runner.RunWith;

/**
 * Testing class for my postEventFragment
 * TODO:
 *  - More verbose testing
 *  - Test if added to database
 *  - could read the toast (although using the toast should only be a temporary solution)
 *  - once database is connected check that qr code links
 * @author Griffin
 * @version 1.1
 */
@RunWith(AndroidJUnit4.class)
@LargeTest
public class PostEventFragmentTesting {
//    private FragmentScenario<AllEventsFragment> scenario;
//
//    @Rule
//    public GrantPermissionRule mRuntimePermissionRule = GrantPermissionRule.grant(Manifest.permission.POST_NOTIFICATIONS);
//
//    @Before
//    public void setup() {
//        scenario = FragmentScenario.launchInContainer(AllEventsFragment.class);
//    }
//
//    @Test
//    public void testNoData(){
//        onView(withId(R.id.o_postEvent_bottom_scroll)).perform(scrollTo());
//        onView(withId(R.id.o_postEvent_post_button)).perform(click());
//    }
//
//    @Test
//    // ensure that you are running in organiser mode
//    public void testWithData() {
//        // tests entering name
//        String eventName = "Hippo Party";
//        onView(withId(R.id.o_postEvent_name_edit)).perform(ViewActions.typeText(eventName));
//        onView(withId(R.id.o_postEvent_name_edit)).perform(ViewActions.closeSoftKeyboard());
//        onView(withText(eventName)).check(matches(isDisplayed()));
//        // tests entering date
//        String eventDateTime = "Tomorrow";
//        onView(withId(R.id.o_postEvent_dataTime_edit)).perform(ViewActions.typeText(eventDateTime));
//        onView(withId(R.id.o_postEvent_dataTime_edit)).perform(ViewActions.closeSoftKeyboard());
//        onView(withText(eventDateTime)).check(matches(isDisplayed()));
//        // tests entering price
//        String eventPrice = "Free";
//        onView(withId(R.id.o_postEvent_price_edit)).perform(ViewActions.typeText(eventPrice));
//        onView(withId(R.id.o_postEvent_price_edit)).perform(ViewActions.closeSoftKeyboard());
//        onView(withId(R.id.o_postEvent_price_edit)).check(matches(withText(eventPrice)));
//        // tests entering location
//        String eventLocation = "Edmonton";
//        onView(withId(R.id.o_postEvent_location_edit)).perform(ViewActions.typeText(eventLocation));
//        onView(withId(R.id.o_postEvent_location_edit)).perform(ViewActions.closeSoftKeyboard());
//        onView(withId(R.id.o_postEvent_location_edit)).check(matches(withText(eventLocation)));
//        // tests entering description
//        String eventDescription = "Hippo Party! Join us for a party!";
//        onView(withId(R.id.o_postEvent_description_edit)).perform(ViewActions.typeText(eventDescription));
//        onView(withId(R.id.o_postEvent_description_edit)).perform(ViewActions.closeSoftKeyboard());
//        onView(withId(R.id.o_postEvent_description_edit)).check(matches(withText(eventDescription)));
//        // tests entering event limit
//        String eventLimit = "1200";
//        onView(withId(R.id.o_postEvent_bottom_scroll)).perform(scrollTo());
//        onView(withId(R.id.o_postEvent_limit_edit)).perform(ViewActions.typeText(eventLimit));
//        onView(withId(R.id.o_postEvent_limit_edit)).perform(ViewActions.closeSoftKeyboard());
//        onView(withId(R.id.o_postEvent_limit_edit)).check(matches(withText(eventLimit)));
//        // tests entering number of winners
//        String eventWinners = "200";
//        onView(withId(R.id.o_postEvent_winners_edit)).perform(ViewActions.typeText(eventWinners));
//        onView(withId(R.id.o_postEvent_winners_edit)).perform(ViewActions.closeSoftKeyboard());
//        onView(withId(R.id.o_postEvent_winners_edit)).check(matches(withText(eventWinners)));
//        // tests entering enables geolocation
//        onView(withId(R.id.o_postEvent_geolocation_check)).perform(click());
//        onView(withId(R.id.o_postEvent_geolocation_check)).check(matches(isChecked()));
//
//        onView(withId(R.id.o_postEvent_post_button)).perform(click());
//
//        // checks that qr code is visible
//        onView(withId(R.id.o_eventqr_view)).check(matches(isDisplayed()));
//    }
}
