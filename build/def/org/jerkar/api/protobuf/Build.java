package org.jerkar.api.protobuf;

import org.jerkar.api.depmanagement.JkDependencies;
import org.jerkar.api.depmanagement.JkMavenPublication;
import org.jerkar.api.depmanagement.JkMavenPublicationInfo;
import org.jerkar.api.depmanagement.JkModuleId;
import org.jerkar.api.depmanagement.JkPopularModules;
import org.jerkar.api.depmanagement.JkPublishRepos;
import org.jerkar.api.depmanagement.JkVersion;
import org.jerkar.tool.JkOptions;
import org.jerkar.tool.builtins.javabuild.JkJavaBuild;

public class Build extends JkJavaBuild {

    public boolean publishOssrh;

    @Override
    public JkModuleId moduleId() {
        return JkModuleId.of("org.jerkar", "protobuf-plugin");
    }

    @Override
    public JkVersion version() {
        return JkVersion.ofName("0.5");  
    }

    @Override
    protected JkDependencies dependencies() {
        return JkDependencies.builder()
                .on(JkPopularModules.JERKAR_CORE, "0.4.6")
                .build();
    }

    @Override
    protected JkMavenPublication mavenPublication() {
        return super.mavenPublication().with(JkMavenPublicationInfo
                .of("Jerkar plugin for protobuffer", "A Jerkar plugin for Google Protobuffer",
                        "http://jerkar.github.io")
                .withScm("https://github.com/jerkar/protobuf-plugin.git").andApache2License()
                .andGitHubDeveloper("cuchaz", "cuchaz@gmail.com").andGitHubDeveloper("djeang", "djeangdev@yahoo.fr"));
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
