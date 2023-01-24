import dev.jeka.core.api.system.JkProcess;
import dev.jeka.core.tool.JkBean;
import dev.jeka.core.tool.JkDoc;
import dev.jeka.core.tool.JkInjectProject;
import dev.jeka.core.tool.builtins.project.ProjectJkBean;

// This build class is a special case where we cannot import subproject directly in code,
// as the sample needs the "plugin project" to be build prior compiling its build class
class Build extends JkBean {

    JkProcess pluginCommand = JkProcess.ofWinOrUx("jekaw" , "./jekaw").setWorkingDir("dev.jeka.plugins.protobuf.plugin");

    JkProcess sampleCommand = JkProcess.ofWinOrUx("jekaw" , "./jekaw").setWorkingDir("dev.jeka.plugins.protobuf.sample");

    public void buildPlugin() {
        pluginCommand.exec("project#clean project#pack", "-lv");
    }

    public void buildSample() {
        sampleCommand.exec("project#clean project#pack", "-lv");
    }

    @JkDoc("Build both Plugin and Sample projects")
    public void build() {
        buildPlugin();
        buildSample();
    }

    @JkDoc("Build, test and publish the Plugin to Maven Central")
    public void cicd() {
        build();

        // launch the main class to check that the protobuf generated class is usable.
        sampleCommand.exec("project#runJar");

        pluginCommand.exec("project#publish");
    }



}