package com.github.alexcojocaru.mojo.elasticsearch.v2.step;

import com.github.alexcojocaru.mojo.elasticsearch.v2.ClusterConfiguration;
import com.github.alexcojocaru.mojo.elasticsearch.v2.ElasticsearchSetupException;
import com.github.alexcojocaru.mojo.elasticsearch.v2.InstanceConfiguration;
import com.github.alexcojocaru.mojo.elasticsearch.v2.client.ElasticsearchClient;
import com.github.alexcojocaru.mojo.elasticsearch.v2.client.ElasticsearchClientException;
import com.github.alexcojocaru.mojo.elasticsearch.v2.client.ElasticsearchCommand;
import com.github.alexcojocaru.mojo.elasticsearch.v2.client.ElasticsearchCommand.RequestMethod;
import org.apache.maven.plugin.logging.Log;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.runners.MockitoJUnitRunner;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.IntStream;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyMap;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * @author Alex Cojocaru
 *
 */
@RunWith(MockitoJUnitRunner.class)
public class BootstrapClusterStepTest
{
    @Mock
    private Log log;

    @Mock
    private ClusterConfiguration config;
    
    @Mock
    private InstanceConfiguration instanceConfig;
    
    @Mock
    private ElasticsearchClient client;
    
    @Spy
    private BootstrapClusterStep step;

    
    @Before
    public void setup()
    {
        when(config.getLog()).thenReturn(log);
        
        when(instanceConfig.getClusterConfiguration()).thenReturn(config);
        when(config.getInstanceConfigurationList()).thenReturn(Arrays.asList(instanceConfig));
    }

    @Test
    public void testExecuteWithoutFile()
    {
        when(config.getPathInitScripts()).thenReturn(new ArrayList<>());
        
        step.execute(config);
        
        verify(step, never()).parseJson(any(ElasticsearchClient.class), eq(log), any(Path.class));
        verify(step, never()).parseScript(any(ElasticsearchClient.class), eq(log), any(Path.class));
    }

    @Test
    public void testExecuteJsonFile()
    {
        String filePath = "folder/init.json";
        List<String> filePaths = Arrays.asList(filePath);
        
        when(config.getPathInitScripts()).thenReturn(filePaths);
        doNothing().when(step).validateFile(filePath);
        doNothing()
                .when(step)
                .parseJson(any(ElasticsearchClient.class), eq(log), any(Path.class));
        
        step.execute(config);
        
        verify(step).validateFile(filePath);
        
        ArgumentCaptor<Path> pathCaptor = ArgumentCaptor.forClass(Path.class);
        verify(step).parseJson(any(ElasticsearchClient.class), eq(log), pathCaptor.capture());
        assertEquals(filePath, pathCaptor.getValue().toString().replace('\\', '/'));
    }

    @Test
    public void testExecuteJsonFiles()
    {
        String filePath1 = "folder/init.json";
        String filePath2 = "folder/otherInit.json";
        List<String> filePaths = Arrays.asList(filePath1, filePath2);

        when(config.getPathInitScripts()).thenReturn(filePaths);
        doNothing().when(step).validateFile(anyString());
        doNothing()
                .when(step)
                .parseJson(any(ElasticsearchClient.class), eq(log), any(Path.class));

        step.execute(config);

        ArgumentCaptor<Path> pathCaptor = ArgumentCaptor.forClass(Path.class);
        verify(step, times(2)).parseJson(any(ElasticsearchClient.class), eq(log), pathCaptor.capture());

        List<Path> capturedPaths = pathCaptor.getAllValues();
        assertEquals(2, capturedPaths.size());
        assertNotNull(capturedPaths.get(0));
        assertTrue(capturedPaths.get(0).toString().replace('\\', '/').equalsIgnoreCase(filePath1));
        assertNotNull(capturedPaths.get(1));
        assertTrue(capturedPaths.get(1).toString().replace('\\', '/').equalsIgnoreCase(filePath2));
    }

