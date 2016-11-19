package com.github.alexcojocaru.mojo.elasticsearch.v2;

import org.apache.maven.plugin.logging.Log;

/**
 * Misc utilities/validators for the setup stage of an ES instance.
 *
 * @author Alex Cojocaru
 */
public final class PreConditions
{

    private PreConditions()
    {
        throw new RuntimeException("Dont call this private constructor");
    }

    public static void checkConfiguredElasticsearchVersion(final Log log,
            final String elasticsearchVersion)
    {
        if (elasticsearchVersion.matches("[0-4]\\..*"))
        {
            String errorMessage = String.format(
                    "elasticsearch-maven-plugin supports only versions 5+ of Elasticsearch. You configured: %s.",
                    elasticsearchVersion);

            log.error(errorMessage);

            throw new ElasticsearchSetupException(errorMessage);
        }
    }
}
