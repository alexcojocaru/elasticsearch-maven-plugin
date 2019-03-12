package com.github.alexcojocaru.mojo.elasticsearch.v2;

/**
 * @author Alex Cojocaru
 */
public class ElasticsearchArtifact
        extends AbstractArtifact
{
    public static final String ELASTICSEARCH_GROUPID = "com.github.alexcojocaru";

    public ElasticsearchArtifact(
            final String artifactId,
            final String version,
            final String classifier,
            final String type)
    {
        super(ELASTICSEARCH_GROUPID, artifactId, version, classifier, type);
    }

    @Override
    public String getType()
    {
        return type;
    }

    @Override
    public String toString()
    {
        return "ElasticsearchArtifact[" + super.getArtifactCoordinates() + "]";
    }

}
