package com.github.alexcojocaru.mojo.elasticsearch.v2;

import org.apache.commons.lang3.Validate;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;

import com.github.alexcojocaru.mojo.elasticsearch.v2.step.PostStartInstanceSequence;

/**
 * The main plugin mojo which starts forked ES instances.
 * 
 * @author Alex Cojocaru
 */
@Mojo(name = "runforked", defaultPhase = LifecyclePhase.PRE_INTEGRATION_TEST)
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

        PluginContext context = buildContext();

        for (InstanceConfiguration config : context.getConfigurationList())
        {
            Validate.notNull(this.getVersion(), "The Elasticsearch version is required");
            PreConditions.checkConfiguredElasticsearchVersion(getLog(), this.getVersion());

            getLog().info(String.format("Starting Elasticsearch configuration: %s", config));

            try
            {
                InstanceContext instanceContext = new InstanceContext(config,
                        context.getArtifactResolver(), context.getLog());

                ForkedInstance instance = new ForkedInstance(instanceContext);
                instance.configureInstance();

                Thread thread = new Thread(instance);
                thread.start();

                new PostStartInstanceSequence().execute(instanceContext);
            }
            catch (Exception e)
            {
                throw new MojoExecutionException(e.getMessage(), e);
            }
        }

        if (isSetAwait())
        {
            new ExecutionLock(getLog()).lock();
        }
    }

}
