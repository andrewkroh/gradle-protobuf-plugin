/*
 * Copyright 2012 Andrew Kroh
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.andrewkroh.gradle.plugins.protobuf

import org.gradle.api.Project
import org.gradle.api.Task
import org.gradle.api.tasks.bundling.Jar
import org.gradle.testfixtures.ProjectBuilder
import org.junit.Before
import org.junit.Test

import static org.junit.Assert.assertEquals
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

/**
 * JUnit test for {@link ProtobufPlugin}.
 *
 * @author Andrew Kroh
 */
class ProtobufPluginTest {

    Project project;

    @Before
    public void setUp() {
        project = ProjectBuilder.builder().build()
        project.apply plugin: 'protobuf'
    }

    @Test
    public void 'plugin applies java plugin'() {
        assertTrue(project.plugins.hasPlugin('java'))
    }

    @Test
    public void 'plugin adds protobuf extension'() {
        def pluginExt = project.extensions.getByName('protobuf')
        assertNull(pluginExt.version)
        assertEquals('protoc', pluginExt.compiler)
        assertEquals('src/main/proto', pluginExt.src)
        assertEquals('generated/java', pluginExt.outputJava)
        assertEquals('generated/cpp', pluginExt.outputCpp)
        assertEquals('generated/python', pluginExt.outputPython)
        assertFalse(pluginExt.outputToProjectDir)
		assertTrue(pluginExt.autoDependency)
    }
}