    @Test
    public void testExecuteScriptFile()
    {
        String filePath = "folder/init.script";
        List<String> filePaths = Arrays.asList(filePath);
        
        when(config.getPathInitScripts()).thenReturn(filePaths);
        doNothing().when(step).validateFile(filePath);
        doNothing()
                .when(step)
                .parseScript(any(ElasticsearchClient.class), eq(log), any(Path.class));
        
        step.execute(config);
        
        verify(step).validateFile(filePath);
        
        ArgumentCaptor<Path> pathCaptor = ArgumentCaptor.forClass(Path.class);
        verify(step).parseScript(any(ElasticsearchClient.class), eq(log), pathCaptor.capture());
        assertEquals(filePath, pathCaptor.getValue().toString().replace('\\', '/'));
    }

    @Test
    public void testExecuteScriptFiles()
    {
        String filePath1 = "folder/init.script";
        String filePath2 = "folder/otherInit.script";
        List<String> filePaths = Arrays.asList(filePath1, filePath2);

        when(config.getPathInitScripts()).thenReturn(filePaths);
        doNothing().when(step).validateFile(anyString());
        doNothing()
                .when(step)
                .parseScript(any(ElasticsearchClient.class), eq(log), any(Path.class));

        step.execute(config);

        ArgumentCaptor<Path> pathCaptor = ArgumentCaptor.forClass(Path.class);
        verify(step, times(2)).parseScript(any(ElasticsearchClient.class), eq(log), pathCaptor.capture());

        List<Path> capturedPaths = pathCaptor.getAllValues();
        assertEquals(2, capturedPaths.size());
        assertNotNull(capturedPaths.get(0));
        assertTrue(capturedPaths.get(0).toString().replace('\\', '/').equalsIgnoreCase(filePath1));
        assertNotNull(capturedPaths.get(1));
        assertTrue(capturedPaths.get(1).toString().replace('\\', '/').equalsIgnoreCase(filePath2));
    }
    
    @Test(expected = ElasticsearchSetupException.class)
    public void testValidateFile()
    {
        step.validateFile("not_a_file");
    }
    
    @SuppressWarnings("unchecked")
    @Test
    public void testParseJson()
    {
        String jsonFile = "src/test/resources/init.json";
        
        // there are 2 requests in the json file;
        // mock two ElasticsearchCommand objects to be returned for each
        ElasticsearchCommand esCommand1 = mock(ElasticsearchCommand.class);
        ElasticsearchCommand esCommand2 = mock(ElasticsearchCommand.class);
        doReturn(esCommand1, esCommand2).when(step).parseMapCommand(anyMap());

        doNothing().when(step).executeInitCommand(eq(client), eq(log), eq(esCommand1));
        doNothing().when(step).executeInitCommand(eq(client), eq(log), eq(esCommand2));
        
        step.parseJson(client, log, Paths.get(jsonFile));
        
        ArgumentCaptor<Map<String, Object>> captor = ArgumentCaptor.forClass(Map.class);
        verify(step, times(2)).parseMapCommand(captor.capture());
        
        // verify the first argument (ie. the first request in the json)
        Map<String, Object> command1 = captor.getAllValues().get(0);
        assertEquals(3, command1.size());
        assertEquals("PUT", command1.get("method"));
        assertEquals("load_test_index/test_type/1", command1.get("path"));
        assertTrue(command1.get("payload") instanceof Map);
        assertEquals(1, ((Map<String, String>)command1.get("payload")).size());
        assertEquals("alex", ((Map<String, String>)command1.get("payload")).get("name"));
        
        // verify the second argument (ie. the second request in the json)
        Map<String, Object> command2 = captor.getAllValues().get(1);
        assertEquals(2, command2.size());
        assertEquals("POST", command2.get("method"));
        assertEquals("load_test_index/_refresh", command2.get("path"));

        verify(step).executeInitCommand(client, log, esCommand1);
        verify(step).executeInitCommand(client, log, esCommand2);
    }
    
