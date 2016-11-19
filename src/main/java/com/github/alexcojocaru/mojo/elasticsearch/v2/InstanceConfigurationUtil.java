package com.github.alexcojocaru.mojo.elasticsearch.v2;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.codehaus.plexus.util.CollectionUtils;

/**
 * Various utilities / validators for ES instance configurations.
 * 
 * @author Alex Cojocaru
 */
public final class InstanceConfigurationUtil
{

    private InstanceConfigurationUtil()
    {
        // hide constructor
    }

    public static void validatePorts(List<InstanceConfiguration> configurations)
    {
        List<Integer> httpPorts = new ArrayList<>();
        List<Integer> transportPorts = new ArrayList<>();

        configurations.forEach(config -> {
            httpPorts.add(config.getHttpPort());
            transportPorts.add(config.getTransportPort());
        });

        Set<Integer> httpPortsSet = new HashSet<>(httpPorts);
        Set<Integer> transportPortsSet = new HashSet<>(httpPorts);

        if (httpPortsSet.size() != httpPorts.size()
                || transportPortsSet.size() != transportPorts.size()
                || CollectionUtils.intersection(httpPorts, transportPorts).size() > 0)
        {

            throw new ElasticsearchSetupException(
                    "We have conflicting ports in the list of HTTP ports ["
                    + StringUtils.join(httpPorts, ',')
                    + "] and the list of transport ports ["
                    + StringUtils.join(transportPorts, ',') + "]");
        }
    }

    public static void validateInstanceCount(int instanceCount)
    {
        if (instanceCount < 1)
        {
            throw new ElasticsearchSetupException(
                    "The instanceCount property should not be smaller than 1");
        }
    }

}
