package com.github.alexcojocaru.mojo.elasticsearch.v2.step;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;

import com.github.alexcojocaru.mojo.elasticsearch.v2.ClusterConfiguration;
import com.github.alexcojocaru.mojo.elasticsearch.v2.ElasticsearchSetupException;

/**
 * Validate that the all provided and inferred HTTP and transport ports are unique.
 * 
 * @author Alex Cojocaru
 */
public class ValidateUniquePortsStep
        implements ClusterStep
{

    @Override
    public void execute(ClusterConfiguration config)
    {
        List<Integer> httpPorts = new ArrayList<>();
        List<Integer> transportPorts = new ArrayList<>();

        config.getInstanceConfigurationList().forEach(instanceConfig -> {
            httpPorts.add(instanceConfig.getHttpPort());
            transportPorts.add(instanceConfig.getTransportPort());
        });

        // create additional sets to verify that there are no duplicates within the source lists
        Set<Integer> httpPortsSet = new HashSet<>(httpPorts);
        Set<Integer> transportPortsSet = new HashSet<>(transportPorts);
        
        Set<Integer> intersection = new HashSet<>(httpPortsSet);
        intersection.retainAll(transportPortsSet);

        if (httpPortsSet.size() != httpPorts.size()
                || transportPortsSet.size() != transportPorts.size()
                || intersection.size() > 0)
        {

            throw new ElasticsearchSetupException(
                    "We have conflicting ports in the list of HTTP ports ["
                    + StringUtils.join(httpPorts, ',')
                    + "] and the list of transport ports ["
                    + StringUtils.join(transportPorts, ',') + "]");
        }
    }

}
