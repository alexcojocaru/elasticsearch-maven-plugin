package com.github.alexcojocaru.mojo.elasticsearch.v2.util.root;

import org.apache.commons.exec.CommandLine;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class MacCommandWrapStrategy extends AbstractUnixCommandWrapStrategy {

    @Override
    public CommandLine wrapCommand(CommandLine baseCommand, String username) {
        // macOS 'su' syntax is slightly different
        String innerCommand = Stream.of(baseCommand.toStrings())
                .map(this::quote)
                .collect(Collectors.joining(" "));

        CommandLine cmd = new CommandLine("su");
        cmd.addArgument(username);
        cmd.addArgument("-c");
        cmd.addArgument(innerCommand, false);

        return cmd;
    }
}
