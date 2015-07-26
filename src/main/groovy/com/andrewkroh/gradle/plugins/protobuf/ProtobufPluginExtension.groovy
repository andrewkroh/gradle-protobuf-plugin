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

/**
 * Plugin extension object that becomes available as a project property with the
 * same name as this plugin. It can be used to override the default settings for
 * the plugin.
 *
 * @author Andrew Kroh
 */
class ProtobufPluginExtension {

    /**
     * Name (or full path) of the Google Protocol Buffer compiler that the
     * plugin will execute. Default value is 'protoc'.
     */
    String compiler = 'protoc'

    /**
     * Protocol Buffers version that your project requires. By specifying this
     * value the plugin will verify that it is using specified version of the
     * compiler.
     */
    String version

    /**
     * Source directory for your .proto files. The value is relative to the
     * project root.
     */
    String src = 'src/main/proto'

    /**
     * Output directory for generated java source files. The value is relative
     * to the project build directory.
     */
    String outputJava = 'generated/java'

    /**
     * Output directory for generated CPP source files. The value is relative to
     * the project build directory.
     */
    String outputCpp = 'generated/cpp'

    /**
     * Output directory for generated python source files. The value is relative
     * to the project build directory.
     */
    String outputPython = 'generated/python'

    /**
     * Output generated files relative to project root directory instead of
     * relative to build directory.
     */
    boolean outputToProjectDir = false

    /**
     * Activate or deactivate automatic dependency creation. Default is 'true',
     * i.e. the dependency is added automatically based on the compiler version.
     */
    boolean autoDependency = true

}
