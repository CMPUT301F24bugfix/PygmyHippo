package com.example.pygmyhippo.Common;

/*
The Unit tests for the facility class
Purposes:
    - Test the getters and setters for the class
Issues:
    - Should test if parsable is working
 */
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

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

    @Test
    public void testFacilityExists() {
        // Exits but in our circumstances none of the fields have been made. So should not exist
        Facility newFacility = new Facility();
        assertFalse(newFacility.facilityExists());

        // Change one fields and it should "exist" ie there is something to display
        newFacility.setName("Some Facility");
        assertTrue(newFacility.facilityExists());

        newFacility.setName(null);
        newFacility.setLocation("88 St. 45 Ave");
        assertTrue(newFacility.facilityExists());

        newFacility.setLocation(null);
        newFacility.setFacilityPicture("https//:image/url");
        assertTrue(newFacility.facilityExists());

        assertTrue(facility.facilityExists());
    }
}
