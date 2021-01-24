import dev.jeka.core.api.depmanagement.JkDependencySet;
import dev.jeka.core.api.depmanagement.JkRepoSet;
import dev.jeka.core.api.depmanagement.JkScope;
import dev.jeka.core.api.java.JkJavaVersion;
import dev.jeka.core.api.system.JkLocator;
import dev.jeka.core.api.tooling.JkGitWrapper;
import dev.jeka.core.tool.JkCommandSet;
import dev.jeka.core.tool.JkEnv;
import dev.jeka.core.tool.JkInit;
import dev.jeka.core.tool.builtins.java.JkPluginJava;

public class Build extends JkCommandSet {

    private final JkPluginJava java = getPlugin(JkPluginJava.class);

    @JkEnv("OSSRH_USER")
    public String ossrhUser;

    @JkEnv("OSSRH_PWD")
    public String ossrhPwd;

    @Override
    protected void setup() {
        java.getProject()
            .getConstruction()
                .getDependencyManagement().addDependencies(JkDependencySet.of()
                    .andFile(JkLocator.getJekaJarPath(), JkScope.PROVIDED)
                    .and("com.github.os72:protoc-jar:3.11.4")).__
                .getCompilation()
                    .setJavaVersion(JkJavaVersion.V8).__.__
            .getPublication()
                .setVersion(JkGitWrapper.of().getVersionFromTags())
                .setModuleId("dev.jeka:protobuf-plugin")
                .setRepos(JkRepoSet.ofOssrhSnapshotAndRelease(ossrhUser, ossrhPwd))
                .getMavenPublication()
                    .getPomMetadata()
                        .getProjectInfo()
                            .setName("Jeka plugin for protobuf")
                            .setDescription("A Jeka plugin for Google Protocol buffer")
                            .setUrl("https://org.jerkar/protobuf-plugin").__
                        .getScm()
                            .setUrl("https://github.com/jerkar/protobuf-plugin.git").__
                        .addApache2License()
                        .addGithubDeveloper("cuchaz", "cuchaz@gmail.com")
                        .addGithubDeveloper("djeang", "djeangdev@yahoo.fr");

        // Make javadoc only for releases
        if (!java.getProject().getPublication().getVersion().isSnapshot()) {
            java.pack.javadoc = true;
        }

    }

    public void cleanPack() {
        clean(); java.pack();
    }

    public static void main(String[] args) {
        JkInit.instanceOf(Build.class, args).cleanPack();
    }

}
