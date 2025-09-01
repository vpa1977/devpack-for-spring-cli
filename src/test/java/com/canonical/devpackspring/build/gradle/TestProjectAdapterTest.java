package com.canonical.devpackspring.build.gradle;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;
import org.springframework.cli.support.IntegrationTestSupport;
import org.springframework.cli.support.MockConfigurations;

import java.nio.file.Path;

import static org.assertj.core.api.Assertions.assertThat;

public class TestProjectAdapterTest {
    private final ApplicationContextRunner contextRunner = new ApplicationContextRunner()
            .withUserConfiguration(MockConfigurations.MockBaseConfig.class);

    @Test
    void testCloneProject(final @TempDir Path workingDir) {
        Path projectPath = Path.of("test-data").resolve("projects").resolve("gradle-kotlin");
        IntegrationTestSupport.installInWorkingDirectory(projectPath, workingDir);
        contextRunner.withUserConfiguration(MockConfigurations.MockUserConfig.class).run(context -> {
            Path clonedPath = null;
            try (TempProjectAdapter adapter = new TempProjectAdapter(workingDir)) {
                clonedPath = adapter.getProjectPath();
                assertThat(clonedPath.resolve("gradle")).exists();
                assertThat(clonedPath.resolve("gradle/wrapper/gradle-wrapper.properties")).exists();
            }
            assertThat(clonedPath).doesNotExist();
        });

    }
}
