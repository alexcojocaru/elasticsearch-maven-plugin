package com.github.alexcojocaru.mojo.elasticsearch.v2.util;

public class VersionUtil {
    
    public static boolean isUnder_5_0_0(String version)
    {
        return version.matches("[0-4]\\..*");
    }

    public static boolean isBetween_5_0_0_and_6_2_x(String version)
    {
        return version.matches("5\\..*") || version.matches("6\\.[0-2]\\..*");
    }

    public static boolean isEqualOrGreater_6_4_0(String version)
    {
        return version.matches("6\\.([4-9]|(\\d){2,})\\..*")
                || version.matches("([7-9]|(\\d){2,})\\..*");
    }

    public static boolean isEqualOrGreater_7_0_0(String version)
    {
        return version.matches("([7-9]|(\\d){2,})\\..*");
    }

}
