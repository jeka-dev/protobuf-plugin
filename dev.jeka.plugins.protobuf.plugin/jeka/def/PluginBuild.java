import dev.jeka.core.api.crypto.gpg.JkGpg;
import dev.jeka.core.api.depmanagement.JkRepoSet;
import dev.jeka.core.api.java.JkJavaVersion;
import dev.jeka.core.api.project.JkProject;
import dev.jeka.core.api.system.JkLocator;
import dev.jeka.core.tool.JkBean;
import dev.jeka.core.tool.JkInjectProperty;
import dev.jeka.core.tool.JkJekaVersionCompatibilityChecker;
import dev.jeka.core.tool.builtins.project.ProjectJkBean;

public class PluginBuild extends JkBean {

    private final ProjectJkBean projectJkBean = getBean(ProjectJkBean.class).configure(this::configure);

    @JkInjectProperty("OSSRH_USER")
    public String ossrhUser;

    @JkInjectProperty("OSSRH_PWD")
    public String ossrhPwd;

    private void configure(JkProject project) {
        JkJekaVersionCompatibilityChecker.setCompatibilityRange(project.packaging.manifest,
                "0.10.5",
                "https://raw.githubusercontent.com/jerkar/protobuf-plugin/breaking_versions.txt");
        project.flatFacade()
            .setJvmTargetVersion(JkJavaVersion.V8)
            .useSimpleLayout()
            .includeJavadocAndSources(false, true)
            .configureCompileDependencies(deps -> deps
                    .andFiles(JkLocator.getJekaJarPath())
            )
            .configureRuntimeDependencies(deps -> deps
                    .minus(JkLocator.getJekaJarPath())
            );
        project.publication
            .setModuleId("dev.jeka:protobuf-plugin")
            .setRepos(JkRepoSet.ofOssrhSnapshotAndRelease(ossrhUser, ossrhPwd,
                    JkGpg.ofStandardProject(getBaseDir()).getSigner("")))
            .maven.pomMetadata
                    .setProjectName("Jeka plugin for protobuf")
                    .setProjectDescription("A Jeka plugin for Google Protocol buffer")
                    .setProjectUrl("https://github.com/jerkar/spring-boot-plugin")
                    .setScmUrl("https://github.com/jerkar/spring-boot-plugin")
                    .addApache2License()
                    .addGithubDeveloper("cuchaz", "cuchaz@gmail.com")
                    .addGithubDeveloper("djeang", "djeangdev@yahoo.fr");
    }

    public void cleanPack() {
        projectJkBean.clean(); projectJkBean.pack();
    }

}
