package com.github.alexcojocaru.mojo.elasticsearch.v2;

import org.apache.maven.execution.MavenSession;
import org.apache.maven.toolchain.Toolchain;
import org.apache.maven.toolchain.ToolchainManager;
import org.apache.maven.toolchain.java.JavaToolchainImpl;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static java.util.Collections.emptyList;
import static java.util.Collections.emptyMap;
import static java.util.Collections.singletonList;
import static java.util.Collections.singletonMap;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class AbstractElasticsearchMojoTest {

    private AbstractElasticsearchMojo abstractElasticsearchMojo;

    @Before
    public void setup()
    {
        abstractElasticsearchMojo = Mockito.mock(
                AbstractElasticsearchMojo.class,
                Mockito.CALLS_REAL_METHODS);
    }

    @Test
    public void testGetPathInitScriptNullInput()
    {
        abstractElasticsearchMojo.setPathInitScript(null);
        final List<String> pathInitScripts = abstractElasticsearchMojo.getPathInitScript();
        assertEquals(0, pathInitScripts.size());
    }

    @Test
    public void testGetPathInitScriptEmptyInput()
    {
        abstractElasticsearchMojo.setPathInitScript("");
        final List<String> pathInitScripts = abstractElasticsearchMojo.getPathInitScript();
        assertEquals(0, pathInitScripts.size());
    }

    @Test
    public void testGetPathInitScriptSingleInputTrimmed()
    {
        final String script = "script.json";

        abstractElasticsearchMojo.setPathInitScript(script);
        final List<String> pathInitScripts = abstractElasticsearchMojo.getPathInitScript();
        assertEquals(1, pathInitScripts.size());
        assertEquals(script, pathInitScripts.get(0));
    }

    @Test
    public void testGetPathInitScriptSingleInputNotTrimmed()
    {
        final String script = "    script.json    ";

        abstractElasticsearchMojo.setPathInitScript(script);
        final List<String> pathInitScripts = abstractElasticsearchMojo.getPathInitScript();
        assertEquals(1, pathInitScripts.size());
        assertEquals(script.trim(), pathInitScripts.get(0));
    }

    @Test
    public void testGetPathInitScriptMultipleInputTrimmed()
    {
        final String script1 = "script.json";
        final String script2 = "other.script";

        abstractElasticsearchMojo.setPathInitScript(script1 + "," + script2);
        final List<String> pathInitScripts = abstractElasticsearchMojo.getPathInitScript();
        assertEquals(2, pathInitScripts.size());
        assertEquals(script1, pathInitScripts.get(0));
        assertEquals(script2, pathInitScripts.get(1));
    }

    @Test
    public void testGetPathInitScriptMultipleInputNotTrimmed()
    {
        final String script1 = "script.json";
        final String script2 = "other.script";

        abstractElasticsearchMojo.setPathInitScript(script1 + " , \n" + script2 + "\n");
        final List<String> pathInitScripts = abstractElasticsearchMojo.getPathInitScript();
        assertEquals(2, pathInitScripts.size());
        assertEquals(script1, pathInitScripts.get(0));
        assertEquals(script2, pathInitScripts.get(1));
    }

    @Test(expected = IllegalStateException.class)
    public void testDownloadUsernameButNoPassword() {
        abstractElasticsearchMojo.downloadUrlUsername = "foo";
        abstractElasticsearchMojo.buildClusterConfiguration();
    }

    @Test(expected = IllegalStateException.class)
    public void testDownloadPasswordButNoUsername() {
        abstractElasticsearchMojo.downloadUrlPassword = "bar";
        abstractElasticsearchMojo.buildClusterConfiguration();
    }

    @Test
    public void testDownloadUsernameAndPassword() {
        final String username = "foo";
        final String password = "bar";
        abstractElasticsearchMojo.downloadUrlUsername = username;
        abstractElasticsearchMojo.downloadUrlPassword = password;
        abstractElasticsearchMojo.logLevel = "INFO";
        final ClusterConfiguration configuration = abstractElasticsearchMojo.buildClusterConfiguration();
        assertEquals(username, configuration.getDownloadUrlUsername());
        assertEquals(password, configuration.getDownloadUrlPassword());
    }

    @Test
    public void testPrepareEnvironmentVariablesBothSet() {
        Map<String, String> jdkToolchain = new HashMap<>();
        jdkToolchain.put("version", "11");
        jdkToolchain.put("vendor", "sun");

        Map<String, String> environmentVariables = singletonMap("JAVA_HOME", "/path/to/jdk11");

        ToolchainManager toolchainManager = mock(ToolchainManager.class);
        MavenSession session = mock(MavenSession.class);

        assertThrows(IllegalArgumentException.class, () -> abstractElasticsearchMojo.prepareEnvironmentVariables(
                session, toolchainManager, jdkToolchain, environmentVariables
        ));
    }

    @Test
    public void testPrepareEnvironmentVariablesToolchainsEmpty() {
        Map<String, String> jdkToolchain = new HashMap<>();
        jdkToolchain.put("version", "11");
        jdkToolchain.put("vendor", "sun");

        Map<String, String> environmentVariables = emptyMap();

        ToolchainManager toolchainManager = mock(ToolchainManager.class);
        MavenSession session = mock(MavenSession.class);

        when(toolchainManager.getToolchains(session, "jdk", jdkToolchain)).thenReturn(emptyList());

        assertThrows(IllegalArgumentException.class, () -> abstractElasticsearchMojo.prepareEnvironmentVariables(
                session, toolchainManager, jdkToolchain, environmentVariables));
    }

    @Test
    public void testPrepareEnvironmentVariablesFromToolchain() {
        Map<String, String> jdkToolchain = new HashMap<>();
        jdkToolchain.put("version", "11");
        jdkToolchain.put("vendor", "sun");

        Map<String, String> environmentVariables = emptyMap();

        ToolchainManager toolchainManager = mock(ToolchainManager.class);
        MavenSession session = mock(MavenSession.class);
        JavaToolchainImpl toolchain = mock(JavaToolchainImpl.class);

        when(toolchainManager.getToolchains(session, "jdk", jdkToolchain)).thenReturn(singletonList(toolchain));
        when(toolchain.getJavaHome()).thenReturn("/path/to/jdk11");

        Map<String, String> preparedEnvironmentVariables = abstractElasticsearchMojo.prepareEnvironmentVariables(
                session, toolchainManager, jdkToolchain, environmentVariables
        );
        assertEquals("/path/to/jdk11", preparedEnvironmentVariables.get("JAVA_HOME"));
    }

    @Test
    public void testPrepareEnvironmentVariablesFromEnvVar() {
        Map<String, String> jdkToolchain = null;

        Map<String, String> environmentVariables = singletonMap("JAVA_HOME", "/path/to/jdk8");

        ToolchainManager toolchainManager = mock(ToolchainManager.class);
        MavenSession session = mock(MavenSession.class);

        Map<String, String> preparedEnvironmentVariables = abstractElasticsearchMojo.prepareEnvironmentVariables(
                session, toolchainManager, jdkToolchain, environmentVariables
        );
        assertEquals("/path/to/jdk8", preparedEnvironmentVariables.get("JAVA_HOME"));
    }
}
