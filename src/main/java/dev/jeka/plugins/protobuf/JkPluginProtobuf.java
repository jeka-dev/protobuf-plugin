package dev.jeka.plugins.protobuf;

import dev.jeka.core.api.file.JkPathMatcher;
import dev.jeka.core.api.file.JkPathTree;
import dev.jeka.core.api.system.JkLog;
import dev.jeka.core.api.system.JkProcess;
import dev.jeka.core.api.utils.JkUtilsPath;
import dev.jeka.core.api.utils.JkUtilsString;
import dev.jeka.core.tool.JkCommands;
import dev.jeka.core.tool.JkDoc;
import dev.jeka.core.tool.JkPlugin;
import dev.jeka.core.tool.builtins.java.JkPluginJava;

import java.nio.charset.Charset;
import java.nio.file.Path;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

@JkDoc("Compiles protocol buffer files to javaPlugin source.")
public class JkPluginProtobuf extends JkPlugin {

    private static final String PROTOC_COMMAND = "protoc";

    @JkDoc("Relative path of the protocol buffer files.")
    public String protoFilePath = "src/main/proto";

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
        compile(protoFiles, generatedSourceDir(), Arrays.asList(extraArguments), charset());
    }

    public static void compile(JkPathTree protoFiles, Path javaOut, List<String> extraArgs, Charset sourceCharset) {
        Path tempDir = JkUtilsPath.createTempDirectory("jkprotoc");
        JkProcess.of(PROTOC_COMMAND, makeArgs(protoFiles, protoFiles.getRoot(), tempDir, extraArgs))
                .withFailOnError(true)
                .runSync();
        final DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mmZ");
        dateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
        String dateString = dateFormat.format(new Date());
        String annotations = "@javax.annotation.Generated(value=\"com.google.protobuf\", date=\"" + dateString + "\")"
                + "\n@SuppressWarnings(\"all\")";

        JkPathTree javaFiles = JkPathTree.of(tempDir).andMatcher(JkPathMatcher.of("**/*.javaPlugin"));
        List<Path> files = javaFiles.getFiles();
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
        for (Path file : protoFiles.getFiles()) {
            args.add(file.normalize().toString());
        }
        args.addAll(extraArgs);
        return args.toArray(new String[0]);
    }

    private Path generatedSourceDir() {
        return javaPlugin.getProject().getMaker().getOutLayout().getGeneratedResourceDir();
    }

    private Charset charset() {
        return Charset.forName(javaPlugin.getProject().getCompileSpec().getEncoding());
    }

}
