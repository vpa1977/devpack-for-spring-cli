package com.canonical.devpackspring.build.gradle;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import org.apache.commons.io.FileUtils;

public class TempProjectAdapter implements AutoCloseable {
    private final Path projectPath;
    public TempProjectAdapter(Path curProject) throws IOException {
        projectPath = Files.createTempDirectory(curProject.getFileName().toString());
        File[] files = curProject.toFile().listFiles();
        if (files == null) {
            return;
        }
        for (File f : files) {
            Files.createSymbolicLink(projectPath.resolve(f.getName()), f.toPath());
        }
    }

    public Path getProjectPath() {
        return projectPath;
    }

    @Override
    public void close() throws Exception {
        FileUtils.deleteDirectory(projectPath.toFile());
    }
}
