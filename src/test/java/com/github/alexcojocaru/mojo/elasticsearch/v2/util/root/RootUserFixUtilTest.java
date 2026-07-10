package com.github.alexcojocaru.mojo.elasticsearch.v2.util.root;

import org.apache.commons.exec.CommandLine;
import org.apache.commons.lang3.SystemUtils;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Unit tests for RootUserFixUtil.
 *
 * Note: These tests verify the logic flow without mocking or running as root.
 * They test the public API and behavior that can be verified without root access.
 */
public class RootUserFixUtilTest {

    @Test
    public void testGetCommandWrapStrategyReturnsCorrectType() {
        // This test verifies that the correct strategy is returned based on OS
        CommandWrapStrategy strategy = RootUserFixUtil.getCommandWrapStrategy();

        assertNotNull("Strategy should never be null", strategy);

        if (SystemUtils.IS_OS_LINUX) {
            assertTrue("Should return LinuxCommandWrapStrategy on Linux",
                    strategy instanceof LinuxCommandWrapStrategy);
        } else if (SystemUtils.IS_OS_MAC) {
            assertTrue("Should return MacCommandWrapStrategy on macOS",
                    strategy instanceof MacCommandWrapStrategy);
        } else {
            assertTrue("Should return NoOpCommandWrapStrategy on unsupported OS",
                    strategy instanceof NoOpCommandWrapStrategy);
        }
    }

    @Test
    public void testGetCommandWrapStrategyConsistency() {
        // Verify that multiple calls return the same type of strategy
        CommandWrapStrategy strategy1 = RootUserFixUtil.getCommandWrapStrategy();
        CommandWrapStrategy strategy2 = RootUserFixUtil.getCommandWrapStrategy();

        assertNotNull("First call should return strategy", strategy1);
        assertNotNull("Second call should return strategy", strategy2);
        assertEquals("Multiple calls should return same strategy type",
                strategy1.getClass(), strategy2.getClass());
    }

    @Test
    public void testEsUserNameConstant() {
        // Verify the constant is properly defined and accessible
        assertEquals("ES_USER_NAME should be 'esuser'",
                "esuser", RootUserFixUtil.ES_USER_NAME);
        assertNotNull("ES_USER_NAME should not be null",
                RootUserFixUtil.ES_USER_NAME);
        assertFalse("ES_USER_NAME should not be empty",
                RootUserFixUtil.ES_USER_NAME.isEmpty());
    }

    @Test
    public void testCommandWrapStrategyNeverReturnsNull() {
        // Verify that getCommandWrapStrategy never returns null
        // regardless of platform
        CommandWrapStrategy strategy = RootUserFixUtil.getCommandWrapStrategy();
        assertNotNull("Strategy should never be null", strategy);
    }

    @Test
    public void testCommandWrapStrategyCanWrapCommands() {
        // Verify that the returned strategy can actually wrap commands
        CommandWrapStrategy strategy = RootUserFixUtil.getCommandWrapStrategy();

        try {
            CommandLine cmd = new CommandLine("test");
            CommandLine wrapped = strategy.wrapCommand(cmd, "testuser");
            assertNotNull("Wrapped command should not be null", wrapped);
            assertTrue("Wrapped command should have at least one argument",
                    wrapped.toStrings().length > 0);
        } catch (Exception e) {
            fail("Should not throw exception when wrapping command: " + e.getMessage());
        }
    }

    @Test
    public void testCommandWrapStrategyWithComplexCommand() {
        // Test that complex commands can be wrapped
        CommandWrapStrategy strategy = RootUserFixUtil.getCommandWrapStrategy();

        CommandLine cmd = new CommandLine("bin/elasticsearch");
        cmd.addArgument("-p");
        cmd.addArgument("pid");
        cmd.addArgument("-Epath.data=/tmp");

        try {
            CommandLine wrapped = strategy.wrapCommand(cmd, "esuser");
            assertNotNull("Wrapped complex command should not be null", wrapped);

            String[] args = wrapped.toStrings();
            assertTrue("Wrapped command should have multiple arguments",
                    args.length >= 3);
        } catch (Exception e) {
            fail("Should not throw exception with complex command: " + e.getMessage());
        }
    }

    @Test
    public void testGetCommandWrapStrategyMultipleThreads() throws InterruptedException {
        // Test that multiple threads can safely call getCommandWrapStrategy
        final CommandWrapStrategy[] strategies = new CommandWrapStrategy[5];
        Thread[] threads = new Thread[5];

        for (int i = 0; i < threads.length; i++) {
            final int index = i;
            threads[i] = new Thread(() -> {
                strategies[index] = RootUserFixUtil.getCommandWrapStrategy();
            });
        }

        // Start all threads
        for (Thread thread : threads) {
            thread.start();
        }

        // Wait for completion
        for (Thread thread : threads) {
            thread.join(5000);
        }

        // All threads should have completed
        for (Thread thread : threads) {
            assertFalse("Thread should have completed", thread.isAlive());
        }

        // All should return same type
        for (CommandWrapStrategy strategy : strategies) {
            assertNotNull("Strategy should not be null", strategy);
            assertEquals("All strategies should be same type",
                    strategies[0].getClass(), strategy.getClass());
        }
    }
}