    @Test(expected = ElasticsearchSetupException.class)
    public void testParseJsonInvalidFile()
    {
        step.parseJson(client, log, Paths.get("not_a_file"));
    }

    
    @Test
    public void testParseMapCommand()
    {
        Map<String, Object> command = new HashMap<>();
        command.put("method", "PUT");
        command.put("path", "index_name");
        
        Map<String, String> payload = new HashMap<>();
        payload.put("attribute1", "value1");
        payload.put("attribute2", "value2");
        command.put("payload", payload);
        
        ElasticsearchCommand esCommand = step.parseMapCommand(command);
        
        assertEquals(RequestMethod.PUT, esCommand.getRequestMethod());
        assertEquals("index_name", esCommand.getRelativeUrl());
        assertEquals("{\"attribute1\":\"value1\",\"attribute2\":\"value2\"}", esCommand.getJson());
    }
    
    @Test(expected = IllegalArgumentException.class)
    public void testParseMapCommandUnknownMethodName()
    {
        Map<String, Object> command = new HashMap<>();
        command.put("method", "PATCH");
        command.put("path", "index_name");
        command.put("payload", new HashMap<>());
        
        step.parseMapCommand(command);
    }
    
    @Test
    public void testParseMapCommandDelete()
    {
        Map<String, Object> command = new HashMap<>();
        command.put("method", "DELETE");
        command.put("path", "index_name");
        
        ElasticsearchCommand esCommand = step.parseMapCommand(command);
        
        assertEquals(RequestMethod.DELETE, esCommand.getRequestMethod());
        assertEquals("index_name", esCommand.getRelativeUrl());
        assertNull(esCommand.getJson());
    }
    
    @Test(expected = IllegalArgumentException.class)
    public void testParseMapCommandDeleteWithPayload()
    {
        Map<String, Object> command = new HashMap<>();
        command.put("method", "DELETE");
        command.put("path", "index_name");
        command.put("payload", new HashMap<>());
        
        step.parseMapCommand(command);
    }
    
    @Test
    public void testParseScript()
    {
        String scriptFile = "src/test/resources/init.script";
        
        // there are 4 requests in the json file;
        // mock four ElasticsearchCommand objects to be returned for each
        ElasticsearchCommand[] esCommands = IntStream.rangeClosed(1, 4)
                .mapToObj(i -> {
                    ElasticsearchCommand esCommand = mock(ElasticsearchCommand.class);

                    // tell the step to do nothing when it's told to execute this
                    doNothing().when(step).executeInitCommand(eq(client), eq(log), eq(esCommand));

                    return esCommand;
                })
                .toArray(ElasticsearchCommand[]::new);
        
        // return the corresponding mock command for the given string command
        doReturn(esCommands[0])
            .when(step)
            .parseStringCommand("PUT:load_test_index:{ \"settings\" : { \"number_of_shards\" : 1, \"number_of_replicas\" : 0 } }");
        doReturn(esCommands[1])
            .when(step)
            .parseStringCommand("PUT:load_test_index/test_type/1:{ \"name\" : \"alex\" }");
        doReturn(esCommands[2])
            .when(step)
            .parseStringCommand("DELETE:load_test_index/test_type/2:");
        doReturn(esCommands[3])
            .when(step)
            .parseStringCommand("POST:load_test_index/_refresh:");
        
        // there are another 8 comments and empty lines, mock all with the same mock skip command
        ElasticsearchCommand emptyCommand = mock(ElasticsearchCommand.class);
        when(emptyCommand.isSkip()).thenReturn(true);
        doReturn(emptyCommand).when(step).parseStringCommand("# create the index");
        doReturn(emptyCommand).when(step).parseStringCommand("# the index name is hardcoded");
        doReturn(emptyCommand).when(step).parseStringCommand("# index a document");
        doReturn(emptyCommand).when(step).parseStringCommand("# delete the 2nd document");
        doReturn(emptyCommand).when(step).parseStringCommand("# refresh the index");
        
        step.parseScript(client, log, Paths.get(scriptFile));
         
        // the parse method should have been called for all string commands, in order
        ArgumentCaptor<String> captor = ArgumentCaptor.forClass(String.class);
        verify(step, times(12)).parseStringCommand(captor.capture());

        // the exec method should have been called with all commands, in order
        Arrays.stream(esCommands).forEach(esCommand ->
        {
            verify(step).executeInitCommand(client, log, esCommand);
        });
    }
    
