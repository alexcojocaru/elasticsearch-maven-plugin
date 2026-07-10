package com.github.alexcojocaru.mojo.elasticsearch.v2.util.root;

import org.apache.maven.plugin.logging.Log;
import org.apache.commons.lang3.SystemUtils;

class MacUserFixStrategy extends AbstractUnixUserFixStrategy {

    @Override
    public boolean isSupported() {
        return SystemUtils.IS_OS_MAC;
    }

    @Override
    public boolean userExists(String username) throws Exception {
        return runCommand("dscl", ".", "-read", "/Users/" + username).exitCode == 0;
    }

    @Override
    public void createUser(String username, Log log) throws Exception {
        String uid = findNextMacSystemUid();
        runCommand("dscl", ".", "-create", "/Users/" + username);
        runCommand("dscl", ".", "-create", "/Users/" + username, "UserShell", "/bin/bash");
        runCommand("dscl", ".", "-create", "/Users/" + username, "RealName", username);
        runCommand("dscl", ".", "-create", "/Users/" + username, "UniqueID", uid);
        runCommand("dscl", ".", "-create", "/Users/" + username, "PrimaryGroupID", "20");
        runCommand("dscl", ".", "-create", "/Users/" + username, "NFSHomeDirectory", "/var/empty");
        runCommand("dscl", ".", "-passwd", "/Users/" + username, "");
        log.info("User '" + username + "' created on macOS (UID " + uid + ")");
    }

    @Override
    public void grantPermissions(String username, String baseDir, Log log) throws Exception {
        String aclEntry = username + " allow list,search,read,write,execute,delete,add_file,add_subdirectory,delete_child";
        CommandResult result = runCommand("chmod", "-R", "+a", aclEntry, baseDir);
        logResult(log, result,
                "Granted ACL permissions for '" + username + "' on " + baseDir,
                "Failed to grant ACL permissions on macOS");
    }

    @Override
    public void revokePermissions(String username, String baseDir, Log log) throws Exception {
        CommandResult result = runCommand("chmod", "-R", "-a", username, baseDir);
        logResult(log, result,
                "Revoked ACL permissions for '" + username + "' on " + baseDir,
                "Failed to revoke ACL permissions on macOS");
    }

    @Override
    public void deleteUser(String username, Log log) throws Exception {
        CommandResult result = runCommand("dscl", ".", "-delete", "/Users/" + username);
        logResult(log, result,
                "User '" + username + "' deleted (macOS)",
                "Failed to delete macOS user");
    }

    private String findNextMacSystemUid() throws Exception {
        CommandResult result = runCommand("dscl", ".", "-list", "/Users", "UniqueID");
        int maxUid = 200;
        for (String line : result.output.split("\n")) {
            String[] parts = line.trim().split("\\s+");
            if (parts.length == 2) {
                try {
                    int uid = Integer.parseInt(parts[1]);
                    if (uid > maxUid && uid < 500) maxUid = uid;
                } catch (NumberFormatException ignored) {}
            }
        }
        return String.valueOf(maxUid + 1);
    }
}
