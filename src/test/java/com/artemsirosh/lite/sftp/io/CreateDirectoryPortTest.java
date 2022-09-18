package com.artemsirosh.lite.sftp.io;

import com.artemsirosh.lite.sftp.domain.Directory;
import com.artemsirosh.lite.sftp.domain.ItemId;
import com.artemsirosh.lite.sftp.errors.AbstractServiceException;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;

class CreateDirectoryPortTest extends LocalFileSystemItemPortTestSuite {

    private static final Directory ALPHA = Directory.builder()
            .id(ItemId.newInstanceUUID())
            .name("alpha")
            .build();

    @TempDir
    Path rootDir;

    @BeforeEach
    void setUpService() {
        this.initialize(rootDir);
    }

    @Test
    @DisplayName("Should create directory w/o failures when it is a new directory")
    void test_00() {
        Assertions.assertThatCode(() -> getCreateDirectoryPort().createDirectory(ALPHA))
                .doesNotThrowAnyException();
    }

    @Test
    @DisplayName("Should create directory when it is a new directory")
    void test_01() {
        Assertions.assertThatCode(() -> getCreateDirectoryPort().createDirectory(ALPHA))
                .describedAs("Should create directory w/o errors")
                .doesNotThrowAnyException();

        Assertions.assertThat(getDirectoryPath(ALPHA))
                .describedAs("Created directory should exists")
                .exists();
    }

    @Test
    @DisplayName("Should create directory when new directory has parent")
    void test_05() throws IOException {

        createDirectory(ALPHA);
        final var bravoDirectory = Directory.builder()
                .id(ItemId.newInstanceUUID())
                .name("bravo")
                .parent(ALPHA)
                .build();

        Assertions.assertThatCode(() -> getCreateDirectoryPort().createDirectory(bravoDirectory))
                .describedAs("Should create directory w/o errors")
                .doesNotThrowAnyException();

        final Path actualDirectoryPath = getDirectoryPath(bravoDirectory);
        Assertions.assertThat(actualDirectoryPath)
                .describedAs("Created directory should exists")
                .exists();
    }

    @Test
    @DisplayName("Should create directory when new directory deep hierarchy of parents")
    void test_02() throws IOException {
        Directory parent = ALPHA;
        Directory directory = null;
        for (final String name : List.of("bravo", "charlie", "delta", "echo")) {
            createDirectory(parent);
            directory = Directory.builder()
                    .id(ItemId.newInstanceUUID())
                    .name(name)
                    .parent(parent)
                    .build();

            parent = directory;
        }

        final Directory echoDirectory = directory;
        Assertions.assertThatCode(() -> getCreateDirectoryPort().createDirectory(echoDirectory))
                .describedAs("Should create directory w/o errors")
                .doesNotThrowAnyException();

        Assertions.assertThat(getDirectoryPath(echoDirectory))
                .describedAs("Created directory should exists")
                .exists();
    }

    @Test
    @DisplayName("Should fail directory creation when same directory exists")
    void test_03() throws IOException {
        createDirectory(ALPHA);
        Assertions.assertThatCode(() -> getCreateDirectoryPort().createDirectory(ALPHA))
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

        Assertions.assertThatCode(() -> getCreateDirectoryPort().createDirectory(newDirectory))
                .isInstanceOf(AbstractServiceException.class);
    }
}