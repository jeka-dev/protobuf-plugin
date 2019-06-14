package dev.jeka.plugins.protobuf;

import dev.jeka.core.api.file.JkPathTree;
import dev.jeka.core.api.system.JkLog;
import dev.jeka.core.api.system.JkProcess;
import dev.jeka.core.api.utils.JkUtilsString;
import dev.jeka.core.tool.JkCommands;
import dev.jeka.core.tool.JkDoc;
import dev.jeka.core.tool.JkPlugin;
import dev.jeka.core.tool.builtins.java.JkPluginJava;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@JkDoc("Compiles protocol buffer files to javaPlugin source.")
public class JkPluginProtobuf extends JkPlugin {

    private static final String PROTOC_COMMAND = "protoc";

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
        javaPlugin.getProject().getMaker().getTasksForCompilation().getPreCompile().chain(() -> {
            JkLog.startTask("Compiling protocol buffer files...");
            run();
            JkLog.endTask();
        });
    }

    @JkDoc("Compiles protocol buffer files to javaPlugin.")
    public void run() {
        JkPathTree protoFiles = getCommands().getBaseTree().goTo(protoFilePath);
        String[] extraArguments = JkUtilsString.translateCommandline(extraArgs);
        compile(protoFiles, Arrays.asList(extraArguments),
                javaPlugin.getProject().getMaker().getOutLayout().getGeneratedResourceDir());
    }

    public static void compile(JkPathTree protoFiles, List<String> extraArgs, Path javaOut) {
        JkProcess.of(PROTOC_COMMAND, makeArgs(protoFiles, protoFiles.getRoot(), extraArgs, javaOut))
                .withFailOnError(true)
                .withLogCommand(JkLog.isVerbose())
                .runSync();
        JkLog.info("Protocol buffer compiled " + protoFiles.count(100000, false) + " files.");
    }

    private static String[] makeArgs(JkPathTree protoFiles, Path protoPath, List<String> extraArgs, Path javaOut) {
        List<String> args = new ArrayList<String>();
        args.add("--proto_path=" + protoPath.normalize().toString());
        args.add("--java_out=" + javaOut.normalize().toString());
        for (Path file : protoFiles.getFiles()) {
            args.add(file.normalize().toString());
        }
        args.addAll(extraArgs);
        return args.toArray(new String[0]);
    }

}
