[![Build Status](https://travis-ci.org/jerkar/protobuf-plugin.svg?branch=master)](https://travis-ci.org/jerkar/protobuf-plugin)
[![Maven Central](https://maven-badges.herokuapp.com/maven-central/dev.jeka/protobuf-plugin/badge.svg)](https://maven-badges.herokuapp.com/maven-central/dev.jeka/protobuf-plugin) <br/>

# Jeka library/plugin for Protobuf

This provides a plugin to use the [protobuf](https://developers.google.com/protocol-buffers/) compiler in your Java builds

## How to use

This plugin no longer requires to have `protoc` installed on the host machine.

Just declare the plugin and put protobuf files under `src/main/proto`.  

```java
@JkDefClasspath("dev.jeka.plugins:protobuf:[version]")
public class Build extends JkCommandSet {
    
    JkPluginJava javaPlugin = getPlugin(JkPluginJava.class);
    
    JkPluginProtobuf protobufPlugin = getPlugin(JkPluginProtobuf.class);

    ...
}
```
The Java source files will be generated automatically prior compiling Java sources and 
 _com.google.protobuf:protobuf-java_ library will be added to project dependency. 

### Programmatically

You can use the protocol Buffer wrapper programmatically as a vanilla library using 
static method `dev.jeka.plugins.protobuf.JkProtobufJarWrapper#compile` method. 

### Using with JkPluginJava

The most common usage is to use it along Jeka `java` plugin. For such you only have to :

* declare the plugin as shown below
* update your IDE metadata `jeka intellij#iml`
* that's all !


The plugin takes care to : 
* add a pre-compilation task for generating .java files in _jeka/output/geneated_sources/java_. _.proto_ source files are supposed to lie in _src/main/protobuf_.
* append _com.google.protobuf:protobuf-java_ library to project dependencies.

## How to build this project

This project uses Jeka wrapper, you don't need to have Jeka installed on your machine. simply execute `./jekaw cleanPack`
from the root of this project.




 






