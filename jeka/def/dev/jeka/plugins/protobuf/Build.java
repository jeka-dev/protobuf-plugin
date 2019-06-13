package dev.jeka.plugins.protobuf;

import dev.jeka.core.api.depmanagement.JkDependencySet;
import dev.jeka.core.api.depmanagement.JkMavenPublicationInfo;
import dev.jeka.core.api.depmanagement.JkRepoSet;
import dev.jeka.core.api.depmanagement.JkVersion;
import dev.jeka.core.api.java.JkJavaVersion;
import dev.jeka.core.api.java.project.JkJavaProject;
import dev.jeka.core.api.system.JkLocator;
import dev.jeka.core.api.tooling.JkGitWrapper;
import dev.jeka.core.tool.JkCommands;
import dev.jeka.core.tool.JkEnv;
import dev.jeka.core.tool.JkInit;
import dev.jeka.core.tool.builtins.java.JkPluginJava;

import static dev.jeka.core.api.depmanagement.JkJavaDepScopes.PROVIDED;

public class Build extends JkCommands {

    private final JkPluginJava javaPlugin = getPlugin(JkPluginJava.class);

    @JkEnv("OSSRH_USER")
    public String ossrhUser;

    @JkEnv("OSSRH_PWD")
    public String ossrhPwd;

    @Override
    protected void setup() {
        JkJavaProject project = javaPlugin.getProject();
        JkGitWrapper git = JkGitWrapper.of(getBaseDir());

        // Let Git drive project version numbering
        String projectVersion = git.getVersionWithTagOrSnapshot();
        project.setVersionedModule("dev.jeka:protobuf-plugin", projectVersion);
        project.getCompileSpec().setSourceAndTargetVersion(JkJavaVersion.V8);

        // Make javadoc only for releases
        if (!JkVersion.of(projectVersion).isSnapshot()) {
            javaPlugin.pack.javadoc = true;
        }

        // Use same Jeka version both for building and compiling
        project.addDependencies(JkDependencySet.of().andFile(JkLocator.getJekaJarPath(), PROVIDED));

        // Setup to publish on Maven Central
        javaPlugin.getProject().getMaker().getTasksForPublishing()
                .setMavenPublicationInfo(mavenPublicationInfo())
                .setPublishRepos(JkRepoSet.ofOssrhSnapshotAndRelease(ossrhUser, ossrhPwd));
    }

    private JkMavenPublicationInfo mavenPublicationInfo() {
        return JkMavenPublicationInfo.of("Jeka plugin for protobuf", "A Jeka plugin for Google Protocol buffer",
                        "https://org.jerkar/protobuf-plugin")
                .withScm("https://github.com/jerkar/protobuf-plugin.git")
                .andApache2License()
                .andGitHubDeveloper("cuchaz", "cuchaz@gmail.com")
                .andGitHubDeveloper("djeang", "djeangdev@yahoo.fr");
    }

    public static void main(String[] args) {
        JkPluginJava javaPlugin = JkInit.instanceOf(Build.class, args).javaPlugin;
        javaPlugin.clean().pack();
    }

}
