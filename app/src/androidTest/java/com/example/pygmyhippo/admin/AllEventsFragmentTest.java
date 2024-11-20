package com.example.pygmyhippo.admin;

import static androidx.test.espresso.Espresso.onData;
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.core.AllOf.allOf;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsInstanceOf.instanceOf;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.test.espresso.assertion.ViewAssertions;
import androidx.test.espresso.contrib.RecyclerViewActions;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.LargeTest;
import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.rule.GrantPermissionRule;

import com.example.pygmyhippo.MainActivity;
import com.example.pygmyhippo.R;
import com.example.pygmyhippo.common.Account;

import org.junit.After;
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
    public static Intent createIntent() {
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_MAIN);
        intent.setClassName("com.example.pygmyhippo", "com.example.pygmyhippo.MainActivity");
        intent.addCategory(Intent.CATEGORY_LAUNCHER);
        intent.putExtra("currentRole", "admin");

        Account account = new Account();
        account.setAccountID("0");
        account.setName("Testing account");
        account.setCurrentRole(Account.AccountRole.user);
        intent.putExtra("signedInAccount", account);

        return intent;
    }

    @Rule
    public ActivityScenarioRule<MainActivity> scenario = new ActivityScenarioRule<>(createIntent());

    @Rule
    public GrantPermissionRule mRuntimePermissionRule = GrantPermissionRule.grant(Manifest.permission.POST_NOTIFICATIONS);

    @Before
    public void setup() {
        scenario.getScenario().onActivity(activity -> {
            NavController navcontroller = Navigation.findNavController(activity, R.id.nav_host_fragment_activity_main);
            Bundle navArgs = new Bundle();
            Account account = new Account();
            account.setAccountID("0");
            account.setName("Testing account");
            account.setCurrentRole(Account.AccountRole.user);
            navArgs.putParcelable("signedInAccount", account);
            navArgs.putBoolean("useFirebase", false);
            navArgs.putBoolean("useNavigation", false);
            navcontroller.navigate(R.id.admin_navigation_all_events, navArgs);
        });
    }

    @After
    public void cleanup() {

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
