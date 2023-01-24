import dev.jeka.core.api.system.JkProcess;
import dev.jeka.core.tool.JkBean;
import dev.jeka.core.tool.JkInjectProject;
import dev.jeka.core.tool.builtins.project.ProjectJkBean;


class Build extends JkBean {

    public void buildPlugin() {
        JkProcess.ofWinOrUx("jekaw" , "./jekaw")
                .setWorkingDir("dev.jeka.plugins.protobuf.plugin").exec("cleanPack", "-lv");
    }

    public void buildSample() {
        JkProcess.ofWinOrUx("jekaw" , "./jekaw")
                .setWorkingDir("dev.jeka.plugins.protobuf.sample").exec("cleanPack", "-lv");
    }

    public void build() {
        buildPlugin();
        buildSample();
    }

}