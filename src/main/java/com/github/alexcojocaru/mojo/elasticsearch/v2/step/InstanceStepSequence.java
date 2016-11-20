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
package com.github.alexcojocaru.mojo.elasticsearch.v2.step;

/**
 * Copied from the t7mp project.
 * Defines a sequence of {@link InstanceStep}s.
 * 
 * @author Joerg Bellmann
 */
public interface InstanceStepSequence
        extends InstanceStep
{

    void add(InstanceStep step);

}
