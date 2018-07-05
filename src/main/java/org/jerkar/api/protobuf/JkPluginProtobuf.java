package org.jerkar.api.protobuf;

import org.jerkar.api.file.JkPathMatcher;
import org.jerkar.api.file.JkPathTree;
import org.jerkar.api.system.JkLog;
import org.jerkar.api.system.JkProcess;
import org.jerkar.api.utils.JkUtilsPath;
import org.jerkar.api.utils.JkUtilsString;
import org.jerkar.api.utils.JkUtilsTime;
import org.jerkar.tool.JkBuild;
import org.jerkar.tool.JkDoc;
import org.jerkar.tool.JkPlugin;
import org.jerkar.tool.builtins.java.JkPluginJava;

import java.nio.charset.Charset;
import java.nio.file.Path;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

@JkDoc("Compiles protocol buffer files to java source.")
public class JkPluginProtobuf extends JkPlugin {

    private static final String PROTOC_COMMAND = "protoc";

    @JkDoc("Relative path of the protocol buffer files.")
    public String protoFilePath = "src/main/proto";

    @JkDoc("Extra arguments to add to 'protoc' command.")
    public String extraArgs = "";

    private final JkPluginJava java;


    protected JkPluginProtobuf(JkBuild build) {
        super(build);
        this.java = build.plugins().get(JkPluginJava.class);
    }

    @JkDoc("Add protocol buffer source generation to the Java Project Maker. " +
            "The source generation will be automatically run prior compilation phase.")
    @Override
    protected void decorateBuild() {
        JkPathTree protoFiles = build.baseTree().goTo(protoFilePath);
        String[] extraArguments = JkUtilsString.translateCommandline(extraArgs);
        java.project().maker().sourceGenerator.chain(() -> {
            long start = System.nanoTime();
            JkLog.startTask("Compiling protocol buffer files.");
            JkLog.startTask("Generating ");
            compile(protoFiles, java.project().getOutLayout().generatedSourceDir(), Arrays.asList(extraArguments),
                    Charset.forName(java.project().getCompileSpec().getEncoding()));
            JkLog.endTask("Done is " + JkUtilsTime.durationInMillis(start) + " milliseconds.");
        });
    }

    @JkDoc("Compiles protocol buffer files to java.")
    public void run() {

    }

    public static void compile(JkPathTree protoFiles, Path javaOut, List<String> extraArgs, Charset sourceCharset) {
        Path tempDir = JkUtilsPath.createTempDirectory("jkprotoc");
        JkProcess.of(PROTOC_COMMAND, makeArgs(protoFiles, protoFiles.root(), tempDir, extraArgs))
                .failOnError(true)
                .runSync();

        final DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mmZ");
        dateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
        String dateString = dateFormat.format(new Date());
        String annotations = "@javax.annotation.Generated(value=\"com.google.protobuf\", date=\"" + dateString + "\")"
                + "\n@SuppressWarnings(\"all\")";

        JkPathTree javaFiles = JkPathTree.of(tempDir).andMatcher(JkPathMatcher.accept("**/*.java"));
        List<Path> files = javaFiles.files();
        for (Path file : files) {
            String source = new String(JkUtilsPath.readAllBytes(file), sourceCharset);
            source = source.replace("public final class", annotations + "\npublic final class");
            Path relativePath = tempDir.relativize(file).normalize();
            Path targetPath = javaOut.resolve(relativePath);
            JkUtilsPath.write(targetPath, source.getBytes(sourceCharset));
            JkUtilsPath.deleteFile(file);
        }
        JkPathTree.of(tempDir).deleteRoot();;
        JkLog.info("Protocol buffer compiled " + files.size() + " files.");
    }

    private static String[] makeArgs(JkPathTree protoFiles, Path javaOut, Path protoPath, List<String> extraArgs) {
        List<String> args = new ArrayList<String>();
        args.add("--proto_path=" + protoPath.normalize().toString());
        args.add("--java_out=" + javaOut.normalize().toString());
        for (Path file : protoFiles.files()) {
            args.add(file.normalize().toString());
        }
        args.addAll(extraArgs);
        return args.toArray(new String[0]);
    }

}
