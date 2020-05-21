package com.github.alexcojocaru.mojo.elasticsearch.v2;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.List;

import static org.junit.Assert.assertEquals;

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
}
