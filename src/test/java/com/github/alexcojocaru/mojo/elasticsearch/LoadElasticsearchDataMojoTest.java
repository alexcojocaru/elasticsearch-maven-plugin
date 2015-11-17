package com.github.alexcojocaru.mojo.elasticsearch;

import java.io.File;
import java.util.HashMap;

import org.apache.maven.plugin.testing.AbstractMojoTestCase;

import static org.codehaus.plexus.PlexusTestCase.getBasedir;

/**
 * @author alexcojocaru
 *
 */
public class LoadElasticsearchDataMojoTest extends AbstractMojoTestCase
{

    private ElasticsearchNode elasticsearchNode;

    private LoadElasticsearchDataMojo mojo;

    @Override
    protected void setUp() throws Exception
    {
        super.setUp();
        
        String dataPath = new File("target/test-harness/elasticsearch-data").getAbsolutePath();
        this.elasticsearchNode = ElasticsearchNode.start(dataPath);

        //Configure mojo with context
        File testPom = new File(getBasedir(), "src/test/resources/goals/load/pom.xml");
        mojo = (LoadElasticsearchDataMojo)lookupMojo("load", testPom);
        mojo.setPluginContext(new HashMap());
        mojo.getPluginContext().put("test", elasticsearchNode);
    }
    
    @Override
    protected void tearDown() throws Exception
    {
        super.tearDown();
        this.elasticsearchNode.stop();
    }
    
    public void testMojoLookup() throws Exception
    {
        assertNotNull(mojo);
    }
    
    public void testMojoExecution() throws Exception
    {
        mojo.execute();
    }

}
