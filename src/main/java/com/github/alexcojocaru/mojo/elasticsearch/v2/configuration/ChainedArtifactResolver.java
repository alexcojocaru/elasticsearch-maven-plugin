/**
 * Copyright (C) 2010-2012 Joerg Bellmann <joerg.bellmann@googlemail.com>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.github.alexcojocaru.mojo.elasticsearch.v2.configuration;

import java.io.File;
import java.util.LinkedList;
import java.util.List;

import org.apache.commons.lang3.Validate;

/**
 * Copied from the t7mp project.
 * 
 * @author Joerg Bellmann
 *
 */
public class ChainedArtifactResolver
        implements PluginArtifactResolver
{

    protected List<PluginArtifactResolver> resolverChain = new LinkedList<PluginArtifactResolver>();

    public ChainedArtifactResolver()
    {
        this.resolverChain.add(new SystemPathArtifactResolver());
    }

    @Override
    public File resolveArtifact(final String coordinates) throws ArtifactException
    {
        File result = null;
        for (PluginArtifactResolver resolver : resolverChain)
        {
            try
            {
                result = resolver.resolveArtifact(coordinates);
                if (result != null)
                {
                    break;
                }
                // CHECKSTYLE:OFF: Empty catch block
            }
            catch (ArtifactException e)
            {
            }
            // CHECKSTYLE:ON: Empty catch block
        }
        if (result == null)
        {
            throw new ArtifactException(
                    "Could not resolve artifact with coordinates " + coordinates);
        }
        return result;
    }

    public void addPluginArtifactResolver(PluginArtifactResolver pluginArtifactResolver)
    {
        Validate.notNull(pluginArtifactResolver, "PluginArtifactResolvers should not be null");
        this.resolverChain.add(pluginArtifactResolver);
    }

}
