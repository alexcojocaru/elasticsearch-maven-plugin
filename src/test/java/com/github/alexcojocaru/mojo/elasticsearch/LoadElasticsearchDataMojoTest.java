package com.github.alexcojocaru.mojo.elasticsearch;

import java.io.File;

import org.apache.maven.plugin.testing.AbstractMojoTestCase;

/**
 * @author alexcojocaru
 *
 */
public class LoadElasticsearchDataMojoTest extends AbstractMojoTestCase
{

    @Override
    protected void setUp() throws Exception
    {
        super.setUp();
        
        String dataPath = new File("target/test-harness/elasticsearch-data").getAbsolutePath();
        ElasticSearchNode.start(dataPath);
    }
    
    @Override
    protected void tearDown() throws Exception
    {
        super.tearDown();

        ElasticSearchNode.stop();
    }
    
    public void testMojoLookup() throws Exception
    {
        File testPom = new File(getBasedir(), "src/test/resources/goals/load/pom.xml");
        
        LoadElasticsearchDataMojo mojo = (LoadElasticsearchDataMojo)lookupMojo("load", testPom);
 
        assertNotNull(mojo);
    }
    
    public void testMojoExecution() throws Exception
    {
        File testPom = new File(getBasedir(), "src/test/resources/goals/load/pom.xml");
        LoadElasticsearchDataMojo mojo = (LoadElasticsearchDataMojo)lookupMojo("load", testPom);
        assertNotNull(mojo);
        mojo.execute();
        
    }

}
