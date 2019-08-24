package com.github.alexcojocaru.mojo.elasticsearch.v2;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.List;

import static org.junit.Assert.assertEquals;

@RunWith(MockitoJUnitRunner.class)
public class AbstractElasticsearchMojoTest {

    private final AbstractElasticsearchMojo abstractElasticsearchMojo = Mockito.mock(
                                                                  AbstractElasticsearchMojo.class,
                                                                  Mockito.CALLS_REAL_METHODS);

    @Test
    public void testGetPathInitScriptsNullInput()
    {
        abstractElasticsearchMojo.setPathInitScripts(null);
        final List<String> pathInitScripts = abstractElasticsearchMojo.getPathInitScripts();
        assertEquals(0, pathInitScripts.size());
    }

    @Test
    public void testGetPathInitScriptsEmptyInput()
    {
        abstractElasticsearchMojo.setPathInitScripts("");
        final List<String> pathInitScripts = abstractElasticsearchMojo.getPathInitScripts();
        assertEquals(0, pathInitScripts.size());
    }

    @Test
    public void testGetPathInitScriptsSingleInputTrimmed()
    {
        final String script = "script.json";

        abstractElasticsearchMojo.setPathInitScripts(script);
        final List<String> pathInitScripts = abstractElasticsearchMojo.getPathInitScripts();
        assertEquals(1, pathInitScripts.size());
        assertEquals(script, pathInitScripts.get(0));
    }

    @Test
    public void testGetPathInitScriptsSingleInputNotTrimmed()
    {
        final String script = "    script.json    ";

        abstractElasticsearchMojo.setPathInitScripts(script);
        final List<String> pathInitScripts = abstractElasticsearchMojo.getPathInitScripts();
        assertEquals(1, pathInitScripts.size());
        assertEquals(script.trim(), pathInitScripts.get(0));
    }

    @Test
    public void testGetPathInitScriptsMultipleInputTrimmed()
    {
        final String script1 = "script.json";
        final String script2 = "other.script";

        abstractElasticsearchMojo.setPathInitScripts(script1 + "," + script2);
        final List<String> pathInitScripts = abstractElasticsearchMojo.getPathInitScripts();
        assertEquals(2, pathInitScripts.size());
        assertEquals(script1, pathInitScripts.get(0));
        assertEquals(script2, pathInitScripts.get(1));
    }

    @Test
    public void testGetPathInitScriptsMultipleInputNotTrimmed()
    {
        final String script1 = "script.json";
        final String script2 = "other.script";

        abstractElasticsearchMojo.setPathInitScripts(script1 + " , \n" + script2 + "\n");
        final List<String> pathInitScripts = abstractElasticsearchMojo.getPathInitScripts();
        assertEquals(2, pathInitScripts.size());
        assertEquals(script1, pathInitScripts.get(0));
        assertEquals(script2, pathInitScripts.get(1));
    }
}
