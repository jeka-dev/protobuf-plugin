package dev.jeka.plugins.protobuf;

import dev.jeka.core.api.file.JkPathTree;
import dev.jeka.core.api.system.JkLog;
import dev.jeka.core.api.system.JkProcess;
import dev.jeka.core.api.utils.JkUtilsPath;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

/**
 * Provides method to compile Google Protocol Buffer files to Java source.
 */
public class JkProtobufWrapper {

    private static final String PROTOC_COMMAND = "protoc";

    /**
     * Compiles specified protobuf files to the specified output.
     * @param protoFiles Set of .proto files
     * @param extraArgs Extra arguments to pass to the protobuf compiler
     * @param javaOut Location of output .java files
     */
    public static void compile(JkPathTree protoFiles, List<String> extraArgs, Path javaOut) {
        JkUtilsPath.createDirectories(javaOut);
        JkProcess.of(PROTOC_COMMAND, makeArgs(protoFiles, protoFiles.getRoot(), extraArgs, javaOut))
                .withFailOnError(true)
                .withLogCommand(JkLog.isVerbose())
                .runSync();
        JkLog.info("Protocol buffer compiled " + protoFiles.count(100000, false) + " files to " + javaOut + ".");
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
