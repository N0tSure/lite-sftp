package com.artemsirosh.lite.sftp.io;

import com.artemsirosh.lite.sftp.domain.Directory;
import com.artemsirosh.lite.sftp.domain.Item;
import com.artemsirosh.lite.sftp.errors.AbstractServiceException;
import com.artemsirosh.lite.sftp.port.outbound.CreateDirectoryPort;
import com.artemsirosh.lite.sftp.port.outbound.DeleteItemPort;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.lang.NonNull;
import org.springframework.util.Assert;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;

@Slf4j
@RequiredArgsConstructor
class LocalFileSystemService implements CreateDirectoryPort, DeleteItemPort {

    private final Path rootDirectory;

    void init() {
        Assert.isTrue(Files.exists(rootDirectory), "Root directory doesn't exists");
        Assert.isTrue(Files.isDirectory(rootDirectory), "Root directory is file");
    }

    @Override
    public void createDirectory(@NonNull final Directory directory) {
        final Path directoryPath = getItemPath(directory);

        try {
            Files.createDirectory(directoryPath);
            log.debug("Created directory: {}, path: {}", directory, directoryPath);
        } catch (FileAlreadyExistsException exc) {
            throw new ItemPathAlreadyExistsException(directory, exc);
        } catch (IOException exc) {
            throw new UncheckedIOException("Unable to create directory: '" + directoryPath + "'", exc);
        }
    }

    @Override
    public void deleteItem(@NonNull final Item item) {
        final Path itemPath = getItemPath(item);
        if (!Files.exists(itemPath)) {
            throw new ItemPathNotExistsException(itemPath);
        }

        try {
            if (item.isDirectory()) {
                log.debug("Deleting directory: {}", item.getId());
                Files.walkFileTree(itemPath, new DeletingPathVisitor());
            } else {
                log.debug("Deleting file: {}", item.getId());
                Files.delete(itemPath);
            }

            log.debug("Deleted item: {}", item.getId());

        } catch (IOException exc) {
            throw new UncheckedIOException("Unable to delete item: '" + itemPath + "'", exc);
        }
    }

    private Path getItemPath(final Item item) {
        Path parentPath = this.rootDirectory;
        for (final String name : item.calculatePath()) {
            parentPath = parentPath.resolve(name);
            if (!Files.exists(parentPath)) {
                throw new ItemPathNotExistsException(parentPath);
            }
        }
        log.debug("Resolved parent path '{}'", parentPath);

        final Path itemPath = parentPath.resolve(item.getName());
        log.debug("Resolved item path '{}'", itemPath);

        return itemPath;
    }

    private static class DeletingPathVisitor extends SimpleFileVisitor<Path> {
        @Override
        public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
            Files.delete(file);
            return FileVisitResult.CONTINUE;
        }

        @Override
        public FileVisitResult postVisitDirectory(Path dir, IOException exc) throws IOException {
            if (exc == null) {
                Files.delete(dir);
            } else {
                throw exc;
            }

            return FileVisitResult.CONTINUE;
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
