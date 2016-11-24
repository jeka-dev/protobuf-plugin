
# Jerkar library for Protobuf

This provides a library to use the [protobuf](https://developers.google.com/protocol-buffers/) compiler in your Java builds.

## How to use

First, [Make sure jerkar is installed](http://jerkar.github.io/documentation/latest/getting_started.html)

Then add the `JkImport` annotation to your Build script.

```java
@JkImport("org.jerkar:protobuf:1.0")
public class Build extends JkJavaBuild {
    ...
}
```

TODO: actually publish library to repo somewhere!

After adding the import, make sure to update your IDE's classpath.

Make a method to run the protobuf compiler.

```java
public void protobuf() {
    JkProtobuf.of(this).compile();
}
```

Override the `compile` method to run the protobuf compiler before the java compiler.

```java
@Override
public void compile() {
    protobuf();
    super.compile();
}
```

Make a task to run the protobuf compiler after you edit `.proto` files.

```java
public void doProtobuf() {
    protobuf();
}
```

`JkProtobuf` will write the generated java sources to the `build/output/generated-sources/java` directory by default.
Make sure to update your IDE classpath to include that directory.


## Configuring `JkProtobuf`

By default, `JkProtobuf` looks for `*.proto` files in the `proto` folder of your project.
You can change the defaults by configuring your `JkProtobuf` instance.

```java
public void protobuf() {
    JkProtobuf.of(this)
        .withProtoFiles(baseDir().from("my/proto/dir"))
        .compile();
}
```

To use specific proto files, rather than an entire directory:

```java
public void protobuf() {
    JkProtobuf.of(this)
        .withProtoFiles(baseDir().from("my/proto/dir").include("thisOne.proto"))
        .andProtoFiles(baseDir().from("my/other/proto/dir").include("anotherOne.proto"))
        .compile();
}
```

If your proto files reference definitions from another project, you can specify other proto directories too.

```java
@JkProject("../other/project")
JkJavaBuild otherProject;

public void protobuf() {
    JkProtobuf.of(this)
        .andProtoDirs(otherProject.baseDir().file("proto"))
        .compile();
}
```

By default, `JkProtobuf` will invoke the protobuf compiler by using the `protoc` command in
your operating system's command line environment. If you need to use a specific protobuf compiler,
you can configure `JkProtobuf` with the path to the `protoc` executable directly.

```java
public void protobuf() {
    JkProtobuf.of(this)
        .withProtoc(new File("/path/to/protoc"))
        .compile();
}
```

Any other tweaking to the `protoc` command line invocation can be done by adding extra arguments.

```java
public void protobuf() {
    JkProtobuf.of(this)
        .withExtraArgs("--include_source_info")
        .compile();
}
```
