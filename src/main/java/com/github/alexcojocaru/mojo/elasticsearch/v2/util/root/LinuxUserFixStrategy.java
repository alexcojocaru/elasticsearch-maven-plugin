package com.github.alexcojocaru.mojo.elasticsearch.v2.util.root;

import org.apache.maven.plugin.logging.Log;
import org.apache.commons.lang3.SystemUtils;

class LinuxUserFixStrategy extends AbstractUnixUserFixStrategy {

    @Override
    public boolean isSupported() {
        return SystemUtils.IS_OS_LINUX;
    }

    @Override
    public boolean userExists(String username) throws Exception {
        return runCommand("id", "-u", username).exitCode == 0;
    }

    @Override
    public void createUser(String username, Log log) throws Exception {
        CommandResult result = runCommand("useradd", "-r", "-s", "/usr/sbin/nologin", username);
        logResult(log, result,
                "User '" + username + "' created (Linux)",
                "Failed to create Linux user");
    }

    @Override
    public void grantPermissions(String username, String baseDir, Log log) throws Exception {
        CommandResult result = runCommand("chmod", "-R", "a+rwx", baseDir);
        logResult(log, result,
                "Granted permissions for '" + username + "' on " + baseDir,
                "Failed to grant permissions on Linux");
    }

    @Override
    public void revokePermissions(String username, String baseDir, Log log) throws Exception {
        CommandResult result = runCommand("chmod", "-R", "u+rwX,g+rX,o-rwx", baseDir);
        logResult(log, result,
                "Revoked permissions for '" + username + "' on " + baseDir,
                "Failed to revoke permissions on Linux");
    }

    @Override
    public void deleteUser(String username, Log log) throws Exception {
        CommandResult result = runCommand("userdel", "-r", username);
        logResult(log, result,
                "User '" + username + "' deleted (Linux)",
                "Failed to delete Linux user");
    }
}
