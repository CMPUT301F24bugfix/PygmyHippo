package com.example.pygmyhippo.Common;

/*
The Unit tests for the Image dataclass
Purpose:
    - To test the getters and setters of the image
Issues:
    - None really. Maybe in future add database testing here
 */

import static org.junit.Assert.assertEquals;

import com.example.pygmyhippo.common.Image;

import org.junit.Before;
import org.junit.Test;

public class ImageTest {
    private Image image;

    @Before
    public void setUp() {
        image = new Image("https://image", "image1", Image.ImageType.Account);
    }

    @Test
    public void testGetID() {
        assertEquals("image1", image.getID());
    }

    @Test
    public void testSetID() {
        image.setID("image2");
        assertEquals("image2", image.getID());
    }

    @Test
    public void testGetImageType() {
        assertEquals(Image.ImageType.Account, image.getType());
    }

    @Test
    public void testSetImageType() {
        image.setType(Image.ImageType.Event);
        assertEquals(Image.ImageType.Event, image.getType());
    }

    @Test
    public void testGetUrl() {
        assertEquals("https://image", image.getUrl());
    }

    @Test
    public void testSetUrl() {
        image.setUrl("https://new");
        assertEquals("https://new", image.getUrl());
    }
}
