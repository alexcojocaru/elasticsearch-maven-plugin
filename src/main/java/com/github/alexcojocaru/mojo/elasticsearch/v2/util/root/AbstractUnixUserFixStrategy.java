package com.github.alexcojocaru.mojo.elasticsearch.v2.util.root;

import org.apache.maven.plugin.logging.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.stream.Collectors;

/**
 * Base class for Unix-like user fix strategies.
 * Provides shared helpers for command execution and logging.
 */
abstract class AbstractUnixUserFixStrategy implements UserFixStrategy {

    protected CommandResult runCommand(String... cmd) throws IOException, InterruptedException {
        ProcessBuilder pb = new ProcessBuilder(cmd);
        pb.redirectErrorStream(true);
        Process process = pb.start();

        String output;
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
            output = reader.lines().collect(Collectors.joining("\n"));
        }

        int exitCode = process.waitFor();
        return new CommandResult(exitCode, output);
    }

    protected void logResult(Log log, CommandResult result, String successMsg, String failMsg) {
        if (result.exitCode == 0) {
            log.info(successMsg);
        } else {
            log.warn(failMsg + ": " + result.output);
        }
    }

    protected static class CommandResult {
        final int exitCode;
        final String output;
        CommandResult(int exitCode, String output) {
            this.exitCode = exitCode;
            this.output = output;
        }
    }

    @Override
    public boolean isRunningAsRoot() {
        return "root".equals(System.getProperty("user.name", ""));
    }
}
