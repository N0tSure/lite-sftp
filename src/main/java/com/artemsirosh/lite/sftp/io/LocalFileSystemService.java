package com.artemsirosh.lite.sftp.io;

import com.artemsirosh.lite.sftp.domain.Directory;
import com.artemsirosh.lite.sftp.domain.Item;
import com.artemsirosh.lite.sftp.errors.AbstractServiceException;
import com.artemsirosh.lite.sftp.port.outbound.CreateDirectoryPort;
import lombok.RequiredArgsConstructor;
import org.springframework.lang.NonNull;
import org.springframework.util.Assert;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Files;
import java.nio.file.Path;

@RequiredArgsConstructor
class LocalFileSystemService implements CreateDirectoryPort {

    private final Path rootDirectory;

    void init() {
        Assert.isTrue(Files.exists(rootDirectory), "Root directory doesn't exists");
        Assert.isTrue(Files.isDirectory(rootDirectory), "Root directory is file");
    }

    @Override
    public void createDirectory(@NonNull final Directory directory) {
        Path parentPath = this.rootDirectory;
        for (final String name : directory.calculatePath()) {
            parentPath = parentPath.resolve(name);
            if (!Files.exists(parentPath)) {
               throw new ItemPathNotExistsException(parentPath);
            }
        }

        final Path directoryPath = parentPath.resolve(directory.getName());
        try {
            Files.createDirectory(directoryPath);
        } catch (FileAlreadyExistsException exc) {
           throw new ItemPathAlreadyExistsException(directory, exc);
        } catch (IOException exc) {
            throw new UncheckedIOException("Unable to create directory: '" + directoryPath + "'", exc);
        }
    }

    private static class ItemPathAlreadyExistsException extends AbstractServiceException {
        private ItemPathAlreadyExistsException(final Item item, final FileAlreadyExistsException exc) {
            super("Unable create Item: '" + item + "' it's already exists", exc);
        }
    }

    private static class ItemPathNotExistsException extends AbstractServiceException {
        private ItemPathNotExistsException(final Path path) {
            super("Item not exists: '" + path + "'");
        }
    }
}
