package com.github.alexcojocaru.mojo.elasticsearch.v2;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.SystemUtils;

import com.github.alexcojocaru.mojo.elasticsearch.v2.util.VersionUtil;
import com.google.common.base.Joiner;

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

    public String buildBundleFilename()
    {
        return Joiner
                .on("-")
                .skipNulls()
                .join(getArtifactId(), getVersion(), getClassifier() /* May be null */)
                + "." + getType();
    }

    @Override
    public String toString()
    {
        return "ElasticsearchArtifact[" + super.getArtifactCoordinates() + "]";
    }


    public static class ElasticsearchArtifactBuilder
    {
        private ClusterConfiguration clusterConfig;

        public ElasticsearchArtifactBuilder withClusterConfig(ClusterConfiguration clusterConfig)
        {
            this.clusterConfig = clusterConfig;
            return this;
        }

        public ElasticsearchArtifact build()
        {
            String flavour = clusterConfig.getFlavour();
            String version = clusterConfig.getVersion();

            String id = getArtifactId(flavour, version);
            String classifier = getArtifactClassifier(version);
            String type = getArtifactType(version);

            return new ElasticsearchArtifact(id, version, classifier, type);
        }

        private String getArtifactId(String flavour, String version)
        {
            if (VersionUtil.isBetween_6_3_0_and_7_10_x(version))
            {
                if (StringUtils.isEmpty(flavour))
                {
                    return "elasticsearch-oss";
                }
                else if ("default".equals(flavour))
                {
                    return "elasticsearch";
                }
                else
                {
                    return String.format("elasticsearch-%s", flavour);
                }
            }
            else {
                return "elasticsearch";
            }
        }

        private String getArtifactClassifier(String version)
        {
            if (VersionUtil.isEqualOrGreater_7_0_0(version))
            {
                if (SystemUtils.IS_OS_WINDOWS)
                {
                    return "windows-x86_64";
                }
                else if (SystemUtils.IS_OS_MAC)
                {
                    return "darwin-x86_64";
                }
                else if (SystemUtils.IS_OS_LINUX)
                {
                    return "linux-x86_64";
                }
                else {
                    throw new IllegalStateException("Unknown OS, cannot determine the Elasticsearch classifier.");
                }
            }
            else // No classifier for ES below 7.0.0
            {
                return null;
            }
        }

        private String getArtifactType(String version)
        {
            if (VersionUtil.isEqualOrGreater_7_0_0(version))
            {
                if (SystemUtils.IS_OS_WINDOWS)
                {
                    return "zip";
                }
                else if (SystemUtils.IS_OS_MAC)
                {
                    return "tar.gz";
                }
                else if (SystemUtils.IS_OS_LINUX)
                {
                    return "tar.gz";
                }
                else {
                    throw new IllegalStateException("Unknown OS, cannot determine the Elasticsearch classifier.");
                }
            }
            else // Only a single artifact type below 7.0.0
            {
                return "zip";
            }
        }
    }

}
