package com.github.alexcojocaru.mojo.elasticsearch.v2.util.root;

import org.apache.commons.exec.CommandLine;

/**
 * Base class for Unix command wrapping strategies.
 */
abstract class AbstractUnixCommandWrapStrategy implements CommandWrapStrategy {

    protected String quote(String value) {
        return "'" + value.replace("'", "'\\''") + "'";
    }

    @Override
    public abstract CommandLine wrapCommand(CommandLine baseCommand, String username);
}
