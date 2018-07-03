package com.github.alexcojocaru.mojo.elasticsearch.v2;

/**
 * @author Alex Cojocaru
 */
public class ElasticsearchArtifact
        extends AbstractArtifact
{
    public static final String ELASTICSEARCH_GROUPID = "com.github.alexcojocaru";
    public static final String ELASTICSEARCH_TYPE = "zip";

    public ElasticsearchArtifact(final String artifactId, final String version)
    {
        this(ELASTICSEARCH_GROUPID, artifactId, version, ELASTICSEARCH_TYPE);
    }

    public ElasticsearchArtifact(
            final String groupId,
            final String artifactId,
            final String version,
            final String type)
    {
        super(groupId, artifactId, version, null, type);
    }

    @Override
    public String getType()
    {
        return ELASTICSEARCH_TYPE;
    }

    @Override
    public String toString()
    {
        return "ElasticsearchArtifact[" + super.getArtifactCoordinates() + "]";
    }

}
