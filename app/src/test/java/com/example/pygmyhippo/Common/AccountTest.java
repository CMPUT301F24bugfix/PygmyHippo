package com.example.pygmyhippo.Common;
/*
The Unit Tests for the Account Class
Purposes:
    - To test the getters and setters of the account class
Issues:
    - Some attributes of the account class may change in the future
 */
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import com.example.pygmyhippo.common.Account;
import com.example.pygmyhippo.common.Facility;

import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;


public class AccountTest {
    private Account testAccount;

    @Before
    public void setUp() {
        ArrayList<Account.AccountRole> roles = new ArrayList<>();
        roles.add(Account.AccountRole.user);
        roles.add(Account.AccountRole.admin);
        testAccount = new Account(
                "account1",
                "Bob",
                "he/him",
                "780 666 7777",
                "someEmail@gmail.com",
                "device1",
                "https//:profilePicture",
                "Edmonton AB",
                true,
                false,
                new ArrayList<>(),
                Account.AccountRole.user,
                null
        );
        testAccount.setRoles(roles);
    }

    @Test
    public void testGetName() {
        assertEquals("Bob", testAccount.getName());
    }

    @Test
    public void testSetName() {
        testAccount.setName("Kevin");
        assertEquals("Kevin", testAccount.getName());
    }

    @Test
    public void testGetAccountID() {
        assertEquals("account1", testAccount.getAccountID());
    }

    @Test
    public void testSetAccountID() {
        testAccount.setAccountID("account2");
        assertEquals("account2", testAccount.getAccountID());
    }

    @Test
    public void testGetPronouns() {
        assertEquals("he/him", testAccount.getPronouns());
    }

    @Test
    public void testSetPronouns() {
        testAccount.setPronouns("she/her");
        assertEquals("she/her", testAccount.getPronouns());
    }

    @Test
    public void testGetPhone() {
        assertEquals("780 666 7777", testAccount.getPhoneNumber());
    }

    @Test
    public void testSetPhone() {
        testAccount.setPhoneNumber("111 000 3333");
        assertEquals("111 000 3333", testAccount.getPhoneNumber());
    }

    @Test
    public void testGetEmail() {
        assertEquals("someEmail@gmail.com", testAccount.getEmailAddress());
    }

    @Test
    public void testSetEmail() {
        testAccount.setEmailAddress("newEmail@yahoo.ca");
        assertEquals("newEmail@yahoo.ca", testAccount.getEmailAddress());
    }

    @Test
    public void testGetDeviceID() {
        assertEquals("device1", testAccount.getDeviceID());
    }

    @Test
    public void testSetDeviceID() {
        testAccount.setDeviceID("device2");
        assertEquals("device2", testAccount.getDeviceID());
    }

    @Test
    public void testGetProfilePicture() {
        assertEquals("https//:profilePicture", testAccount.getProfilePicture());
    }

    @Test
    public void testSetProfilePicture() {
        testAccount.setProfilePicture("newPicture");
        assertEquals("newPicture", testAccount.getProfilePicture());
    }

    @Test
    public void testGetLocation() {
        assertEquals("Edmonton AB", testAccount.getLocation());
    }

    @Test
    public void testSetLocation() {
        testAccount.setLocation("Calgary AB");
        assertEquals("Calgary AB", testAccount.getLocation());
    }

    @Test
    public void testGetNotifications() {
        assertTrue(testAccount.isReceiveNotifications());
    }

    @Test
    public void testSetNotifications() {
        testAccount.setReceiveNotifications(false);
        assertFalse(testAccount.isReceiveNotifications());
    }

    @Test
    public void testisEnabledGeolocation() {
        assertFalse(testAccount.isEnableGeolocation());
    }

    @Test
    public void testSetEnabledGeolocation() {
        testAccount.setEnableGeolocation(true);
        assertTrue(testAccount.isEnableGeolocation());
    }

    @Test
    public void testGetRoles() {
        assertEquals("user", testAccount.getRoles().get(0).value);
        assertEquals("admin", testAccount.getRoles().get(1).value);
    }

    @Test
    public void testSetRoles() {
        ArrayList<Account.AccountRole> roles = new ArrayList<>();
        testAccount.setRoles(roles);
        assertEquals(roles, testAccount.getRoles());
    }

    @Test
    public void testGetCurrentRole() {
        assertEquals(Account.AccountRole.user, testAccount.getCurrentRole());
    }

    @Test
    public void testSetCurrentRole() {
        testAccount.setCurrentRole(Account.AccountRole.admin);
        assertEquals(Account.AccountRole.admin, testAccount.getCurrentRole());
    }

    @Test
    public void testGetFacility() {
        assertEquals(null, testAccount.getFacilityProfile());
    }
}
