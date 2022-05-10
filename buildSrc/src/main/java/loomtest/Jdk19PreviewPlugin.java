package loomtest;

import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.gradle.api.plugins.JavaApplication;
import org.gradle.api.plugins.JavaPluginExtension;
import org.gradle.api.tasks.compile.JavaCompile;
import org.gradle.api.tasks.testing.Test;
import org.gradle.jvm.toolchain.JavaLanguageVersion;

import java.util.List;

public class Jdk19PreviewPlugin implements Plugin<Project> {

  private static final String ENABLE_PREVIEW = "--enable-preview";

  @Override
  public void apply(Project project) {
    configureToolchain(project);
    enablePreviewForBootRun(project);
    enablePreviewForCompilation(project);
    enablePreviewForTests(project);
  }

  private void configureToolchain(Project project) {
    var toolchainSpec = project.getExtensions().getByType(JavaPluginExtension.class).getToolchain();
    toolchainSpec.getLanguageVersion().set(JavaLanguageVersion.of(19));
  }

  private void enablePreviewForBootRun(Project project) {
    project.getExtensions().getByType(JavaApplication.class).setApplicationDefaultJvmArgs(List.of(ENABLE_PREVIEW));
  }

  private void enablePreviewForCompilation(Project project) {
    project.getTasks().withType(
        JavaCompile.class,
        compileTask -> compileTask.getOptions().getCompilerArgs().add(ENABLE_PREVIEW)
    );
  }

  private void enablePreviewForTests(Project project) {
    project.getTasks().named("test").configure(
        test -> ((Test) test).jvmArgs(ENABLE_PREVIEW)
    );
  }

}
