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

package com.krohinc.gradle.plugins.protobuf

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.Task
import org.gradle.api.InvalidUserDataException
import org.gradle.api.tasks.bundling.Jar
import org.slf4j.Logger
import org.slf4j.LoggerFactory

/**
 * Gradle plugin for compiling Google Protocol Buffer files (.proto). It
 * generates Java, CPP, and Python source files from the .proto files in
 * src/main/proto.
 * 
 * <p>
 * This plugin adds a dependency on the Java plugin so that it can compile the
 * generated Java source files.
 * 
 * <p>
 * This plugin generates a sources jar that contains all of the Java sources
 * (including those generated from the .proto files) in this project.
 * 
 * <p>
 * The Protocol Buffer compiler must be on the path for the plugin to work. If
 * the protocol buffer is in a different location then specify the full path to
 * the compiler in your build.gradle file using
 * {@code protobuf.compiler = '/full/path/protoc'}.
 * 
 * @author Andrew Kroh
 */
class ProtobufPlugin implements Plugin<Project> {
    /**
     * Description of the task that compiles .proto files.
     */
    final static String COMPILE_PROTO_TASK_DESCRIPTION =
        'Generates source files by compiling .proto files.'

    /**
     * Name of the task that compiles .proto files.
     */
    final static String COMPILE_PROTO_TASK_NAME = 'compileProto'

    /**
     * Description of the task that generates a source jar.
     */
    final static String SOURCES_JAR_DESCRIPTION = 'Generates a source jar.'

    /**
     * Name of the task that generates a source jar.
     */
    final static String SOURCES_JAR_TASK_NAME = 'sourcesJar'

    /**
     * SLF4J logger for this class.
     */
    final static Logger logger = LoggerFactory.getLogger(ProtobufPlugin.class.name)

    /**
     * Apply this plugin to the target project.
     */
    void apply(Project project) {
        // Ensure that the build-item has a dependency
        // on the Java Plugin.
        project.apply plugin: 'java'
        
        // Add the 'protobuf' extension object:
        project.extensions.create('protobuf', ProtobufPluginExtension)

        // Add task for compiling .proto files.
        addCompileProtoTask(project)

        // Add task for creating a sources jar that will
        // contain all the java files.
        addSourceJarTask(project)

        project.afterEvaluate {
            String detectedProtocVersion = getProtocVersion(project.protobuf.compiler)

            validateProtocVersion(detectedProtocVersion, project.protobuf.version)

            // Add a compile time dependency on the Google protobuf jar.
            addProtobufJarDependency(project, detectedProtocVersion)
        }
    }

    /**
     * Gets the protocol buffer compiler version by calling invoking the
     * compiler with the --version argument.
     * 
     * @param compiler
     *            name of the compiler or full path to the compiler
     * @return version number of the compiler
     */
    String getProtocVersion(final String compiler) {
        def command = "$compiler --version"
        StringBuffer output = new StringBuffer()
        Process process = command.execute()
        process.waitForProcessOutput(output, output)

        String version

        // Protoc returns exit code 1 when called with --version.
        if (process.exitValue() == 1)
        {
            version = output.toString().trim().replaceAll("libprotoc\\s", "");
        }
        else
        {
            throw new InvalidUserDataException("Protoc failed:\n" +
                    output.toString())
        }

        logger.info("Detected version <$version> of the Protocol Buffer compiler.")

        return version
    }

    /**
     * Validates that the version of protoc binary matches the version
     * configured to be used by this plugin.
     * 
     * @param detectedVersion
     *            name of the compiler to test
     * @param requiredVersion
     *            expected version of the compiler
     */
    void validateProtocVersion(final String detectedVersion,
                               final String requiredVersion)
    {
        if (requiredVersion != null
                && !requiredVersion.equalsIgnoreCase(detectedVersion)) {
            throw new InvalidUserDataException(
                    "Protoc version of $detectedVersion does not match " +
                            "the expected version of $requiredVersion.")
        }
    }

