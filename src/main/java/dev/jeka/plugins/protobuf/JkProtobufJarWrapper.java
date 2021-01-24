package dev.jeka.plugins.protobuf;

import com.github.os72.protocjar.Protoc;
import dev.jeka.core.api.file.JkPathTree;
import dev.jeka.core.api.system.JkLog;
import dev.jeka.core.api.system.JkProcess;
import dev.jeka.core.api.utils.JkUtilsPath;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;

public class JkProtobufJarWrapper {

    public static void compile(JkPathTree protoFiles, List<String> extraArgs, Path javaOut) {
        JkUtilsPath.createDirectories(javaOut);
        String args[] = JkProtobufNativeWrapper.makeArgs(protoFiles, protoFiles.getRoot(), extraArgs, javaOut);
        try {
            Protoc.runProtoc(args);
        } catch (IOException e) {
            throw new UncheckedIOException("Error while running protoc with args " + args, e);
        } catch (InterruptedException e) {
            throw new IllegalStateException("Error while running protoc with args " + Arrays.asList(args), e);
        }
        JkLog.info("Protocol buffer compiled " + protoFiles.count(100000, false) + " files to " + javaOut + ".");
    }
}
