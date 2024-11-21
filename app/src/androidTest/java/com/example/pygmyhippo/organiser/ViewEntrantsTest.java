package com.example.pygmyhippo.organiser;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;


import android.Manifest;

import androidx.fragment.app.testing.FragmentScenario;
import androidx.test.espresso.assertion.ViewAssertions;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.LargeTest;
import androidx.test.rule.GrantPermissionRule;

import com.example.pygmyhippo.R;
import com.example.pygmyhippo.organizer.ViewEntrantsFragment;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * Testing class for ViewEntrantsFragment
 * @author Kori
 * TODO:
 *  - Test array adapter once list gets data
 *  - Test that the right entrants are shown after filtering
 */
@RunWith(AndroidJUnit4.class)
@LargeTest
public class ViewEntrantsTest {
    private FragmentScenario<ViewEntrantsFragment> scenario;

    @Rule
    public GrantPermissionRule mRuntimePermissionRule = GrantPermissionRule.grant(Manifest.permission.POST_NOTIFICATIONS);

    @Before
    public void setup() {
        scenario = FragmentScenario.launchInContainer(ViewEntrantsFragment.class);
    }

    @Test
    public void testEntrantSpinner() {
        // FIXME: Navigation will change
        onView(withId(R.id.button_view_entrants)).perform(click());

        // Click the spinner
        onView(withId(R.id.o_entrant_list_spinner)).perform(click());

        // Check if the drop down is displayed
        String waitlist = "WaitList";
        String invited = "Invited";
        String canceled = "Cancelled";
        String accepted = "Accepted";
        onView(withText(waitlist)).check(ViewAssertions.matches(isDisplayed()));
        onView(withText(invited)).check(ViewAssertions.matches(isDisplayed()));
        onView(withText(canceled)).check(ViewAssertions.matches(isDisplayed()));
        onView(withText(accepted)).check(ViewAssertions.matches(isDisplayed()));

        // Click on invited
        onView(withText(invited)).perform(click());
        // Check if invited is displayed on spinner
        onView(withText(invited)).check(ViewAssertions.matches(isDisplayed()));

        // Do the same for canceled, waitlist, and accepted
        // Click the spinner
        onView(withId(R.id.o_entrant_list_spinner)).perform(click());
        onView(withText(canceled)).perform(click());
        onView(withText(canceled)).check(ViewAssertions.matches(isDisplayed()));

        // For accepted
        onView(withId(R.id.o_entrant_list_spinner)).perform(click());
        onView(withText(accepted)).perform(click());
        onView(withText(accepted)).check(ViewAssertions.matches(isDisplayed()));

        // For Waitlist
        onView(withId(R.id.o_entrant_list_spinner)).perform(click());
        onView(withText(waitlist)).perform(click());
        onView(withText(waitlist)).check(ViewAssertions.matches(isDisplayed()));
    }

}
