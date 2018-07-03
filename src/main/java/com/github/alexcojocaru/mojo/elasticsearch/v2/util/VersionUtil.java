package com.github.alexcojocaru.mojo.elasticsearch.v2.util;

public class VersionUtil {
    
    public static boolean isUnder_5_0_0(String version)
    {
        return version.matches("[0-4]\\..*");
    }

    public static boolean isBetween_5_0_0_and_6_2_x(String version) {
        return version.matches("5\\..*") || version.matches("6\\.[0-2]\\..*");
    }

}
