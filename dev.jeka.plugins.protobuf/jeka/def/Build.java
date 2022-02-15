import dev.jeka.core.api.crypto.gpg.JkGpg;
import dev.jeka.core.api.depmanagement.JkRepoSet;
import dev.jeka.core.api.java.JkJavaVersion;
import dev.jeka.core.api.project.JkProject;
import dev.jeka.core.api.system.JkLocator;
import dev.jeka.core.tool.JkBean;
import dev.jeka.core.tool.JkInit;
import dev.jeka.core.tool.JkInjectProperty;
import dev.jeka.core.tool.JkJekaVersionCompatibilityChecker;
import dev.jeka.core.tool.builtins.project.ProjectJkBean;
import dev.jeka.core.tool.builtins.release.VersionFromGitJkBean;

public class Build extends JkBean {

    private final ProjectJkBean projectJkBean = getBean(ProjectJkBean.class).configure(this::configure);

    @JkInjectProperty("OSSRH_USER")
    public String ossrhUser;

    @JkInjectProperty("OSSRH_PWD")
    public String ossrhPwd;

    final VersionFromGitJkBean versionFromGitJkBean = getBean(VersionFromGitJkBean.class);

    private void configure(JkProject project) {
        JkJekaVersionCompatibilityChecker.setCompatibilityRange(project.getConstruction().getManifest(),
                "0.9.20.RC17",
                "https://raw.githubusercontent.com/jerkar/protobuf-plugin/breaking_versions.txt");
        project.simpleFacade()
                .setJvmTargetVersion(JkJavaVersion.V8)
                .mixResourcesAndSources()
                .useSimpleLayout()
                .includeJavadocAndSources(false, true)
                .configureCompileDeps(deps -> deps
                        .andFiles(JkLocator.getJekaJarPath())
                )
                .configureRuntimeDeps(deps -> deps
                        .minus(JkLocator.getJekaJarPath())
                );
        project.getPublication()
                .setModuleId("dev.jeka:protobuf-plugin")
                .setRepos(JkRepoSet.ofOssrhSnapshotAndRelease(ossrhUser, ossrhPwd,
                        JkGpg.ofStandardProject(getBaseDir()).getSigner("")))
                .getMaven()
                .   getPomMetadata()
                        .setProjectName("Jeka plugin for protobuf")
                        .setProjectDescription("A Jeka plugin for Google Protocol buffer")
                        .setProjectUrl("https://github.com/jerkar/spring-boot-plugin")
                        .setScmUrl("https://github.com/jerkar/spring-boot-plugin")
                        .addApache2License()
                        .addGithubDeveloper("cuchaz", "cuchaz@gmail.com")
                    .addGithubDeveloper("djeang", "djeangdev@yahoo.fr");
    }

    public void cleanPack() {
        clean(); projectJkBean.pack();
    }

    public static void main(String[] args) {
        JkInit.instanceOf(Build.class, args).cleanPack();
    }

}
