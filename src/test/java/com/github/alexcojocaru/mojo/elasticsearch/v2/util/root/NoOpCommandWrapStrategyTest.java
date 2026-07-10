package com.github.alexcojocaru.mojo.elasticsearch.v2.util.root;

import org.apache.commons.exec.CommandLine;
import org.junit.Test;

import static org.junit.Assert.*;

public class NoOpCommandWrapStrategyTest {

    @Test
    public void testWrapCommandReturnsUnmodified() {
        NoOpCommandWrapStrategy strategy = new NoOpCommandWrapStrategy();
        CommandLine baseCommand = new CommandLine("bin/elasticsearch");
        baseCommand.addArgument("-p");
        baseCommand.addArgument("pid");

        CommandLine wrapped = strategy.wrapCommand(baseCommand, "esuser");

        // Should return the exact same command, unmodified
        assertNotNull(wrapped);
        String[] args = wrapped.toStrings();

        assertEquals("bin/elasticsearch", args[0]);
        assertEquals("-p", args[1]);
        assertEquals("pid", args[2]);
        assertEquals(3, args.length);

        // Should NOT contain su command
        for (String arg : args) {
            assertNotEquals("Should not contain 'su'", "su", arg);
        }
    }

    @Test
    public void testWrapCommandIgnoresUsername() {
        NoOpCommandWrapStrategy strategy = new NoOpCommandWrapStrategy();
        CommandLine baseCommand = new CommandLine("bin/elasticsearch");

        // Username should be ignored
        CommandLine wrapped1 = strategy.wrapCommand(baseCommand, "user1");
        CommandLine wrapped2 = strategy.wrapCommand(baseCommand, "user2");
        CommandLine wrapped3 = strategy.wrapCommand(baseCommand, null);

        // All should return the same command
        assertArrayEquals(wrapped1.toStrings(), wrapped2.toStrings());
        assertArrayEquals(wrapped1.toStrings(), wrapped3.toStrings());
    }

    @Test
    public void testWrapCommandWithComplexArguments() {
        NoOpCommandWrapStrategy strategy = new NoOpCommandWrapStrategy();
        CommandLine baseCommand = new CommandLine("bin/elasticsearch");
        baseCommand.addArgument("-Epath.data=/tmp/data");
        baseCommand.addArgument("-Epath.logs=/var/log");
        baseCommand.addArgument("-Expack.security.enabled=false");

        CommandLine wrapped = strategy.wrapCommand(baseCommand, "esuser");

        String[] args = wrapped.toStrings();

        // Should be completely unmodified
        assertEquals("bin/elasticsearch", args[0]);
        assertEquals("-Epath.data=/tmp/data", args[1]);
        assertEquals("-Epath.logs=/var/log", args[2]);
        assertEquals("-Expack.security.enabled=false", args[3]);
        assertEquals(4, args.length);
    }
}
