package com.github.alexcojocaru.mojo.elasticsearch.v2.util.root;

import org.apache.commons.exec.CommandLine;
import org.junit.Test;

import static org.junit.Assert.*;

public class MacCommandWrapStrategyTest {

    @Test
    public void testWrapCommandBasic() {
        MacCommandWrapStrategy strategy = new MacCommandWrapStrategy();
        CommandLine baseCommand = new CommandLine("bin/elasticsearch");

        CommandLine wrapped = strategy.wrapCommand(baseCommand, "esuser");

        assertNotNull(wrapped);
        String[] args = wrapped.toStrings();

        // Expected: su esuser -c 'bin/elasticsearch'
        // Note: macOS su doesn't use -s flag
        assertEquals("su", args[0]);
        assertEquals("esuser", args[1]);
        assertEquals("-c", args[2]);
        // args[3] should contain the quoted command
        assertTrue(args[3].contains("bin/elasticsearch"));
    }

    @Test
    public void testWrapCommandWithArguments() {
        MacCommandWrapStrategy strategy = new MacCommandWrapStrategy();
        CommandLine baseCommand = new CommandLine("bin/elasticsearch");
        baseCommand.addArgument("-p");
        baseCommand.addArgument("pid");
        baseCommand.addArgument("-Epath.data=/tmp/data");

        CommandLine wrapped = strategy.wrapCommand(baseCommand, "esuser");

        String[] args = wrapped.toStrings();
        assertEquals("su", args[0]);
        assertEquals("esuser", args[1]);
        assertEquals("-c", args[2]);

        // The inner command should be in the -c argument
        String innerCommand = args[3];
        assertTrue("Should contain bin/elasticsearch", innerCommand.contains("bin/elasticsearch"));
        assertTrue("Should contain -p argument", innerCommand.contains("-p"));
        assertTrue("Should contain pid argument", innerCommand.contains("pid"));
        assertTrue("Should contain -Epath.data", innerCommand.contains("-Epath.data"));
    }

    @Test
    public void testWrapCommandDifferentFromLinux() {
        MacCommandWrapStrategy macStrategy = new MacCommandWrapStrategy();
        LinuxCommandWrapStrategy linuxStrategy = new LinuxCommandWrapStrategy();
        CommandLine baseCommand = new CommandLine("bin/elasticsearch");

        CommandLine macWrapped = macStrategy.wrapCommand(baseCommand, "esuser");
        CommandLine linuxWrapped = linuxStrategy.wrapCommand(baseCommand, "esuser");

        String[] macArgs = macWrapped.toStrings();
        String[] linuxArgs = linuxWrapped.toStrings();

        // macOS: su esuser -c 'command'
        // Linux: su esuser -s /bin/bash -c 'command'
        assertTrue("Mac should have fewer arguments than Linux",
                macArgs.length < linuxArgs.length);

        // Mac should not have -s flag
        for (String arg : macArgs) {
            assertNotEquals("macOS su should not use -s flag", "-s", arg);
        }

        // Linux should have -s flag
        boolean linuxHasSFlag = false;
        for (String arg : linuxArgs) {
            if ("-s".equals(arg)) {
                linuxHasSFlag = true;
                break;
            }
        }
        assertTrue("Linux su should use -s flag", linuxHasSFlag);
    }

    @Test
    public void testWrapCommandWithSpacesInArguments() {
        MacCommandWrapStrategy strategy = new MacCommandWrapStrategy();
        CommandLine baseCommand = new CommandLine("bin/elasticsearch");
        baseCommand.addArgument("-Epath.data=/tmp/my data");

        CommandLine wrapped = strategy.wrapCommand(baseCommand, "esuser");

        String[] args = wrapped.toStrings();
        String innerCommand = args[3];

        // Should properly quote arguments with spaces
        assertTrue("Should contain path with spaces", innerCommand.contains("my data"));
        // The inner command should be properly quoted
        assertTrue("Should be quoted", innerCommand.startsWith("'") || innerCommand.contains("'"));
    }
}
