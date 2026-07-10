package com.github.alexcojocaru.mojo.elasticsearch.v2.util.root;

import org.apache.commons.exec.CommandLine;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class LinuxCommandWrapStrategy extends AbstractUnixCommandWrapStrategy {

    @Override
    public CommandLine wrapCommand(CommandLine baseCommand, String username) {
        // Convert CommandLine to string for execution via su -c
        String innerCommand = Stream.of(baseCommand.toStrings())
                .map(this::quote)
                .collect(Collectors.joining(" "));

        CommandLine cmd = new CommandLine("su");
        cmd.addArgument(username);
        cmd.addArgument("-s");
        cmd.addArgument("/bin/bash");
        cmd.addArgument("-c");
        cmd.addArgument(innerCommand, false);

        return cmd;
    }
}