    @Test
    public void parseStringCommand()
    {
        String command = "PUT:index/type/id:{}";
        
        ElasticsearchCommand esCommand = step.parseStringCommand(command);
        
        assertFalse(esCommand.isSkip());
        assertEquals(RequestMethod.PUT, esCommand.getRequestMethod());
        assertEquals("index/type/id", esCommand.getRelativeUrl());
        assertEquals("{}", esCommand.getJson());
    }
    
    @Test
    public void parseStringCommandWithSpaces()
    {
        String command = " PUT  : index/type/id  : { \"name\" : \"value\" }  ";
        
        ElasticsearchCommand esCommand = step.parseStringCommand(command);
        
        assertFalse(esCommand.isSkip());
        assertEquals(RequestMethod.PUT, esCommand.getRequestMethod());
        assertEquals("index/type/id", esCommand.getRelativeUrl());
        assertEquals("{ \"name\" : \"value\" }", esCommand.getJson());
    }
    
    @Test
    public void parseStringCommandEmptyJson()
    {
        String command = "PUT:index/type/id:";
        
        ElasticsearchCommand esCommand = step.parseStringCommand(command);
        
        assertFalse(esCommand.isSkip());
        assertEquals(RequestMethod.PUT, esCommand.getRequestMethod());
        assertEquals("index/type/id", esCommand.getRelativeUrl());
        assertEquals("", esCommand.getJson());
    }
    
    @Test
    public void parseStringCommandSkip()
    {
        String command = " # comment";
        
        ElasticsearchCommand esCommand = step.parseStringCommand(command);
        
        assertTrue(esCommand.isSkip());
    }
    
    @Test(expected = ElasticsearchSetupException.class)
    public void parseStringCommandWith1Segment()
    {
        step.parseStringCommand("PUT");
    }
    
    @Test(expected = ElasticsearchSetupException.class)
    public void parseStringCommandWith2Segments()
    {
        step.parseStringCommand("PUT:index");
    }
    
    @Test(expected = IllegalArgumentException.class)
    public void parseStringCommandInvalidRequestMethod()
    {
        step.parseStringCommand("PAT:index:{}");
    }
    
    @Test
    public void testExecuteInitCommandPut() throws ElasticsearchClientException {
        ElasticsearchCommand command = mock(ElasticsearchCommand.class);
        when(command.getRequestMethod()).thenReturn(RequestMethod.PUT);
        when(command.getJson()).thenReturn("json");
        when(command.getRelativeUrl()).thenReturn("index/type/id");
        
        step.executeInitCommand(client, log, command);
        
        verify(client).put("/index/type/id", "json");
    }
    
    @Test
    public void testExecuteInitCommandPost() throws ElasticsearchClientException {
        ElasticsearchCommand command = mock(ElasticsearchCommand.class);
        when(command.getRequestMethod()).thenReturn(RequestMethod.POST);
        when(command.getJson()).thenReturn("json");
        when(command.getRelativeUrl()).thenReturn("index/type/id");
        
        step.executeInitCommand(client, log, command);
        
        verify(client).post("/index/type/id", "json", String.class);
    }
    
    @Test
    public void testExecuteInitCommandDelete() throws ElasticsearchClientException {
        ElasticsearchCommand command = mock(ElasticsearchCommand.class);
        when(command.getRequestMethod()).thenReturn(RequestMethod.DELETE);
        when(command.getRelativeUrl()).thenReturn("index/type/id");
        
        step.executeInitCommand(client, log, command);
        
        verify(client).delete("/index/type/id");
    }
    
    @Test(expected = ElasticsearchSetupException.class)
    public void testExecuteInitCommandWithException() throws ElasticsearchClientException {
        ElasticsearchCommand command = mock(ElasticsearchCommand.class);
        when(command.getRequestMethod()).thenReturn(RequestMethod.DELETE);
        when(command.getRelativeUrl()).thenReturn("index/type/id");
        
        doThrow(ElasticsearchClientException.class).when(client).delete("/index/type/id");
        
        step.executeInitCommand(client, log, command);
    }

}
