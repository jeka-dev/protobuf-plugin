package dev.jeka.plugins.protobuf;

import dev.jeka.core.api.file.JkPathTree;
import dev.jeka.core.api.system.JkLog;
import dev.jeka.core.api.utils.JkUtilsString;
import dev.jeka.core.tool.JkCommands;
import dev.jeka.core.tool.JkDoc;
import dev.jeka.core.tool.JkPlugin;
import dev.jeka.core.tool.builtins.java.JkPluginJava;

import java.util.Arrays;

@JkDoc("Compiles protocol buffer files to javaPlugin source.")
public class JkPluginProtobuf extends JkPlugin {

    @JkDoc("Relative path of the protocol buffer files.")
    public String protoFilePath = "src/main/protobuf";

    @JkDoc("Extra arguments to add to 'protoc' command.")
    public String extraArgs = "";

    private final JkPluginJava javaPlugin;

    protected JkPluginProtobuf(JkCommands commands) {
        super(commands);
        this.javaPlugin = commands.getPlugins().get(JkPluginJava.class);
    }

    @JkDoc("Add protocol buffer source generation to the Java Project Maker. " +
            "The source generation will be automatically run prior compilation phase.")
    @Override
    protected void activate() {
        javaPlugin.getProject().getMaker().getTasksForCompilation().getPreCompile().chain(this::compile);
    }

    @JkDoc("Compiles protocol buffer files to javaPlugin.")
    public void compile() {
        JkLog.startTask("Compiling protocol buffer files from " + protoFilePath);
        JkPathTree protoFiles = getCommands().getBaseTree().goTo(protoFilePath);
        String[] extraArguments = JkUtilsString.translateCommandline(extraArgs);
        JkProtobufWrapper.compile(protoFiles, Arrays.asList(extraArguments),
                javaPlugin.getProject().getMaker().getOutLayout().getGeneratedSourceDir());
        JkLog.endTask();
    }

}
