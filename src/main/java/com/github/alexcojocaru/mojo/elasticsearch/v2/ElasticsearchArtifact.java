package com.github.alexcojocaru.mojo.elasticsearch.v2;

/**
 * @author Alex Cojocaru
 */
public class ElasticsearchArtifact
        extends AbstractArtifact
{
    public static final String DEFAULT_ELASTICSEARCH_VERSION = "6.0.0-beta2";
    public static final String ELASTICSEARCH_GROUPID = "org.elasticsearch.distribution.zip";
    public static final String ELASTICSEARCH_ARTIFACTID = "elasticsearch";
    public static final String ELASTICSEARCH_TYPE = "zip";

    public ElasticsearchArtifact()
    {
        this(DEFAULT_ELASTICSEARCH_VERSION);
    }

    public ElasticsearchArtifact(final String version)
    {
        this(ELASTICSEARCH_GROUPID, ELASTICSEARCH_ARTIFACTID, version, ELASTICSEARCH_TYPE);
    }

    public ElasticsearchArtifact(final String groupId, final String artifactId,
            final String version, final String type)
    {
        super(groupId, artifactId, version, null, type);
    }

    @Override
    public String getType()
    {
        return "zip";
    }

    @Override
    public String toString()
    {
        return "ElasticsearchArtifact[" + super.getArtifactCoordinates() + "]";
    }

}
