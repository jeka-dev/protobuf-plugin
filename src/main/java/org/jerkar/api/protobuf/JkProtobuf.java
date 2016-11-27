package org.jerkar.api.protobuf;

import java.io.File;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

import org.jerkar.api.file.JkFileTree;
import org.jerkar.api.file.JkFileTreeSet;
import org.jerkar.api.file.JkPathFilter;
import org.jerkar.api.system.JkLog;
import org.jerkar.api.system.JkProcess;
import org.jerkar.api.utils.JkUtilsFile;
import org.jerkar.tool.builtins.javabuild.JkJavaBuild;

public class JkProtobuf {

    private static final String DefaultProtoDir = "src/main/proto";

    /**
     * Initializes a <code>JkProtobuf</code> using default settings
     */
    public static JkProtobuf of(JkJavaBuild build) {
        return new JkProtobuf(build);
    }
    
    private final String protocCommand;
    private final File workingDir;
    private final JkFileTreeSet protoDirs;
    private final JkFileTreeSet protoFiles;
    private final File tempDir;
    private final File javaDir;
    private List<String> extraArgs;
    
    private JkProtobuf(JkJavaBuild build) {
        this.protocCommand = "protoc";
        this.workingDir = build.baseDir().root();
        this.protoDirs = JkFileTreeSet.empty();
        this.protoFiles = JkFileTreeSet.of(build.file(DefaultProtoDir));
        this.tempDir = build.ouputDir("proto");
        this.javaDir = build.generatedSourceDir();
        this.extraArgs = new ArrayList<String>();
    }
    
    private JkProtobuf(String protocCommand, File workingDir, JkFileTreeSet protoDirs, JkFileTreeSet protoFiles, File tempDir, File javaDir, List<String> extraArgs) {
        this.protocCommand = protocCommand;
        this.workingDir = workingDir;
        this.protoDirs = protoDirs;
        this.protoFiles = protoFiles;
        this.tempDir = tempDir;
        this.javaDir = javaDir;
        this.extraArgs = extraArgs;
    }
    
    /**
     * Specify a path to protoc.
     */
    public JkProtobuf withProtoc(File protocFile) {
        return new JkProtobuf(protocFile.getAbsolutePath(), this.workingDir, this.protoDirs, this.protoFiles, this.tempDir, this.javaDir, this.extraArgs);
    }
    
    /**
     * Set the working directory for the protoc process.
     */
    public JkProtobuf withWorkingDir(File workingDir) {
        return new JkProtobuf(this.protocCommand, workingDir, this.protoDirs, this.protoFiles, this.tempDir, this.javaDir, this.extraArgs);
    }
    
    /**
     * Add a directory to lookup .proto files. See {@link andProtoDirs(JkFileTreeSet)}.
     */
    public JkProtobuf andProtoDir(File dir) {
        return andProtoDirs(dir);
    }
    
    /**
     * Add the src/main/proto directory from another project to lookup .proto files.
     * To use a different project, use {@link andProtoDirs(JkFileTreeSet)} instead.
     */
    public JkProtobuf andProtoDir(JkJavaBuild build) {
        return andProtoDir(build.file(DefaultProtoDir));
    }
    
    /**
     * Add directories to lookup .proto files. See {@link #andProtoDirs(JkFileTreeSet)}.
     */
    public JkProtobuf andProtoDirs(File ... dirs) {
        return new JkProtobuf(this.protocCommand, this.workingDir, this.protoDirs.and(dirs), this.protoFiles, this.tempDir, this.javaDir, this.extraArgs);
    }
    
    /**
     * Add a directory to lookup .proto files. See {@link #andProtoDirs(JkFileTreeSet)}.
     */
    public JkProtobuf andProtoDirs(JkFileTree ... dirs) {
        return new JkProtobuf(this.protocCommand, this.workingDir, this.protoDirs.and(dirs), this.protoFiles, this.tempDir, this.javaDir, this.extraArgs);
    }
    
    /**
     * Add directories to lookup .proto files.
     * These directories are passed to protoc with the --proto_path flag.
     * Only the root directory of each {@link JkFileTree} is used.
     * This does not specify individual .proto files, use {@link #andProtoFiles(JkFileTreeSet)} for that.
     */
    public JkProtobuf andProtoDirs(JkFileTreeSet dirs) {
        return new JkProtobuf(this.protocCommand, this.workingDir, this.protoDirs.and(dirs), this.protoFiles, this.tempDir, this.javaDir, this.extraArgs);
    }
    
    /**
     * Sets the .proto files to compile. See {@link #withProtoFiles(JkFileTreeSet)}.
     */
    public JkProtobuf withProtoFiles(JkFileTree files) {
        return withProtoFiles(JkFileTreeSet.of(files));
    }
    
    /**
     * Sets the .proto files to compile.
     * The parent directories of these files are added using {@link andProtoDirs(JkFileTreeSet)},
     * if the directory was not added already.
     */
    public JkProtobuf withProtoFiles(JkFileTreeSet files) {
        return new JkProtobuf(this.protocCommand, this.workingDir, this.protoDirs, files, this.tempDir, this.javaDir, this.extraArgs);
    }
    
