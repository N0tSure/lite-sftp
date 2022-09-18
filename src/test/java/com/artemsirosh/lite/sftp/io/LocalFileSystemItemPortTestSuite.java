package com.artemsirosh.lite.sftp.io;

import com.artemsirosh.lite.sftp.domain.Directory;
import com.artemsirosh.lite.sftp.port.outbound.CreateDirectoryPort;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

abstract class LocalFileSystemItemPortTestSuite {

    private Path temporaryDirectory;
    private LocalFileSystemService service;

    void initialize(final Path temporaryDirectory) {
        this.temporaryDirectory = temporaryDirectory;
        this.service = new LocalFileSystemService(temporaryDirectory);
        this.service.init();
    }

    Path createDirectory(final Directory directory) throws IOException {
        Path directoryPath = temporaryDirectory;
        for (final String name : directory.calculatePath()) {
            directoryPath = directoryPath.resolve(name);
            if (!Files.exists(directoryPath)) {
                Files.createDirectory(directoryPath);
            }
        }

        return Files.createDirectory(directoryPath.resolve(directory.getName()));
    }

    Path createDirectory(final String name) throws IOException {
        return Files.createDirectory(temporaryDirectory.resolve(name));
    }

    Path createDirectory(final String name, final Path path) throws IOException {
        return Files.createDirectory(path.resolve(name));
    }

    CreateDirectoryPort getCreateDirectoryPort() {
        return service;
    }

    Path getDirectoryPath(final Directory directory) {
        return directory.calculatePath().stream().reduce(temporaryDirectory, Path::resolve, (p1, p2) -> p2);
    }
}
