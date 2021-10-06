package dev.jeka.plugins.protobuf;

import dev.jeka.core.api.file.JkPathTree;
import dev.jeka.core.api.java.JkInternalClassloader;
import dev.jeka.core.api.java.JkJavaProcess;
import dev.jeka.core.api.system.JkLog;
import dev.jeka.core.api.utils.JkUtilsIO;
import dev.jeka.core.api.utils.JkUtilsPath;

import java.net.URL;
import java.nio.file.Path;
import java.util.List;

public class JkProtobuf {

    public static void compile(JkPathTree protoFiles, List<String> extraArgs, Path javaOut) {
        JkUtilsPath.createDirectories(javaOut);
        URL embeddedUrl = JkProtobuf.class.getResource("protoc-jar-3.11.4.jar");
        Path cachedUrl = JkUtilsIO.copyUrlContentToCacheFile(embeddedUrl, null, JkInternalClassloader.URL_CACHE_DIR);
        JkJavaProcess javaProcess = JkJavaProcess.ofJavaJar(cachedUrl, null)
                .addParams(extraArgs)
                .addParams("--java_out=" + javaOut.normalize())
                .addParams("-I=" + protoFiles.getRoot().normalize())
                .setLogCommand(true)
                .setLogOutput(true);
        for (Path file : protoFiles.getRelativeFiles()) {
            javaProcess.addParams(file.toString());
        }
        javaProcess.exec();
        JkLog.info("Protocol buffer compiled " + protoFiles.count(100000, false) + " files to " + javaOut + ".");
    }
}
