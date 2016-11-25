package org.jerkar.api.protobuf;

import org.jerkar.CoreBuild;
import org.jerkar.api.depmanagement.JkDependencies;
import org.jerkar.api.depmanagement.JkMavenPublication;
import org.jerkar.api.depmanagement.JkMavenPublicationInfo;
import org.jerkar.api.depmanagement.JkModuleId;
import org.jerkar.api.depmanagement.JkPublishRepos;
import org.jerkar.api.depmanagement.JkVersion;
import org.jerkar.tool.JkOptions;
import org.jerkar.tool.JkProject;
import org.jerkar.tool.builtins.javabuild.JkJavaBuild;

public class Build extends JkJavaBuild {

    public boolean publishOssrh;

    @JkProject("../jerkar/org.jerkar.core")
    private CoreBuild core;

    @Override
    public JkModuleId moduleId() {
        return JkModuleId.of("org.jerkar", "protobuf-pluggin");
    }

    @Override
    public JkVersion version() {
        return JkVersion.ofName("1.0-SNAPHOT");  
    }

    @Override
    public String javaSourceVersion() {
        return "1.6"; // It's not necessary to stick with V6
    }

    @Override
    protected JkDependencies dependencies() {
        return JkDependencies.of(PROVIDED, core.asDependency(core.packer().jarFile()));
    }

    @Override
    protected JkMavenPublication mavenPublication() {
        return super.mavenPublication().with(JkMavenPublicationInfo
                .of("Jerkar plugin for protobuffer", "A Jerkar plugin for Google Protobuffer",
                        "http://jerkar.github.io")
                .withScm("https://github.com/jerkar/protobuf-plugin.git").andApache2License()
                .andGitHubDeveloper("cuchaz", "cuchaz").andGitHubDeveloper("djeang", "djeangdev@yahoo.fr"));
    }

    @Override
    protected JkPublishRepos publishRepositories() {
        if (publishOssrh) {
            return JkPublishRepos
                    .ossrh(JkOptions.get("repo.ossrh.username"), 
                            JkOptions.get("repo.ossrh.password"), pgp())
                    .withUniqueSnapshot(true);
        }
        return super.publishRepositories();
    }
}
