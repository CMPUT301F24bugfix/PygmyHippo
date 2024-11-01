package com.example.pygmyhippo.admin;

import static androidx.test.espresso.Espresso.onData;
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.Matchers.is;

import android.Manifest;
import android.content.Context;

import androidx.fragment.app.testing.FragmentScenario;
import androidx.test.espresso.assertion.ViewAssertions;
import androidx.test.espresso.contrib.RecyclerViewActions;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.LargeTest;
import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.rule.GrantPermissionRule;

import com.example.pygmyhippo.R;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * Testing for AllEventsFragment.
 *
 * Currently only testing spinners selection and ability to click on first item of list.
 */
@RunWith(AndroidJUnit4.class)
@LargeTest
public class AllEventsFragmentTest {
    private FragmentScenario<AllEventsFragment> scenario;

    @Rule
    public GrantPermissionRule mRuntimePermissionRule = GrantPermissionRule.grant(Manifest.permission.POST_NOTIFICATIONS);


    @Before
    public void setup() {
        scenario = FragmentScenario.launchInContainer(AllEventsFragment.class);
    }

    @Test
    public void testEventCategorySpinner() {


        final Context context = InstrumentationRegistry.getInstrumentation().getTargetContext();
        final String[] userCategories = context.getResources().getStringArray(R.array.all_events_category_spinner);

        for (String category : userCategories) {
            onView(withId(R.id.a_alllist_category_spinner)).perform(click());
            onData(allOf(is(instanceOf(CharSequence.class)), is(category))).perform(click());
            onView(withText(category)).check(ViewAssertions.matches(isDisplayed()));
        }
    }

    @Test
    public void testOrderSpinner() {
        final Context context = InstrumentationRegistry.getInstrumentation().getTargetContext();
        final String[] spinnerOrders = context.getResources().getStringArray(R.array.order_spinner);

        for (String order : spinnerOrders) {
            onView(withId(R.id.a_alllist_order_spinner)).perform(click());
            onData(allOf(is(instanceOf(CharSequence.class)), is(order))).perform(click());
            onView(withText(order)).check(ViewAssertions.matches(isDisplayed()));
        }
    }

    @Test
    public void testAllEventList() {
        // TODO: Add more thorough testing for items in all list after navigation is figured out.
        onView(withId(R.id.a_alllist_recycler))
                .perform(RecyclerViewActions.actionOnItemAtPosition(0, click()));
    }
}
