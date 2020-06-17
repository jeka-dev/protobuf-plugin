[![Build Status](https://travis-ci.org/jerkar/protobuf-plugin.svg?branch=master)](https://travis-ci.org/jerkar/protobuf-plugin)
[![Maven Central](https://maven-badges.herokuapp.com/maven-central/dev.jeka/protobuf-plugin/badge.svg)](https://maven-badges.herokuapp.com/maven-central/dev.jeka/protobuf-plugin) <br/>

# Jerkar library for Protobuf

This provides a library to use the [protobuf](https://developers.google.com/protocol-buffers/) compiler in your Java builds.

## How to use

[Protobuff compiler](https://developers.google.com/protocol-buffers/docs/downloads) must be installed on the hosting machine.

### Programmatically

You can use the protocol Buffer wrapper programmatically as a vanilla library using 
static method `dev.jeka.plugins.protobuf.JkProtobufWrapper#compile` method. 

### Command line only

You can invoke this plugin from command line on a Jeka project that does not declare it.

To compile .poto files
`jeka @dev.jeka.plugins:protobuf:[version] protobuf#compile`
or `jeka protobuf#compile` if you have annoted a def class with `@JkDefClasspath` annotation.

To get help and options :
`jeka protobuf#compile`

### Using with JkPluginJava

The most common usage is to use it along Jeka `java` plugin. For such you only have to :

* declare the plugin as shown below
* update your IDE metadata `jeka intellij#iml`
* that's all !

```java
@JkDefClasspath("dev.jeka.plugins:protobuf:[version]")
public class Build extends JkCommandSet {
    
    JkPluginJava javaPlugin = getPlugin(JkPluginJava.class);
    
    JkPluginProtobuf protobufPlugin = getPlugin(JkPluginProtobuf.class);

    ...
}
```
The plugin takes care to : 
* add a pre-compilation task for generating .java files in _jeka/output/geneated_sources/java_. _.proto_ source files are supposed to lie in _src/main/protobuf_.
* append _com.google.protobuf:protobuf-java_ library to project dependencies.

## How to build this project

This project uses Jeka wrapper, you don't need to have Jeka installed on your machine. simply execute `./jekaw cleanPack`
from the root of this project.




 






