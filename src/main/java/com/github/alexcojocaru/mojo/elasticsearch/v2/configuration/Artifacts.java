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

import com.github.alexcojocaru.mojo.elasticsearch.v2.AbstractArtifact;
import com.google.common.base.Splitter;
import com.google.common.collect.Iterables;

/**
 * Copied from the t7mp project.
 * 
 * @author Joerg Bellmann
 *
 */
public final class Artifacts
{

    private static final int ZERO = 0;
    private static final int ONE = 1;
    private static final int TWO = 2;
    private static final int THREE = 3;
    private static final int FOUR = 4;
    private static final int FIVE = 5;

    private Artifacts()
    {
        // hide constructor
    }

    public static AbstractArtifact fromCoordinates(String coordinates)
    {
        Iterable<String> splitted =
                Splitter.on(':').omitEmptyStrings().trimResults().split(coordinates);
        String[] strings = Iterables.toArray(splitted, String.class);
        if (strings.length < THREE || strings.length > FIVE)
        {
            throw new InvalidCoordinatesException(coordinates);
        }
        else
        {
            String extension = "jar"; // DEFAULT
            String classifier = null; // DEFAULT
            String groupId = strings[ZERO];
            String artifactId = strings[ONE];
            String version = strings[strings.length - ONE];
            if (strings.length == FOUR)
            {
                extension = strings[TWO];
            }
            if (strings.length == FIVE)
            {
                classifier = strings[THREE];
            }
            return new DefaultArtifact(groupId, artifactId, version, classifier, extension);
        }
    }

    static final class DefaultArtifact
            extends AbstractArtifact
    {

        private String type;

        DefaultArtifact(String groupId, String artifactId, String version, String classifier,
                String type)
        {
            super(groupId, artifactId, version, classifier, type);
            this.type = type;
        }

        @Override
        public String getType()
        {
            return type;
        }
    }

}
