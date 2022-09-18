package com.artemsirosh.lite.sftp.io;

import com.artemsirosh.lite.sftp.domain.Directory;
import com.artemsirosh.lite.sftp.domain.Item;
import com.artemsirosh.lite.sftp.domain.ItemId;
import com.artemsirosh.lite.sftp.errors.AbstractServiceException;
import com.artemsirosh.lite.sftp.port.outbound.CreateDirectoryPort;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

class CreateDirectoryPortTest {

    private static final Directory ALPHA = Directory.builder()
            .id(ItemId.newInstanceUUID())
            .name("alpha")
            .build();

    @TempDir
    Path rootDir;

    private CreateDirectoryPort service;

    @BeforeEach
    void setUpService() {
        this.service = new LocalFileSystemService(rootDir);
    }

    @Test
    @DisplayName("Should create directory w/o failures when it is a new directory")
    void test_00() {
        Assertions.assertThatCode(() -> service.createDirectory(ALPHA))
                .doesNotThrowAnyException();
    }

    @Test
    @DisplayName("Should create directory when it is a new directory")
    void test_01() {
        Assertions.assertThatCode(() -> service.createDirectory(ALPHA))
                .describedAs("Should create directory w/o errors")
                .doesNotThrowAnyException();

        Assertions.assertThat(rootDir.resolve(ALPHA.getName()))
                .describedAs("Created directory should exists")
                .exists();
    }

    @Test
    @DisplayName("Should create directory when new directory has parent")
    void test_05() throws IOException {

        final var bravo = "bravo";
        final Path alphaDirectoryPath = rootDir.resolve(ALPHA.getName());
        Files.createDirectory(alphaDirectoryPath);

        final Path actualDirectoryPath = rootDir.resolve(ALPHA.getName())
                .resolve(bravo);

        final var bravoItem = Directory.builder()
                .id(ItemId.newInstanceUUID())
                .name(bravo)
                .parent(ALPHA)
                .build();

        Assertions.assertThatCode(() -> service.createDirectory(bravoItem))
                .describedAs("Should create directory w/o errors")
                .doesNotThrowAnyException();

        Assertions.assertThat(actualDirectoryPath)
                .describedAs("Created directory should exists")
                .exists();
    }

    @Test
    @DisplayName("Should create directory when new directory deep hierarchy of parents")
    void test_02() throws IOException {
        final var newDirectoryName = "foxtrot";

        Path actualDirectoryPath = rootDir;
        Item parent = null;
        Directory directory;
        for (final String name : List.of("bravo", "charlie", "delta", "echo")) {
            actualDirectoryPath = actualDirectoryPath.resolve(name);
            Files.createDirectory(actualDirectoryPath);
            directory = Directory.builder()
                    .id(ItemId.newInstanceUUID())
                    .name(name)
                    .parent(parent)
                    .build();

            parent = directory;
        }

        final var newDirectory = Directory.builder()
                .id(ItemId.newInstanceUUID())
                .name(newDirectoryName)
                .parent(parent)
                .build();

        Assertions.assertThatCode(() -> service.createDirectory(newDirectory))
                .describedAs("Should create directory w/o errors")
                .doesNotThrowAnyException();

        Assertions.assertThat(actualDirectoryPath.resolve(newDirectoryName))
                .describedAs("Created directory should exists")
                .exists();
    }

    @Test
    @DisplayName("Should fail directory creation when same directory exists")
    void test_03() throws IOException {
        Files.createDirectory(rootDir.resolve(ALPHA.getName()));
        Assertions.assertThatCode(() -> service.createDirectory(ALPHA))
                .isInstanceOf(AbstractServiceException.class);
    }

    @Test
    @DisplayName("Should fail directory creation when parent directory doesn't exist")
    void test_04() {
        final var newDirectory = Directory.builder()
                .id(ItemId.newInstanceUUID())
                .name("bravo")
                .parent(ALPHA)
                .build();

        Assertions.assertThatCode(() -> service.createDirectory(newDirectory))
                .isInstanceOf(AbstractServiceException.class);
    }
}