package loomtest;

import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.gradle.api.tasks.testing.Test;

public class ConventionsPlugin implements Plugin<Project> {

  @Override
  public void apply(Project project) {
    project.getPlugins().apply("org.springframework.boot");
    project.getPlugins().apply("io.spring.dependency-management");
    project.getPlugins().apply("java");
    project.getPlugins().apply("application");

    project.getRepositories().mavenCentral();

    project.getTasks().named("test").configure(
        test -> ((Test) test).useJUnitPlatform()
    );
  }

}
