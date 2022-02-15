package dev.jeka.plugins.protobuf;

import dev.jeka.core.api.depmanagement.JkModuleFileProxy;
import dev.jeka.core.api.depmanagement.JkModuleId;
import dev.jeka.core.api.depmanagement.JkRepoSet;
import dev.jeka.core.api.file.JkPathTree;
import dev.jeka.core.api.java.JkJavaProcess;
import dev.jeka.core.api.system.JkLog;
import dev.jeka.core.api.utils.JkUtilsPath;

import java.nio.file.Path;
import java.util.List;

public class JkProtobuf {

    public static final JkModuleId PROTOBUF_MODULE = JkModuleId.of("com.google.protobuf:protobuf-java");

    private static final String PROTOC_JAR_MODULE = "com.github.os72:protoc-jar";

    public static void compile(JkPathTree protoFiles, List<String> extraArgs, Path javaOut, JkRepoSet repos,
                               String protocVersion) {
        JkUtilsPath.createDirectories(javaOut);
        Path jar = JkModuleFileProxy.of(repos, PROTOC_JAR_MODULE + ":3.11.4").get();
        JkJavaProcess javaProcess = JkJavaProcess.ofJavaJar(jar, null)
                .addParams(extraArgs)
                .addParams("--java_out=" + javaOut.normalize())
                .addParams("-I=" + protoFiles.getRoot().normalize())
                .addParamsIf(protocVersion != null, "-v" + protocVersion)
                .setLogCommand(true)

                .setLogOutput(true);
        List<Path> paths = protoFiles.getRelativeFiles();
        for (Path file : paths) {
            javaProcess.addParams(file.toString());
        }
        javaProcess.exec();
        JkLog.info("Protocol buffer compiled " +
                protoFiles.count(100000, false) + " files to " + javaOut + ".");
    }
}
