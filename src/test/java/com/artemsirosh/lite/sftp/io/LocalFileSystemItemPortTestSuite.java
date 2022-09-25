package com.artemsirosh.lite.sftp.io;

import com.artemsirosh.lite.sftp.domain.Directory;
import com.artemsirosh.lite.sftp.domain.File;
import com.artemsirosh.lite.sftp.port.outbound.CreateDirectoryPort;
import com.artemsirosh.lite.sftp.port.outbound.CreateFilePort;
import com.artemsirosh.lite.sftp.port.outbound.DeleteItemPort;

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
        directoryPath = directoryPath.resolve(directory.getName());
        if (!Files.exists(directoryPath)) {
            Files.createDirectory(directoryPath);
        }

        return directoryPath;
    }

    Path createFile(final File file) throws IOException {
        final Path parent = createDirectory((Directory) file.getParent());
        return Files.createFile(parent.resolve(file.getName()));
    }

    CreateDirectoryPort getCreateDirectoryPort() {
        return service;
    }

    Path getDirectoryPath(final Directory directory) {
        final Path parentDirectoryPath = directory.calculatePath().stream()
                .reduce(temporaryDirectory, Path::resolve, (p1, p2) -> p2);

        return parentDirectoryPath.resolve(directory.getName());
    }

    DeleteItemPort getDeleteItemPort() {
        return service;
    }

    CreateFilePort getCreateFilePort() {
        return service;
    }
}
