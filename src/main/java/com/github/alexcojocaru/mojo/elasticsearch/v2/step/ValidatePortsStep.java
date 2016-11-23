package com.github.alexcojocaru.mojo.elasticsearch.v2.step;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;

import com.github.alexcojocaru.mojo.elasticsearch.v2.ClusterConfiguration;
import com.github.alexcojocaru.mojo.elasticsearch.v2.ElasticsearchSetupException;

/**
 * Validate that the provided and inferred HTTP and transport ports are not protected
 * (ie. less than 1024).
 * 
 * @author Alex Cojocaru
 */
public class ValidatePortsStep
        implements ClusterStep
{

    @Override
    public void execute(ClusterConfiguration config)
    {
        List<Integer> ports = new ArrayList<>();

        // Iterate twice, because I want to maintain the order:
        // HTTP ports first, then transport ports
        config.getInstanceConfigurationList().forEach(instanceConfig -> {
            ports.add(instanceConfig.getHttpPort());
        });
        config.getInstanceConfigurationList().forEach(instanceConfig -> {
            ports.add(instanceConfig.getTransportPort());
        });
        
        List<Integer> protectedPorts = ports.stream()
                .filter(port -> port < 1024)
                .collect(Collectors.toList());

        if (protectedPorts.size() > 0)
        {
            throw new ElasticsearchSetupException(String.format(
                    "The following provided or inferred ports are protected (below 1024): %s",
                    StringUtils.join(protectedPorts, ',')));
        }
    }

}
