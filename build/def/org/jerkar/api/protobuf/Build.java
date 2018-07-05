package org.jerkar.api.protobuf;

import org.jerkar.api.depmanagement.JkDependencySet;
import org.jerkar.api.depmanagement.JkMavenPublicationInfo;
import org.jerkar.api.depmanagement.JkPopularModules;
import org.jerkar.api.depmanagement.JkPublishRepos;
import org.jerkar.tool.JkOptions;
import org.jerkar.tool.builtins.java.JkJavaProjectBuild;
import org.jerkar.tool.builtins.repos.JkPluginPgp;

public class Build extends JkJavaProjectBuild {

    public boolean publishOssrh;

    @Override
    protected void configurePlugins() {
        super.configurePlugins();
        project().setVersionedModule( "org.jerkar:protobuf-plugin", "0.6_SNAPSHOT");
        project().setDependencies(JkDependencySet.of().and(JkPopularModules.JERKAR_CORE, "0.7.+"));
        project().setMavenPublicationInfo(mavenPublication());
        project().maker().setPublishRepos(publishRepositories());
    }

    private JkMavenPublicationInfo mavenPublication() {
        return JkMavenPublicationInfo.of("Jerkar plugin for protobuffer", "A Jerkar plugin for Google Protobuffer",
                        "http://jerkar.github.io")
                .withScm("https://github.com/jerkar/protobuf-plugin.git").andApache2License()
                .andGitHubDeveloper("cuchaz", "cuchaz@gmail.com")
                .andGitHubDeveloper("djeang", "djeangdev@yahoo.fr");
    }

    private JkPublishRepos publishRepositories() {
        if (publishOssrh) {
            return JkPublishRepos
                    .ossrh(JkOptions.get("repo.ossrh.username"), 
                            JkOptions.get("repo.ossrh.password"), this.plugins().get(JkPluginPgp.class).get())
                    .withUniqueSnapshot(true);
        }
        return JkPublishRepos.local();
    }
}
