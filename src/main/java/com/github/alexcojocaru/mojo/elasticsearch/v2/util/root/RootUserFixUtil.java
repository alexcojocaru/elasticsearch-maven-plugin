package com.github.alexcojocaru.mojo.elasticsearch.v2.util.root;

import com.github.alexcojocaru.mojo.elasticsearch.v2.InstanceConfiguration;
import org.apache.commons.lang3.SystemUtils;
import org.apache.maven.plugin.logging.Log;
import java.util.Arrays;
import java.util.List;

public class RootUserFixUtil {

    public static final String ES_USER_NAME = "esuser";
    private static final Object LOCK = new Object();
    private static boolean userCreated = false;
    private static String grantedPath = null;
    private static volatile boolean shutdownHookRegistered = false;

    private static final List<UserFixStrategy> STRATEGIES = Arrays.asList(
            new LinuxUserFixStrategy(),
            new MacUserFixStrategy()
    );

    private static UserFixStrategy activeStrategy;

    public static boolean fixRootUserIfNeeded(InstanceConfiguration config) {
        Log log = config.getClusterConfiguration().getLog();

        synchronized (LOCK) {
            activeStrategy = STRATEGIES.stream()
                    .filter(UserFixStrategy::isSupported)
                    .findFirst()
                    .orElse(null);

            if (activeStrategy == null) {
                log.warn("Root user handling not supported on this OS.");
                return false;
            }

            try {
                if (!activeStrategy.isRunningAsRoot()) {
                    log.debug("Running as non-root user, skipping fix.");
                    return false;
                }

                if (!activeStrategy.userExists(ES_USER_NAME)) {
                    activeStrategy.createUser(ES_USER_NAME, log);
                    userCreated = true;
                }

                activeStrategy.grantPermissions(ES_USER_NAME, config.getBaseDir(), log);
                grantedPath = config.getBaseDir();

                // Only register shutdown hook once
                if (!shutdownHookRegistered) {
                    Runtime.getRuntime().addShutdownHook(new Thread(() -> cleanupUserAndPermissions(log)));
                    shutdownHookRegistered = true;
                }

                return true;

            } catch (Exception e) {
                log.warn("Failed during root fix: " + e.getMessage());
                return false;
            }
        }
    }

    public static CommandWrapStrategy getCommandWrapStrategy() {
        if (SystemUtils.IS_OS_LINUX) {
            return new LinuxCommandWrapStrategy();
        } else if (SystemUtils.IS_OS_MAC) {
            return new MacCommandWrapStrategy();
        } else {
            return new NoOpCommandWrapStrategy();
        }
    }

    private static void cleanupUserAndPermissions(Log log) {
        if (activeStrategy == null) {
            log.debug("No cleanup strategy available.");
            return;
        }

        try {
            if (grantedPath != null)
                activeStrategy.revokePermissions(ES_USER_NAME, grantedPath, log);
        } catch (Exception e) {
            log.warn("Failed to revoke permissions: " + e.getMessage());
        }

        try {
            if (userCreated)
                activeStrategy.deleteUser(ES_USER_NAME, log);
        } catch (Exception e) {
            log.warn("Failed to delete user: " + e.getMessage());
        }
    }
}

