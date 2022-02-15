import dev.jeka.core.api.java.JkJavaVersion;
import dev.jeka.core.api.project.JkProject;
import dev.jeka.core.api.project.JkSourceGenerator;
import dev.jeka.core.tool.JkBean;
import dev.jeka.core.tool.JkInit;
import dev.jeka.core.tool.JkInjectClasspath;
import dev.jeka.core.tool.builtins.project.ProjectJkBean;
import dev.jeka.plugins.protobuf.JkProtobuf;
import dev.jeka.plugins.protobuf.JkProtocSourceGenerator;

@JkInjectClasspath("../dev.jeka.plugins.protobuf/jeka/output/dev.jeka.protobuf-plugin.jar")
class Build extends JkBean {

    ProjectJkBean projectJkBean = getBean(ProjectJkBean.class).configure(this::configure);

    private void configure(JkProject project) {
        project.simpleFacade()
            .setJvmTargetVersion(JkJavaVersion.V8)
            .configureCompileDeps(deps -> deps
                .and("com.google.guava:guava:21.0")
                .and(JkProtobuf.PROTOBUF_MODULE.version("3.13.0"))
            );
        JkSourceGenerator protocGenerator = JkProtocSourceGenerator.of(project, "src/main/proto");
        project.getConstruction().getCompilation().addSourceGenerator(protocGenerator);
    }

    public void cleanPack() {
        clean(); projectJkBean.getProject().pack();
    }

    public static void main(String[] args) {
        JkInit.instanceOf(Build.class).cleanPack();
    }

}