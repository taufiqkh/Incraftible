package com.quiptiq.incraftible.message;

import junit.framework.Assert;

import org.bukkit.Material;
import org.junit.Before;
import org.junit.Test;

public class MaterialNamerTest {
    MaterialNamer namer;

    @Before
    public void setUp() {
        namer = MaterialNamer.getInstance();
    }

    /**
     * Name of a material with a configured value should return that value.
     */
    @Test
    public void testConfiguredName() {
        Assert.assertEquals(
                "Material with an item name specified should return the configured value", "Minecart with Furnace",
                namer.getName(Material.POWERED_MINECART));
    }

    /**
     * Name of a material without a configured value should return a derived
     * value.
     */
    @Test
    public void testDerivedName() {
        Assert.assertEquals(
                "Material without an item name specified should return a derived value", "bed block",
                namer.getName(Material.BED_BLOCK));
    }
}
