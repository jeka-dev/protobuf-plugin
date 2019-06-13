[![Maven Central](https://maven-badges.herokuapp.com/maven-central/org.jerkar/protobuf-plugin/badge.svg)](https://maven-badges.herokuapp.com/maven-central/org.jerkar/protobuf-plugin) <br/>

# Jerkar library for Protobuf

This provides a library to use the [protobuf](https://developers.google.com/protocol-buffers/) compiler in your Java builds.

## How to use

First, [Make sure jerkar is installed](http://jerkar.github.io/documentation/latest/getting_started.html).

[Protobuff compiler](https://developers.google.com/protocol-buffers/docs/downloads) should be installed as well.

Then add the `JkImport` annotation to your Build script and get the plugin.

```java
@JkImport("dev.jeka.plugins:protobuf:[version]")
public class Build extends JkCommands {
    
    JkPluginProtobuf protobufPlugin = getPlugin(JkPluginProtobuf.class);

    ...
}
```

That's all. Now, when building your project, files under `src/main/proto' will be compiled to java sources prior java compilation phase.

You can also run the the protocol buffer compiler explicitly by executing `jeka protobuf#run`.

If you don't want to use the plugin, you can invoke the protocol compiler programmatically by calling static method `JkPluginProtobuf#compile`.

 






