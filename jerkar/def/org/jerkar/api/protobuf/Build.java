package org.jerkar.api.protobuf;

import org.jerkar.api.depmanagement.JkDependencySet;
import org.jerkar.api.depmanagement.JkMavenPublicationInfo;
import org.jerkar.api.depmanagement.JkPopularModules;
import org.jerkar.api.depmanagement.JkRepoSet;
import org.jerkar.api.java.project.JkJavaProject;
import org.jerkar.api.system.JkProcess;
import org.jerkar.tool.JkInit;
import org.jerkar.tool.JkRun;
import org.jerkar.tool.builtins.java.JkPluginJava;

public class Build extends JkRun {

    private final JkPluginJava javaPlugin = getPlugin(JkPluginJava.class);

    public String ossrhUsername;

    public String ossrhPwd;

    @Override
    protected void setup() {
        JkJavaProject project = javaPlugin.getProject();
        project.setVersionedModule( "org.jerkar:protobuf-plugin", "0.7.0.RC1");
        project.setDependencies(JkDependencySet.of().and(JkPopularModules.JERKAR_CORE, "0.7.0.RC1"));
        project.setMavenPublicationInfo(mavenPublication());
        if (!project.getVersionedModule().getVersion().isSnapshot()) {
            javaPlugin.pack.javadoc = true;
            javaPlugin.publish.signArtifacts = true;
            project.getMaker().getTasksForPublishing().getPostActions().chain(() -> {
                String tagName = project.getVersionedModule().getVersion().toString();
                JkProcess git = JkProcess.of("git").withFailOnError(true);
                git.andParams("tag", "-a", tagName, "-m", "Release").runSync();
                git.andParams("push").runSync();
                git.andParams("push", "origin", tagName).runSync();
            });
        }
    }

    @Override
    public void setupAfterPluginActivations() {
        JkJavaProject project = javaPlugin.getProject();
        project.getMaker().getTasksForPublishing().setPublishRepos(JkRepoSet.ofOssrhSnapshotAndRelease(ossrhUsername, ossrhPwd));
    }

    private JkMavenPublicationInfo mavenPublication() {
        return JkMavenPublicationInfo.of("Jerkar plugin for protobuffer", "A Jerkar plugin for Google Protobuffer",
                        "http://jerkar.github.io")
                .withScm("https://github.com/jerkar/protobuf-plugin.git").andApache2License()
                .andGitHubDeveloper("cuchaz", "cuchaz@gmail.com")
                .andGitHubDeveloper("djeang", "djeangdev@yahoo.fr");
    }

    public static void main(String[] args) {
        Build build = JkInit.instanceOf(Build.class, args);
        build.javaPlugin.clean().pack();
        build.javaPlugin.publish();
    }


}
