package com.github.alexcojocaru.mojo.elasticsearch.v2.util.root;

import org.apache.commons.exec.CommandLine;

public class NoOpCommandWrapStrategy implements CommandWrapStrategy {

    @Override
    public CommandLine wrapCommand(CommandLine baseCommand, String username) {
        return baseCommand; // Do nothing
    }
}
