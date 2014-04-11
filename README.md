Gradle Protobuf Plugin ![Build Status](https://blog.crowbird.com/build-status/gradle-protobuf-plugin)
=====================
- Author: Andrew Kroh
- Website: http://blog.crowbird.com
- Download: See [maven central](http://search.maven.org/#search%7Cga%7C1%7Cg%3A%22com.andrewkroh.gradle%22)
- License: Apache License, Version 2.0
- Requirements: Java 1.5+
- Google Protocol Buffer Compiler (protoc): Any version

What is it?
-----------
This is a plugin for Gradle that enables compiling Google Protocol Buffer .proto files into data access classes. It generates Java, CPP, and Python source files from the .proto files in src/main/proto.

This plugin adds a dependency on the Java plugin so that it can compile the generated Java source files.

This plugin generates a sources jar that contains all of the Java sources (including those generated from the .proto files) in this project.

The Protocol Buffer compiler must be on the path for the plugin to work. If the protocol buffer is in a different location then specify the full path to the compiler in your build.gradle file using protobuf.compiler = '/full/path/protoc'.

Usage
-----
```groovy
apply plugin: 'protobuf'
```

The plugin JAR needs to be defined in the classpath of your build script. It is available from maven central.

```groovy
buildscript {
    repositories {
            mavenCentral()
    }

    dependencies {
        classpath 'com.andrewkroh.gradle:gradle-protobuf-plugin:0.2.0'
    } 
}
```

The plugin automatically adds a dependency on the google protobuf jars so all you need to do is specify what repository(s) to use.

```groovy
repositories {
    mavenCentral()
}
```

Project Layout
--------------

Simply put your .proto files in `src/main/proto`.

Tasks
-----

The protobuf plugin defines the following task:

* `compileProto` - Generates source files by compiling .proto files.

Extensions
----------

The protobuf plugin adds a `protobuf` extension to the project which allows you to override the default configuration of the plugin.

* `version` - Protocol Buffers version that your project requires. By specifying this value the plugin will verify that it is using specified version of the compiler.
* `src` - Source directory for your .proto files. The value is relative to the project root.
* `compiler` - Name (or full path) of the Google Protocol Buffer compiler that the plugin will execute.
* `outputCpp` - Output directory for generated CPP source files. The value is relative to the project build directory.
* `outputJava` - Output directory for generated java source files. The value is relative to the project build directory.
* `outputPython` - Output directory for generated python source files. The value is relative to the project build directory.

Example
-------

See the example directory in this repository.
