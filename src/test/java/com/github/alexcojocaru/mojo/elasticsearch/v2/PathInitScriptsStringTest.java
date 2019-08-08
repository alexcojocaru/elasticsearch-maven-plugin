package com.github.alexcojocaru.mojo.elasticsearch.v2;


import org.junit.Test;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class PathInitScriptsStringTest {

    @Test
    public void testStringSplitLogic(){
        String pathInitScripts = "one";
        List<String> l = pathInitScripts.isEmpty() ? new ArrayList<>() : Arrays.asList(pathInitScripts.split(","));
        assertEquals(1, l.size());

        pathInitScripts = "";
        l = pathInitScripts.isEmpty() ? new ArrayList<>() : Arrays.asList(pathInitScripts.split(","));
        assertEquals(0, l.size());

        pathInitScripts = "one,two";
        l = pathInitScripts.isEmpty() ? new ArrayList<>() : Arrays.asList(pathInitScripts.split(","));
        assertEquals(2, l.size());

        System.out.println();
    }

}
