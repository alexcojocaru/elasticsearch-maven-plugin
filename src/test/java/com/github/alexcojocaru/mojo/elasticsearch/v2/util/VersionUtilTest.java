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
    public void testIsBetween_6_3_0_and_7_10_x()
    {
        assertFalse(VersionUtil.isBetween_6_3_0_and_7_10_x("5.0.0"));
        assertFalse(VersionUtil.isBetween_6_3_0_and_7_10_x("5.9.0"));

        assertFalse(VersionUtil.isBetween_6_3_0_and_7_10_x("6.0.0"));
        assertFalse(VersionUtil.isBetween_6_3_0_and_7_10_x("6.1.0"));
        assertFalse(VersionUtil.isBetween_6_3_0_and_7_10_x("6.2.0"));
        assertFalse(VersionUtil.isBetween_6_3_0_and_7_10_x("6.2.19"));

        assertTrue(VersionUtil.isBetween_6_3_0_and_7_10_x("6.3.0"));
        assertTrue(VersionUtil.isBetween_6_3_0_and_7_10_x("6.4.9"));
        assertTrue(VersionUtil.isBetween_6_3_0_and_7_10_x("7.0.0"));
        assertTrue(VersionUtil.isBetween_6_3_0_and_7_10_x("7.1.0"));
        assertTrue(VersionUtil.isBetween_6_3_0_and_7_10_x("7.9.0"));
        assertTrue(VersionUtil.isBetween_6_3_0_and_7_10_x("7.10.0"));
        assertTrue(VersionUtil.isBetween_6_3_0_and_7_10_x("7.10.2"));
        assertTrue(VersionUtil.isBetween_6_3_0_and_7_10_x("7.10.3"));

        assertFalse(VersionUtil.isBetween_6_3_0_and_7_10_x("7.11.0"));
        assertFalse(VersionUtil.isBetween_6_3_0_and_7_10_x("7.11.3"));
        assertFalse(VersionUtil.isBetween_6_3_0_and_7_10_x("7.15.3"));
        assertFalse(VersionUtil.isBetween_6_3_0_and_7_10_x("7.19.3"));
        assertFalse(VersionUtil.isBetween_6_3_0_and_7_10_x("7.20.3"));
        assertFalse(VersionUtil.isBetween_6_3_0_and_7_10_x("7.27.0"));
        assertFalse(VersionUtil.isBetween_6_3_0_and_7_10_x("8.1.0"));
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
    
    @Test
    public void testIsEqualOrGreater_8_0_0()
    {
        assertTrue(VersionUtil.isEqualOrGreater_8_0_0("8.0.0"));
        assertTrue(VersionUtil.isEqualOrGreater_8_0_0("8.2.7"));
        assertTrue(VersionUtil.isEqualOrGreater_8_0_0("9.0.0"));
        assertTrue(VersionUtil.isEqualOrGreater_8_0_0("11.2.1"));

        assertFalse(VersionUtil.isEqualOrGreater_8_0_0("6.11.3"));
        assertFalse(VersionUtil.isEqualOrGreater_8_0_0("7.1.1"));
        assertFalse(VersionUtil.isEqualOrGreater_8_0_0("4.11.1"));
    }


}
