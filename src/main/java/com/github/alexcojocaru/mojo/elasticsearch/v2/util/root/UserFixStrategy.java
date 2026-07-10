package com.github.alexcojocaru.mojo.elasticsearch.v2.util.root;

import org.apache.maven.plugin.logging.Log;

public interface UserFixStrategy {
    boolean isSupported();
    boolean isRunningAsRoot();
    boolean userExists(String username) throws Exception;
    void createUser(String username, Log log) throws Exception;
    void grantPermissions(String username, String baseDir, Log log) throws Exception;
    void revokePermissions(String username, String baseDir, Log log) throws Exception;
    void deleteUser(String username, Log log) throws Exception;
}
