package com.github.alexcojocaru.mojo.elasticsearch;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import org.apache.maven.plugin.testing.AbstractMojoTestCase;

import com.github.alexcojocaru.mojo.elasticsearch.NetUtil.ElasticsearchPort;
import org.elasticsearch.action.admin.indices.delete.DeleteIndexRequest;
import org.elasticsearch.action.admin.indices.exists.indices.IndicesExistsRequest;

/**
 * @author alexcojocaru
 *
 */
public class LoadElasticsearchDataMojoTest extends AbstractMojoTestCase
{

    private ElasticsearchNode elasticsearchNode;

    private LoadElasticsearchDataMojo mojo;
    
    private final static String LOAD_INDEX_NAME = "load_test_index";

    @Override
    protected void setUp() throws Exception
    {
        super.setUp();

        File testPom = new File(getBasedir(), "src/test/resources/goals/load/pom.xml");
        
        String dataPath = new File("target/test-harness/elasticsearch-data").getAbsolutePath();

        Map<ElasticsearchPort, Integer> esPorts = NetUtil.findOpenPortsForElasticsearch();
        int httpPort = esPorts.get(ElasticsearchPort.HTTP);
        int tcpPort = esPorts.get(ElasticsearchPort.TCP);

        this.elasticsearchNode = ElasticsearchNode.start(dataPath, httpPort, tcpPort);

        //Configure mojo with context
        mojo = (LoadElasticsearchDataMojo)lookupMojo("load", testPom);
        mojo.setPluginContext(new HashMap<>());
        mojo.getPluginContext().put("test", elasticsearchNode);
        
        // I cannot find another way of setting the two required propperties at run time.
        mojo.httpPort = esPorts.get(ElasticsearchPort.HTTP);
    }
    
    @Override
    protected void tearDown() throws Exception
    {
        super.tearDown();

        if (elasticsearchNode != null && !elasticsearchNode.isClosed())
        {
            // if the load test index was created by the test, delete it,
            // and wait until the delete finalizes
            if (isExists())
            {
                elasticsearchNode.getClient().admin().indices().delete(
                        new DeleteIndexRequest(LOAD_INDEX_NAME)).get();
            }

            this.elasticsearchNode.stop();
        }
    }
    
    private boolean isExists() throws Exception
    {
        return elasticsearchNode.getClient().admin().indices().exists(
                        new IndicesExistsRequest(LOAD_INDEX_NAME)).get().isExists();
    }
    
    public void testMojoLookup() throws Exception
    {
        assertNotNull(mojo);
    }
    
    public void testMojoExecution() throws Exception
    {
        mojo.execute();

        assertTrue(isExists());
    }

    public void testMojoExecutionIsSkipped() throws Exception
    {
        mojo.skip = true;
        mojo.execute();

        assertFalse(isExists());
    }

}
