[![Build Status](https://travis-ci.org/jerkar/protobuf-plugin.svg?branch=master)](https://travis-ci.org/jerkar/protobuf-plugin)
[![Maven Central](https://maven-badges.herokuapp.com/maven-central/dev.jeka/protobuf-plugin/badge.svg)](https://maven-badges.herokuapp.com/maven-central/dev.jeka/protobuf-plugin) <br/>

# Jerkar library for Protobuf

This provides a library to use the [protobuf](https://developers.google.com/protocol-buffers/) compiler in your Java builds.

## How to use

First, [Make sure jerkar is installed](http://jerkar.github.io/documentation/latest/getting_started.html).

[Protobuff compiler](https://developers.google.com/protocol-buffers/docs/downloads) should be installed as well.

### Programmatically

You can use the protocol Buffer wrapper programmatically as a vanilla library using 
static method `dev.jeka.plugins.protobuf.JkProtobufWrapper#compile` method. 

### Command line only

You can invoke this plugin from command line on a Jeka project that does not declare it.
`jeka @dev.jeka.plugins:protobuf:[version] protobuf#compile`



``````````


Then add the `JkImport` annotation to your Build script and get the plugin.

```java
@JkImport("")
public class Build extends JkCommands {
    
    JkPluginProtobuf protobufPlugin = getPlugin(JkPluginProtobuf.class);

    ...
}
```

That's all. Now, when building your project, files under `src/main/protobuf' will be compiled to java sources prior java compilation phase.

You can also run the the protocol buffer compiler explicitly by executing `jeka protobuf#compile`.



 






