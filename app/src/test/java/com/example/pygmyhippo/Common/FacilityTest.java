package com.example.pygmyhippo.Common;

/*
The Unit tests for the facility class
Purposes:
    - Test the getters and setters for the class
Issues:
    - Should test if parsable is working
 */
import static org.junit.Assert.assertEquals;

import com.example.pygmyhippo.common.Facility;

import org.junit.Before;
import org.junit.Test;

public class FacilityTest {
    private Facility facility;

    @Before
    public void setUp() {
        facility = new Facility("https//:pic", "facility", "Whyte Ave");
    }

    @Test
    public void testGetLocation() {
        assertEquals("Whyte Ave", facility.getLocation());
    }

    @Test
    public void testSetLocation() {
        facility.setLocation("Grey Ave");
        assertEquals("Grey Ave", facility.getLocation());
    }

    @Test
    public void testGetName() {
        assertEquals("facility", facility.getName());
    }

    @Test
    public void testSetName() {
        facility.setName("newFacility");
        assertEquals("newFacility", facility.getName());
    }

    @Test
    public void testGetPicture() {
        assertEquals("https//:pic", facility.getFacilityPicture());
    }

    @Test
    public void testSetPicture() {
        facility.setFacilityPicture("https//:new");
        assertEquals("https//:new", facility.getFacilityPicture());
    }
}
