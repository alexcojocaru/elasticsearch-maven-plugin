package com.github.alexcojocaru.mojo.elasticsearch.v2;

import com.github.alexcojocaru.mojo.elasticsearch.v2.step.PostStartClusterSequence;
import com.github.alexcojocaru.mojo.elasticsearch.v2.step.PostStartInstanceSequence;
import com.github.alexcojocaru.mojo.elasticsearch.v2.step.PreStartClusterSequence;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;

/**
 * A mojo to start cluster nodes using specified configuration values.
 * <p>
 * Mostly useful for debugging purposes and local runs of tests during development.
 *
 * @author Askar Akhmerov
 */
@Mojo(name = "run", defaultPhase = LifecyclePhase.PRE_INTEGRATION_TEST)
public class RunMojo
    extends AbstractElasticsearchMojo {
  @Override
  public void execute() throws MojoExecutionException, MojoFailureException {
    if (skip) {
      getLog().info("Skipping plugin execution");
      return;
    }

    ClusterConfiguration clusterConfig = buildClusterConfiguration();
    new PreStartClusterSequence().execute(clusterConfig);

    for (InstanceConfiguration config : clusterConfig.getInstanceConfigurationList()) {
      getLog().info(String.format(
          "Using Elasticsearch [%d] configuration: %s",
          config.getId(),
          config));

      try {
        ForkedInstance instance = new ForkedInstance(config);
        instance.configureInstance();

        Thread thread = new Thread(instance);
        thread.start();

        new PostStartInstanceSequence().execute(config);

        thread.join();
      } catch (Exception e) {
        throw new MojoExecutionException(e.getMessage(), e);
      }
    }

    new PostStartClusterSequence().execute(clusterConfig);
  }

}
