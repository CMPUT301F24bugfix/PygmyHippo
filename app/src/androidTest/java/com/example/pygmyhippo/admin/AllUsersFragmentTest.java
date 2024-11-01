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

import androidx.test.espresso.assertion.ViewAssertions;
import androidx.test.espresso.contrib.RecyclerViewActions;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.LargeTest;
import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.rule.GrantPermissionRule;

import com.example.pygmyhippo.MainActivity;
import com.example.pygmyhippo.R;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * Testing for AllUsersFragment.
 *
 * Currently only testing spinners selection and ability to click on first item of list.
 */
@RunWith(AndroidJUnit4.class)
@LargeTest
public class AllUsersFragmentTest {

    @Rule
    public GrantPermissionRule mRuntimePermissionRule = GrantPermissionRule.grant(Manifest.permission.POST_NOTIFICATIONS);

    @Rule
    public ActivityScenarioRule<MainActivity> scenario = new ActivityScenarioRule<MainActivity>(MainActivity.class);

    @Test
    public void testUserCategorySpinner() {
        final Context context = InstrumentationRegistry.getInstrumentation().getTargetContext();
        final String[] userCategories = context.getResources().getStringArray(R.array.all_users_category_spinner);

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
    public void testAllUserList() {
        // TODO: Add more thorough testing for items in all list after navigation is figured out.
        onView(withId(R.id.a_alllist_recycler))
                .perform(RecyclerViewActions.actionOnItemAtPosition(0, click()));
    }
}