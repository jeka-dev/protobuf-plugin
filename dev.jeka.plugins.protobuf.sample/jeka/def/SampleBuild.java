import dev.jeka.core.api.java.JkJavaVersion;
import dev.jeka.core.api.project.JkProject;
import dev.jeka.core.api.project.JkSourceGenerator;
import dev.jeka.core.tool.JkBean;
import dev.jeka.core.tool.JkInjectClasspath;
import dev.jeka.core.tool.builtins.project.ProjectJkBean;
import dev.jeka.plugins.protobuf.JkProtocSourceGenerator;

@JkInjectClasspath("../dev.jeka.plugins.protobuf.plugin/jeka/output/dev.jeka.protobuf-plugin.jar")
class SampleBuild extends JkBean {

    ProjectJkBean projectKBean = getBean(ProjectJkBean.class).configure(this::configure);

    private void configure(JkProject project) {
        project.flatFacade()
            .setJvmTargetVersion(JkJavaVersion.V8)
            .configureCompileDependencies(deps -> deps
                .and("com.google.guava:guava:21.0")
                .and("com.google.protobuf:protobuf-java:3.21.12")
            );
        project.packaging.manifest.addMainClass("Sample");
        project.artifactProducer.putMainArtifact(project.packaging::createFatJar);
        JkSourceGenerator protocGenerator = JkProtocSourceGenerator.of(project, "src/main/proto");
        project.prodCompilation.addSourceGenerator(protocGenerator);
    }

    public void cleanPack() {
        projectKBean.clean(); projectKBean.pack();
    }

}