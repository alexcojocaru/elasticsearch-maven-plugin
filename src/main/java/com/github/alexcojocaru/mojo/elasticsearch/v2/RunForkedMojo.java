package com.github.alexcojocaru.mojo.elasticsearch.v2;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;

import com.github.alexcojocaru.mojo.elasticsearch.v2.step.PostStartClusterSequence;
import com.github.alexcojocaru.mojo.elasticsearch.v2.step.PostStartInstanceSequence;
import com.github.alexcojocaru.mojo.elasticsearch.v2.step.PreStartClusterSequence;

/**
 * The main plugin mojo to start a forked ES instances.
 * 
 * @author Alex Cojocaru
 */
@Mojo(name = "runforked", defaultPhase = LifecyclePhase.PRE_INTEGRATION_TEST, threadSafe = true)
public class RunForkedMojo
        extends AbstractElasticsearchMojo
{
    @Override
    public void execute() throws MojoExecutionException, MojoFailureException
    {
        if (skip)
        {
            getLog().info("Skipping plugin execution");
            return;
        }
        
        ClusterConfiguration clusterConfig = buildClusterConfiguration();
        new PreStartClusterSequence().execute(clusterConfig);

        for (InstanceConfiguration config : clusterConfig.getInstanceConfigurationList())
        {
            getLog().info(String.format(
                    "Using Elasticsearch [%d] configuration: %s",
                    config.getId(),
                    config));

            try
            {
                ForkedInstance instance = new ForkedInstance(config);
                instance.configureInstance();

                Thread thread = new Thread(instance);
                thread.start();

                new PostStartInstanceSequence().execute(config);
            }
            catch (Exception e)
            {
                throw new MojoExecutionException(e.getMessage(), e);
            }
        }

        new PostStartClusterSequence().execute(clusterConfig);
    }

}
