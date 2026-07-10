package com.github.alexcojocaru.mojo.elasticsearch.v2.util.root;

import org.apache.commons.exec.CommandLine;
import org.junit.Test;

import static org.junit.Assert.*;

public class LinuxCommandWrapStrategyTest {

    @Test
    public void testWrapCommandBasic() {
        LinuxCommandWrapStrategy strategy = new LinuxCommandWrapStrategy();
        CommandLine baseCommand = new CommandLine("bin/elasticsearch");

        CommandLine wrapped = strategy.wrapCommand(baseCommand, "esuser");

        assertNotNull(wrapped);
        String[] args = wrapped.toStrings();

        // Expected: su esuser -s /bin/bash -c 'bin/elasticsearch'
        assertEquals("su", args[0]);
        assertEquals("esuser", args[1]);
        assertEquals("-s", args[2]);
        assertEquals("/bin/bash", args[3]);
        assertEquals("-c", args[4]);
        // args[5] should contain the quoted command
        assertTrue(args[5].contains("bin/elasticsearch"));
    }

    @Test
    public void testWrapCommandWithArguments() {
        LinuxCommandWrapStrategy strategy = new LinuxCommandWrapStrategy();
        CommandLine baseCommand = new CommandLine("bin/elasticsearch");
        baseCommand.addArgument("-p");
        baseCommand.addArgument("pid");
        baseCommand.addArgument("-Epath.data=/tmp/data");

        CommandLine wrapped = strategy.wrapCommand(baseCommand, "esuser");

        String[] args = wrapped.toStrings();
        assertEquals("su", args[0]);
        assertEquals("esuser", args[1]);

        // The inner command should be in the -c argument
        String innerCommand = args[5];
        assertTrue("Should contain bin/elasticsearch", innerCommand.contains("bin/elasticsearch"));
        assertTrue("Should contain -p argument", innerCommand.contains("-p"));
        assertTrue("Should contain pid argument", innerCommand.contains("pid"));
        assertTrue("Should contain -Epath.data", innerCommand.contains("-Epath.data"));
    }

    @Test
    public void testWrapCommandWithSpacesInArguments() {
        LinuxCommandWrapStrategy strategy = new LinuxCommandWrapStrategy();
        CommandLine baseCommand = new CommandLine("bin/elasticsearch");
        baseCommand.addArgument("-Epath.data=/tmp/my data");

        CommandLine wrapped = strategy.wrapCommand(baseCommand, "esuser");

        String[] args = wrapped.toStrings();
        String innerCommand = args[5];

        // Should properly quote arguments with spaces
        assertTrue("Should contain path with spaces", innerCommand.contains("my data"));
        // The inner command should be properly quoted
        assertTrue("Should be quoted", innerCommand.startsWith("'") || innerCommand.contains("'"));
    }

    @Test
    public void testWrapCommandPreservesUsername() {
        LinuxCommandWrapStrategy strategy = new LinuxCommandWrapStrategy();
        CommandLine baseCommand = new CommandLine("bin/elasticsearch");

        CommandLine wrapped1 = strategy.wrapCommand(baseCommand, "testuser1");
        CommandLine wrapped2 = strategy.wrapCommand(baseCommand, "testuser2");

        assertEquals("testuser1", wrapped1.toStrings()[1]);
        assertEquals("testuser2", wrapped2.toStrings()[1]);
    }
}
