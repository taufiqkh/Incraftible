package com.quiptiq.incraftible.message;

import junit.framework.Assert;

import org.junit.Test;

/**
 * Tests basic message functionality.
 *
 * @author Taufiq Hoven
 */
public class FixedMessageTest {
    private static final String PROJECT_VERSION_PROPERTY_NAME = "project.version";

    @Test
    public void testVersion() {
        Assert.assertEquals(
                "Version must match across project files", FixedMessage.VERSION,
                System.getProperty(PROJECT_VERSION_PROPERTY_NAME));
    }
}