    /**
     * Adds .proto files to compile. See {@link #withProtoFiles(JkFileTreeSet)}.
     */
    public JkProtobuf andProtoFiles(JkFileTree files) {
        return andProtoFiles(JkFileTreeSet.of(files));
    }
    
    /**
     * Adds .proto files to compile. See {@link #withProtoFiles(JkFileTreeSet)}.
     */
    public JkProtobuf andProtoFiles(JkFileTreeSet files) {
        return withProtoFiles(this.protoFiles.and(files));
    }
    
    /**
     * Sets the output java source directory for protoc.
     */
    public JkProtobuf withJavaDir(File javaDir) {
        return new JkProtobuf(this.protocCommand, this.workingDir, this.protoDirs, this.protoFiles, this.tempDir, javaDir, this.extraArgs);
    }
    
    /**
     * Sets the output java source directory for protoc.
     * If the {@link JkFileTreeSet} contains multiple directories, the first one is used.
     */
    public JkProtobuf withJavaDir(JkFileTreeSet treeSet) {
        if (treeSet.fileTrees().isEmpty()) {
            return this;
        }
        File dir = treeSet.fileTrees().get(0).root();
        if (treeSet.fileTrees().size() > 1) {
            JkLog.warn("java dir tree set has multiple directories, picked first one arbitrarily: " + dir);
        }
        return withJavaDir(dir);
    }
    
    /**
     * Adds extra args to the protoc command line invocation.
     */
    public JkProtobuf andExtraArgs(String ... extraArgs) {
        return andExtraArgs(Arrays.asList(extraArgs));
    }
    
    /**
     * Adds extra args to the protoc command line invocation.
     */
    public JkProtobuf andExtraArgs(List<String> extraArgs) {
        List<String> combinedArgs = new ArrayList<String>();
        combinedArgs.addAll(this.extraArgs);
        combinedArgs.addAll(extraArgs);
        return new JkProtobuf(this.protocCommand, this.workingDir, this.protoDirs, this.protoFiles, this.tempDir, this.javaDir, combinedArgs);
    }
    
    /**
     * launch the protoc process
     */
    public void compile() {
    
        // remind user of the args
        JkLog.info("Running protoc with :"
            + "\n\tcommand=" + protocCommand
            + "\n\tworking dir=" + workingDir
            + "\n\tproto dirs=" + protoDirs
            + "\n\tproto files=" + protoFiles
            + "\n\tjava dir=" + javaDir
            + "\n\textra args=" + extraArgs
        );
    
        // create dirs if needed
        this.tempDir.mkdirs();
        this.javaDir.mkdirs();
        
        // run protoc
        JkProcess.of(protocCommand, makeArgs())
            .withWorkingDir(workingDir)
            .failOnError(true)
            .runSync();
        
        // add generated annotations to generated files to suppress java compiler warnings
        final DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mmZ");
        dateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
        String dateString = dateFormat.format(new Date());
        String annotations = "@javax.annotation.Generated(value=\"com.google.protobuf\", date=\"" + dateString + "\")"
            + "\n@SuppressWarnings(\"all\")";
        
        JkFileTree javaFiles = JkFileTree.of(tempDir).andFilter(JkPathFilter.include("**/*.java"));
        for (File file : javaFiles) {
            String source = JkUtilsFile.read(file);
            source = source.replace("public final class", annotations + "\npublic final class");
            JkUtilsFile.writeString(file, source, false);
        }
        
        // move the altered java files to the java dir and cleanup
        javaFiles.copyTo(javaDir);
        JkUtilsFile.deleteDir(tempDir);
    }
    
    private String[] makeArgs() {
    
        List<String> args = new ArrayList<String>();
        
        // collect implicit proto paths from proto files
        JkFileTreeSet protoDirs = this.protoDirs;
        for (File file : protoFiles) {
            File protoDir = file.getParentFile();
            if (!hasDir(protoDirs, protoDir)) {
                protoDirs = protoDirs.and(protoDir);
            }
        }
        
        // add proto paths
        for (JkFileTree fileTree : protoDirs.fileTrees()) {
            args.add("--proto_path=" + fileTree.root().getAbsolutePath());
        }
        
        // set protoc java out to temp dir so we can transform java files afterwards
        if (javaDir != null) {
            args.add("--java_out=" + tempDir.getAbsolutePath());
        }
        
        // add proto files
        for (File file : protoFiles) {
            args.add(file.getAbsolutePath());
        }
        
        // add extra args
        args.addAll(extraArgs);
        
        String[] argsArray = new String[args.size()];
        args.toArray(argsArray);
        return argsArray;
    }
    
    private boolean hasDir(JkFileTreeSet dirs, File dir) {
        for(JkFileTree tree : dirs.fileTrees()) {
            if (tree.root().equals(dir)) {
                return true;
            }
        }
        return false;
    }
}
