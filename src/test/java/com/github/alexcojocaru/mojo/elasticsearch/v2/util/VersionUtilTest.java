package com.github.alexcojocaru.mojo.elasticsearch.v2.util;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class VersionUtilTest {
    
    @Test
    public void testIsUnder_5_0_0()
    {
        assertTrue(VersionUtil.isUnder_5_0_0("0.9.7"));
        assertTrue(VersionUtil.isUnder_5_0_0("4.2"));
        assertTrue(VersionUtil.isUnder_5_0_0("4.2.1"));

        assertFalse(VersionUtil.isUnder_5_0_0("5.0.0"));
        assertFalse(VersionUtil.isUnder_5_0_0("5.2.9"));
        assertFalse(VersionUtil.isUnder_5_0_0("6.3.0"));
    }
    
    @Test
    public void testIsBetween_5_0_0_and_6_x_x()
    {
        assertTrue(VersionUtil.isBetween_5_0_0_and_6_2_x("5.0.0"));
        assertTrue(VersionUtil.isBetween_5_0_0_and_6_2_x("5.2.1"));
        assertTrue(VersionUtil.isBetween_5_0_0_and_6_2_x("6.0.0"));
        assertTrue(VersionUtil.isBetween_5_0_0_and_6_2_x("6.1.0"));
        assertTrue(VersionUtil.isBetween_5_0_0_and_6_2_x("6.2.0"));
        assertTrue(VersionUtil.isBetween_5_0_0_and_6_2_x("6.2.19"));

        assertFalse(VersionUtil.isBetween_5_0_0_and_6_2_x("6.3.0"));
        assertFalse(VersionUtil.isBetween_5_0_0_and_6_2_x("6.4.9"));
        assertFalse(VersionUtil.isBetween_5_0_0_and_6_2_x("7.0.0"));
        assertFalse(VersionUtil.isBetween_5_0_0_and_6_2_x("7.1.0"));
    }

    @Test
    public void testIsEqualOrGreater_6_4_0()
    {
        assertTrue(VersionUtil.isEqualOrGreater_6_4_0("6.4.0"));
        assertTrue(VersionUtil.isEqualOrGreater_6_4_0("6.4.1"));
        assertTrue(VersionUtil.isEqualOrGreater_6_4_0("6.5.0"));
        assertTrue(VersionUtil.isEqualOrGreater_6_4_0("6.11.3"));
        assertTrue(VersionUtil.isEqualOrGreater_6_4_0("7.0.0"));
        assertTrue(VersionUtil.isEqualOrGreater_6_4_0("11.2.1"));

        assertFalse(VersionUtil.isEqualOrGreater_6_4_0("6.3.1"));
        assertFalse(VersionUtil.isEqualOrGreater_6_4_0("6.3.0"));
        assertFalse(VersionUtil.isEqualOrGreater_6_4_0("6.0.0"));
        assertFalse(VersionUtil.isEqualOrGreater_6_4_0("5.1.1"));
        assertFalse(VersionUtil.isEqualOrGreater_6_4_0("4.11.1"));
    }

    @Test
    public void testIsEqualOrGreater_7_0_0()
    {
        assertTrue(VersionUtil.isEqualOrGreater_7_0_0("7.0.0-beta1"));
        assertTrue(VersionUtil.isEqualOrGreater_7_0_0("7.0.0"));
        assertTrue(VersionUtil.isEqualOrGreater_7_0_0("11.2.1"));

        assertFalse(VersionUtil.isEqualOrGreater_7_0_0("6.11.3"));
        assertFalse(VersionUtil.isEqualOrGreater_7_0_0("6.5.0"));
        assertFalse(VersionUtil.isEqualOrGreater_7_0_0("6.4.1"));
        assertFalse(VersionUtil.isEqualOrGreater_7_0_0("6.4.0"));
        assertFalse(VersionUtil.isEqualOrGreater_7_0_0("6.3.1"));
        assertFalse(VersionUtil.isEqualOrGreater_7_0_0("6.3.0"));
        assertFalse(VersionUtil.isEqualOrGreater_7_0_0("6.0.0"));
        assertFalse(VersionUtil.isEqualOrGreater_7_0_0("5.1.1"));
        assertFalse(VersionUtil.isEqualOrGreater_7_0_0("4.11.1"));
    }

}
