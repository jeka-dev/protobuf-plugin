![Build Status](https://github.com/jerkar/protobuf-plugin/actions/workflows/push-master.yml/badge.svg)
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

    @Override
    protected void setup() {
        java.getProject().simpleFacade()
            .setCompileDependencies(deps -> deps
                .and("com.google.protobuf:protobuf-java:3.13.0")
            );
        ...
    }
}
```
The Java source files will be generated automatically prior compiling Java sources and 
 _com.google.protobuf:protobuf-java_ library will be added to project dependency. 
 
See a running example [here](dev.jeka.plugins.protobuf-sample) 

### Programmatically

You can use the protocol Buffer wrapper programmatically as a vanilla library using 
static method `dev.jeka.plugins.protobuf.JkProtobuf#compile` method. 

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
* append _com.google.protobuf:protobuf-java_ library to project dependencies
* update your IDE metadata `jeka intellij#iml`
* that's all !

See example [here](dev.jeka.plugins.protobuf-sample)

The plugin takes care to : 
* add a pre-compilation task for generating .java files in _jeka/output/geneated_sources/java_. _.proto_ source files are supposed to lie in _src/main/protobuf_.
* append _com.google.protobuf:protobuf-java_ library to project dependencies.


## How to build this project

This project uses Jeka wrapper, you don't need to have Jeka installed on your machine. simply execute `./jekaw cleanPack`
from the root of this project.




 