    /**
     * Adds a dependency on the specified version of the protobuf-java jar(s).
     * 
     * @param project
     *            Gradle project on which to add the dependency
     * @param protobufVersion
     *            version of Google Protocol Buffers used
     */
    void addProtobufJarDependency(final Project project, 
                                  final String protobufVersion)
    {
        // Add a dependency on the protocol buffer jar:
        project.dependencies {
            compile group: 'com.google.protobuf', name: 'protobuf-java', version: protobufVersion
        }
    }

    /**
     * Adds the 'compileProto' task to the project which is responsible for
     * compiling .proto files into java, cpp, and python.
     * 
     * @param project
     *            Gradle project on which to add the dependency
     */
    void addCompileProtoTask(Project project)
    {
        Task compileJavaTask = project.tasks.getByName('compileJava')

        Task compileProtoTask = project.task(COMPILE_PROTO_TASK_NAME) {
            description = COMPILE_PROTO_TASK_DESCRIPTION

            String srcDir = new File(project.projectDir.path + File.separator + 
                                     project.protobuf.src).canonicalPath
            def srcProtoFiles = project.fileTree(srcDir).include('*.proto')
            def javaOutputDir = project.file(project.buildDir.path + File.separator + 
                                             project.protobuf.outputJava)
            def cppOutputDir = project.file(project.buildDir.path + File.separator + 
                                            project.protobuf.outputCpp)
            def pythonOutputDir = project.file(project.buildDir.path + File.separator + 
                                            project.protobuf.outputPython)

            // Build up the full protoc command:
            def cmd = "${project.protobuf.compiler} "
            cmd += "--java_out=$javaOutputDir "
            cmd += "--cpp_out=$cppOutputDir "
            cmd += "--python_out=$pythonOutputDir "
            cmd += "--proto_path=$srcDir "
            srcProtoFiles.getFiles().each { srcFile ->
                cmd += "${srcFile.path} "
            }

            // Declare the task's inputs and outputs so Gradle knows
            // if the task needs to run.
            inputs.files(srcProtoFiles.getFiles())
            outputs.dir(javaOutputDir)
            outputs.dir(cppOutputDir)
            outputs.dir(pythonOutputDir)

            // Configure the compileJava task to compile the generated
            // java source files from this task.
            compileJavaTask.source javaOutputDir
            
            doLast {
                // Create the output dirs if they do not exist:
                javaOutputDir.exists() || javaOutputDir.mkdirs()
                cppOutputDir.exists() || cppOutputDir.mkdirs()
                pythonOutputDir.exists() || pythonOutputDir.mkdirs()

                logger.info("Compiling protocol buffers: $cmd")
                StringBuffer output = new StringBuffer()
                Process result = cmd.execute()
                result.waitForProcessOutput(output, output)

                if (result.exitValue() == 0) {
                    logger.info("Protocol buffers compiled successfully. " + 
                        "Output:\n" + output.toString())
                } 
                else 
                {
                    throw new InvalidUserDataException("Protoc failed:\n" + 
                        output.toString())
                }
            }
        }

        // Make the compileJava task depend on compileProto:
        compileJavaTask.dependsOn(compileProtoTask)
    }

    /**
     * Adds the 'sourcesJar' task which packages the java source files for the
     * project into a jar file.
     * 
     * @param project
     *            Gradle project on which to add the dependency
     */
    void addSourceJarTask(final Project project)
    {
        // Get the 'classes' task on which this new
        // task will depend.
        Task classesTask = project.tasks.getByName('classes')

        // Get the 'compileJava' task so that we can
        // access its list of source files.
        Task compileJavaTask =
            project.tasks.getByName('compileJava')

        // Define the 'sourcesJar' task.
        def taskArgs = [ type: Jar, dependsOn: classesTask ]
        Task sourcesJarTask = project.task(taskArgs, SOURCES_JAR_TASK_NAME) {
            description = SOURCES_JAR_DESCRIPTION
            classifier = 'sources'
            from compileJavaTask.source
        }
        
        // Add the sources jar as an artifact of the project.
        project.artifacts {
            archives sourcesJarTask
        }
    }
}
