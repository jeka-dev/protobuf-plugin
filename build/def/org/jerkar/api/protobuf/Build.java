package org.jerkar.api.protobuf;


import org.jerkar.CoreBuild;
import org.jerkar.api.depmanagement.JkDependencies;
import org.jerkar.api.depmanagement.JkModuleId;
import org.jerkar.api.depmanagement.JkVersion;
import org.jerkar.tool.JkProject;
import org.jerkar.tool.builtins.javabuild.JkJavaBuild;

public class Build extends JkJavaBuild {

    @JkProject("../jerkar/org.jerkar.core")
    private CoreBuild core;
    
    @Override
    public JkModuleId moduleId() {
        return JkModuleId.of("org.jerkar", "protobuf");
    }
    
    @Override
    public JkVersion version() {
        return JkVersion.ofName("1.0");
    }
    
    @Override
    public String javaSourceVersion() {
        return "1.6";
    }

    @Override
    protected JkDependencies dependencies() {
        return JkDependencies.of(PROVIDED, core.asDependency(core.packer().jarFile()));
    }
}
