package dev.jeka.plugins.protobuf;

import dev.jeka.core.api.file.JkPathTree;
import dev.jeka.core.api.system.JkLog;
import dev.jeka.core.api.utils.JkUtilsString;
import dev.jeka.core.tool.JkClass;
import dev.jeka.core.tool.JkConstants;
import dev.jeka.core.tool.JkDoc;
import dev.jeka.core.tool.JkPlugin;
import dev.jeka.core.tool.builtins.java.JkPluginJava;

import java.nio.file.Path;
import java.util.Arrays;

@JkDoc("Compiles protocol buffer files to java source.")
public class JkPluginProtobuf extends JkPlugin {

    private static final String DEFAULT_OUT = JkConstants.OUTPUT_PATH + "/generated_sources/java";

    @JkDoc("Relative path of the protocol buffer files.")
    public String protoFilePath = "src/main/proto";

    @JkDoc("Location where .java files are generated.")
    public String outPath = DEFAULT_OUT;

    @JkDoc("Extra arguments to add to 'protoc' command.")
    public String extraArgs = "";

    @JkDoc("The version of Protocol Buffer to add to the project compile classpath (only relevant id using JkPluginJava plugin.")
    public String javaProtocolBufferVersion = "3.8.0";

    protected JkPluginProtobuf(JkClass buildClass) {
        super(buildClass);
    }

    @JkDoc("Add protocol buffer source generation to the Java Project Maker. " +
            "The source generation will be automatically run prior compilation phase.")
    @Override
    protected void afterSetup() {
        if (javaPlugin() != null) {
            javaPlugin().getProject()
                .getConstruction()
                    .getCompilation()
                        .getPreCompileActions()
                            .append(this::compileProtocolBufferFiles);
        }
    }

    @JkDoc("Compiles protocol buffer files to java.")
    public void compileProtocolBufferFiles() {
        JkLog.startTask("Compiling protocol buffer files from " + protoFilePath);
        JkPathTree protoFiles = getJkClass().getBaseTree().goTo(protoFilePath);
        String[] extraArguments = JkUtilsString.translateCommandline(extraArgs);
        final Path out;
        if (javaPlugin() == null || !DEFAULT_OUT.equals(outPath)) {
            out = getJkClass().getBaseDir().resolve(outPath);
        } else {
            out = javaPlugin()
                    .getProject()
                        .getConstruction()
                            .getCompilation()
                                .getLayout().resolveGeneratedSourceDir();
        }
        JkProtobufJarWrapper.compile(protoFiles, Arrays.asList(extraArguments), out);
        JkLog.endTask();
    }

    private JkPluginJava javaPlugin() {
        if (getJkClass().getPlugins().hasLoaded(JkPluginJava.class)) {
            return getJkClass().getPlugin(JkPluginJava.class);
        }
        return null;
    }

    private String protobufModuleVersion() {
        return "com.google.protobuf:protobuf-java:" + javaProtocolBufferVersion;
    }

}
