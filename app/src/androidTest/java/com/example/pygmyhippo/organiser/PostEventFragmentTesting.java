package com.example.pygmyhippo.organiser;
import com.example.pygmyhippo.R;
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.typeTextIntoFocusedView;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.action.ViewActions.scrollTo;
import static androidx.test.espresso.matcher.ViewMatchers.isChecked;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;

import static androidx.test.espresso.action.ViewActions.click;

import static androidx.test.espresso.assertion.ViewAssertions.matches;

import static androidx.test.espresso.matcher.ViewMatchers.withText;

import android.net.Uri;

import android.Manifest;
import androidx.test.rule.GrantPermissionRule;

import androidx.test.core.app.ApplicationProvider;
import androidx.test.espresso.action.ViewActions;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.LargeTest;

import androidx.test.uiautomator.By;
import androidx.test.uiautomator.UiDevice;
import androidx.test.uiautomator.UiObject;
import androidx.test.uiautomator.UiObjectNotFoundException;
import androidx.test.uiautomator.UiSelector;
import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.uiautomator.Until;

import com.example.pygmyhippo.MainActivity;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * Testing class for my postEventFragment
 * TODO:
 *  - More verbose testing
 *  - Test if added to database
 *  - could read the toast (although using the toast should only be a temporary solution)
 *  - once database is connected check that qr code links
 * @author Griffin
 * @version 1.0
 */
@RunWith(AndroidJUnit4.class)
@LargeTest
public class PostEventFragmentTesting {

    private UiDevice device;

    @Rule
    public GrantPermissionRule mRuntimePermissionRule = GrantPermissionRule.grant(Manifest.permission.POST_NOTIFICATIONS);

    @Rule
    public ActivityScenarioRule<MainActivity> scenario = new ActivityScenarioRule<MainActivity>(MainActivity.class);

    @Before
    public void setUp() {
        onView(withId(R.id.organiser_postEvent_page)).perform(click());
        device = UiDevice.getInstance(InstrumentationRegistry.getInstrumentation());
    }

    @Test
    public void testNoData(){
        onView(withId(R.id.o_postEvent_bottom_scroll)).perform(scrollTo());
        onView(withId(R.id.o_postEvent_post_button)).perform(click());
    }

    @Test
    // ensure that you are running in organiser mode
    public void testWithData() {
        // tests entering name
        String eventName = "Hippo Party";
        onView(withId(R.id.o_postEvent_name_edit)).perform(ViewActions.typeText(eventName));
        onView(withId(R.id.o_postEvent_name_edit)).perform(ViewActions.closeSoftKeyboard());
        onView(withText(eventName)).check(matches(isDisplayed()));
        // tests entering date
        String eventDateTime = "Tomorrow";
        onView(withId(R.id.o_postEvent_dataTime_edit)).perform(ViewActions.typeText(eventDateTime));
        onView(withId(R.id.o_postEvent_dataTime_edit)).perform(ViewActions.closeSoftKeyboard());
        onView(withText(eventDateTime)).check(matches(isDisplayed()));
        // tests entering price
        String eventPrice = "Free";
        onView(withId(R.id.o_postEvent_price_edit)).perform(ViewActions.typeText(eventPrice));
        onView(withId(R.id.o_postEvent_price_edit)).perform(ViewActions.closeSoftKeyboard());
        onView(withId(R.id.o_postEvent_price_edit)).check(matches(withText(eventPrice)));
        // tests entering location
        String eventLocation = "Edmonton";
        onView(withId(R.id.o_postEvent_location_edit)).perform(ViewActions.typeText(eventLocation));
        onView(withId(R.id.o_postEvent_location_edit)).perform(ViewActions.closeSoftKeyboard());
        onView(withId(R.id.o_postEvent_location_edit)).check(matches(withText(eventLocation)));
        // tests entering description
        String eventDescription = "Hippo Party! Join us for a party!";
        onView(withId(R.id.o_postEvent_description_edit)).perform(ViewActions.typeText(eventDescription));
        onView(withId(R.id.o_postEvent_description_edit)).perform(ViewActions.closeSoftKeyboard());
        onView(withId(R.id.o_postEvent_description_edit)).check(matches(withText(eventDescription)));
        // tests entering event limit
        String eventLimit = "1200";
        onView(withId(R.id.o_postEvent_bottom_scroll)).perform(scrollTo());
        onView(withId(R.id.o_postEvent_limit_edit)).perform(ViewActions.typeText(eventLimit));
        onView(withId(R.id.o_postEvent_limit_edit)).perform(ViewActions.closeSoftKeyboard());
        onView(withId(R.id.o_postEvent_limit_edit)).check(matches(withText(eventLimit)));
        // tests entering number of winners
        String eventWinners = "200";
        onView(withId(R.id.o_postEvent_winners_edit)).perform(ViewActions.typeText(eventWinners));
        onView(withId(R.id.o_postEvent_winners_edit)).perform(ViewActions.closeSoftKeyboard());
        onView(withId(R.id.o_postEvent_winners_edit)).check(matches(withText(eventWinners)));
        // tests entering enables geolocation
        onView(withId(R.id.o_postEvent_geolocation_check)).perform(click());
        onView(withId(R.id.o_postEvent_geolocation_check)).check(matches(isChecked()));

        onView(withId(R.id.o_postEvent_post_button)).perform(click());

        // checks that qr code is visible
        onView(withId(R.id.o_eventqr_view)).check(matches(isDisplayed()));
    }
}
