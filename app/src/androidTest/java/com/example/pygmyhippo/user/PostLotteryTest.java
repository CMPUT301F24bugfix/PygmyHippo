package com.example.pygmyhippo.user;
import static androidx.test.espresso.Espresso.onData;
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

import static org.hamcrest.CoreMatchers.anything;
import static org.hamcrest.core.AllOf.allOf;
import static org.hamcrest.core.Is.is;

import android.Manifest;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.filters.LargeTest;
import androidx.test.rule.GrantPermissionRule;

import com.example.pygmyhippo.MainActivity;
import com.example.pygmyhippo.R;
import com.example.pygmyhippo.common.Account;
import com.example.pygmyhippo.common.Entrant;
import com.example.pygmyhippo.common.Event;
import com.example.pygmyhippo.database.DBOnCompleteFlags;
import com.example.pygmyhippo.database.DBOnCompleteListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.util.ArrayList;

/**
 * Testing Class for Post Lottery User Page
 * @author Igor
 * @version 1.0
 */
@RunWith(JUnit4.class)
@LargeTest
public class PostLotteryTest {
    private FirebaseFirestore db;
    private Event newEvent;
    private ArrayList<Entrant> entrants;

    // Before running tests, update in EU2denNEmFMBe3pQ2A8L user_test_lost status to "rejected" and
    // in DYo8ytIPjVQc9wSIBxBY user_test_invited status to "invited"

    @Rule
    public ActivityScenarioRule<MainActivity> scenario = new ActivityScenarioRule<>(createIntent());

    @Rule
    public GrantPermissionRule mRuntimePermissionRule = GrantPermissionRule.grant(Manifest.permission.POST_NOTIFICATIONS);


    public static Intent createIntent() {
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_MAIN);
        intent.setClassName("com.example.pygmyhippo", "com.example.pygmyhippo.MainActivity");
        intent.addCategory(Intent.CATEGORY_LAUNCHER);
        intent.putExtra("currentRole", "user");

        Account account = new Account();
        account.setAccountID("user_test");
        account.setName("Testing account");
        account.setCurrentRole(Account.AccountRole.user);
        intent.putExtra("signedInAccount", account);

        return intent;
    }


    @Test
    public void testEventList() {
        try {
            Thread.sleep(1000);
        } catch (Exception e) {
            System.err.println(e);
        }
        onView(withId(R.id.u_my_events_menu_item)).check(matches(isDisplayed()));
    }

    @Test
    public void testEventInvitedButton() {
        scenario.getScenario().onActivity(activity -> {
            NavController navcontroller = Navigation.findNavController(activity, R.id.nav_host_fragment_activity_main);
            Bundle navArgs = new Bundle();
            Account account = new Account();
            account.setAccountID("user_test_invited");
            account.setName("Testing account");
            account.setCurrentRole(Account.AccountRole.user);
            navArgs.putParcelable("signedInAccount", account);
            navArgs.putString("eventID", "DYo8ytIPjVQc9wSIBxBY");
            navcontroller.navigate(R.id.u_my_events_menu_item, navArgs);
        });


        try {
            Thread.sleep(1000);
        } catch (Exception e) {
            System.err.println(e);
        }

        onView(withId(R.id.u_my_events_menu_item)).check(matches(isDisplayed()));

        try {
            Thread.sleep(2000); // wait time to upload
        } catch (Exception e) {
            System.err.println(e);
        }
        onData(anything()).inAdapterView(withId(R.id.u_event_listview)).atPosition(0).perform(click());

        try {
            Thread.sleep(2000); // wait time to upload
        } catch (Exception e) {
            System.err.println(e);
        }
        // Test to see if event fragment is switched by seeing if accept button is displayed
        onView(withId(R.id.u_acceptWaitlistButton)).check(matches(isDisplayed()));
        onView(withId(R.id.u_acceptWaitlistButton)).perform(click());

        try {
            Thread.sleep(2000); // wait time to upload
        } catch (Exception e) {
            System.err.println(e);
        }
        // check if official invitation is displayed
        onView(withId(R.id.u_userStatusDescription)).check(matches(isDisplayed()));
    }


    @Test
    public void testEventLostButton() {
        scenario.getScenario().onActivity(activity -> {
            NavController navcontroller = Navigation.findNavController(activity, R.id.nav_host_fragment_activity_main);
            Bundle navArgs = new Bundle();
            Account account = new Account();
            account.setAccountID("user_test_lost");
            account.setName("Testing account");
            account.setCurrentRole(Account.AccountRole.user);
            navArgs.putParcelable("signedInAccount", account);
            navArgs.putString("eventID", "EU2denNEmFMBe3pQ2A8L");
            navcontroller.navigate(R.id.u_my_events_menu_item, navArgs);
        });


        try {
            Thread.sleep(1000);
        } catch (Exception e) {
            System.err.println(e);
        }

        onView(withId(R.id.u_my_events_menu_item)).check(matches(isDisplayed()));

        try {
            Thread.sleep(2000); // wait time to upload
        } catch (Exception e) {
            System.err.println(e);
        }
        onData(anything()).inAdapterView(withId(R.id.u_event_listview)).atPosition(0).perform(click());

        try {
            Thread.sleep(2000); // wait time to upload
        } catch (Exception e) {
            System.err.println(e);
        }
        // Test to see if event fragment is switched by seeing if accept button is displayed
        onView(withId(R.id.u_acceptWaitlistButton)).check(matches(isDisplayed()));
        onView(withId(R.id.u_acceptWaitlistButton)).perform(click());

        try {
            Thread.sleep(2000); // wait time to upload
        } catch (Exception e) {
            System.err.println(e);
        }
        // check if official invitation is displayed
        onView(withId(R.id.u_userStatus)).check(matches(isDisplayed()));
    }

}




// Kept in case someone wants to use it



//@After
//public void deleteEventByID() {
//    FirebaseFirestore db = FirebaseFirestore.getInstance();
//    db.collection("Events")
//            .document("user_test_event")
//            .delete();
//}


//    public void addEvent(@NonNull Entrant.EntrantStatus status) {
//
//        entrants = new ArrayList<>();
//        // Add hardcoded entrants
//        entrants.add(new Entrant("user_test", status));
//
//        newEvent = new Event(
//                "Hippo Party",
//                "user_test_event",
//                "The Hippopotamus Society",
//                entrants,
//                "The Swamp",
//                "2024-10-31",
//                "4:00 PM MST - 4:00 AM MST",
//                "Love hippos and a party? Love a party! Join a party! We have lots of really cool hippos I'm sure you'd love to meet! There will be food, games, and all sorts of activities you could imagine! It's almost worth the price to see Moo Deng and his buddies!",
//                "$150.00",
//                "hippoparty.png",
//                Event.EventStatus.ongoing,
//                true);
//        FirebaseFirestore db = FirebaseFirestore.getInstance();
//        DocumentReference docRef = db.collection("Events").document();
//
//        newEvent.setEventID(docRef.getId());
//        docRef.set(newEvent)
//                .addOnCompleteListener(task -> {
//                    if (task.isSuccessful()) {
//                        Log.d("DB", String.format("Successfully added Event %s", newEvent.getEventID()));
//
//                        // Add the translated documents to a list to pass to the listener
//                        ArrayList<Event> newEventList = new ArrayList<>();
//                        newEventList.add(newEvent);
//                    } else {
//                        // Notify the listeners of errors
//                        Log.d("DB", String.format("Unsuccessfully in adding Even