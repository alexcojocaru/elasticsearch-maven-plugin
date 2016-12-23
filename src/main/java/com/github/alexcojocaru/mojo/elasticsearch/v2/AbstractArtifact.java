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
package com.github.alexcojocaru.mojo.elasticsearch.v2;

import java.io.File;

import org.apache.commons.lang3.StringUtils;
import org.apache.maven.plugins.annotations.Parameter;

/**
 * Copied from the t7mp project.
 * 
 * @author Joerg Bellmann
 *
 */
public abstract class AbstractArtifact {

    @Parameter(required=true)
    protected String groupId;

    @Parameter(required=true)
    protected String artifactId;

    @Parameter
    protected String version;

    @Parameter
    protected String type;

    @Parameter
    protected String classifier;

    @Parameter
    protected String systemPath;

    protected File file;

    public AbstractArtifact() {
        // default constructor
    }

    public AbstractArtifact(String groupId, String artifactId, String version, String classifier, String type) {
        this.setGroupId(groupId);
        this.setArtifactId(artifactId);
        this.setVersion(version);
        this.setClassifier(classifier);
        this.setType(type);

    }

    public String getGroupId() {
        return groupId;
    }

    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }

    public String getArtifactId() {
        return artifactId;
    }

    public void setArtifactId(String artifactId) {
        this.artifactId = artifactId;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public abstract String getType();

    public void setType(String type) {
        this.type = type;
    }

    public String getClassifier() {
        return classifier;
    }

    public void setClassifier(String classifier) {
        this.classifier = classifier;
    }

    public String getSystemPath() {
        return this.systemPath;
    }

    public void setSystemPath(String systemPath) {
        this.systemPath = systemPath;
    }

    public File getFile() {
        return file;
    }

    public void setFile(File file) {
        this.file = file;
    }

    public String getArtifactCoordinates() {
        if (!StringUtils.isBlank(getSystemPath())) {
            return getSystemPath();
        } else {
            return buildDefaultMavenCoordinates();
        }
    }

    /**
     * The {@code <groupId>:<artifactId>[:<extension>[:<classifier>]]:<version>} of the artifact.
     * 
     * @return The Maven coordinates for the current artifact
     */
    protected String buildDefaultMavenCoordinates() {
        StringBuilder sb = new StringBuilder();
        sb.append(getGroupId()).append(":");
        sb.append(getArtifactId());
        if (StringUtils.isNotBlank(getType())) {
            sb.append(":").append(getType());
        }
        if (StringUtils.isNotBlank(getClassifier())) {
            sb.append(":").append(getClassifier());
        }
        sb.append(":");
        sb.append(getVersion());
        return sb.toString().trim();
    }

    @Override
    public String toString() {
        return "Artifact[" + getArtifactCoordinates() + "]";
    }

}
