package com.github.alexcojocaru.mojo.elasticsearch.v2.util.root;

import org.apache.commons.exec.CommandLine;

/**
 * Defines how to wrap Elasticsearch start commands to run under the correct user.
 */
public interface CommandWrapStrategy {
    /**
     * Wrap the base Elasticsearch command so it executes as the given user.
     * On unsupported platforms, this may simply return the unmodified command.
     */
    CommandLine wrapCommand(CommandLine baseCommand, String username);
}
