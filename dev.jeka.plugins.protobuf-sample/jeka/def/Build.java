import dev.jeka.core.api.java.JkJavaVersion;
import dev.jeka.core.tool.JkClass;
import dev.jeka.core.tool.JkDefClasspath;
import dev.jeka.core.tool.builtins.java.JkPluginJava;
import dev.jeka.plugins.protobuf.JkPluginProtobuf;

@JkDefClasspath("../dev.jeka.plugins.protobuf/jeka/output/dev.jeka.protobuf-plugin.jar")
class Build extends JkClass {

    final JkPluginJava java = getPlugin(JkPluginJava.class);

    final JkPluginProtobuf protobuf = getPlugin(JkPluginProtobuf.class);

    /*
     * Configures plugins to be bound to this command class. When this method is called, option
     * fields have already been injected from command line.
     */
    @Override
    protected void setup() {
        java.getProject().simpleFacade()
            .setJavaVersion(JkJavaVersion.V8)
            .setCompileDependencies(deps -> deps
                .and("com.google.guava:guava:21.0")
                .and(JkPluginProtobuf.PROTOBUF_MODULE.version("3.13.0"))
            );
    }

    public void cleanPack() {
        clean(); java.pack();
    }

}