package com.artemsirosh.lite.sftp.io;

import com.artemsirosh.lite.sftp.domain.Directory;
import com.artemsirosh.lite.sftp.domain.File;
import com.artemsirosh.lite.sftp.domain.ItemId;
import com.artemsirosh.lite.sftp.errors.AbstractServiceException;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.file.Path;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThatCode;

class DeleteItemPortTest extends LocalFileSystemItemPortTestSuite {

    private static final Directory ALPHA = Directory.builder()
            .id(ItemId.newInstanceUUID())
            .name("alpha")
            .build();

    @TempDir
    Path rootDirectory;

    @BeforeEach
    void setUp() {
        initialize(rootDirectory);
    }

    @Test
    @DisplayName("Should delete item w/o failures when Item exists")
    void test_00() throws IOException {
        createDirectory(ALPHA);
        assertThatCode(() -> getDeleteItemPort().deleteItem(ALPHA))
                .doesNotThrowAnyException();
    }

    @Test
    @DisplayName("Should delete item when Item exists")
    void test_01() throws IOException {
        final Path actualDirectoryPath = createDirectory(ALPHA);
        assertThatCode(() -> getDeleteItemPort().deleteItem(ALPHA))
                .describedAs("Should delete Item w/o failures")
                .doesNotThrowAnyException();

        Assertions.assertThat(actualDirectoryPath)
                .describedAs("Directory should be removed")
                .doesNotExist();
    }

    @Test
    @DisplayName("Should delete item when Item exists and have a parent")
    void test_02() throws IOException {
        final Directory bravoDirectory = Directory.builder()
                .id(ItemId.newInstanceUUID())
                .parent(ALPHA)
                .name("bravo")
                .build();

        final Path actualDirectoryPath = createDirectory(bravoDirectory);
        assertThatCode(() -> getDeleteItemPort().deleteItem(bravoDirectory))
                .describedAs("Should delete Item w/o failures")
                .doesNotThrowAnyException();

        Assertions.assertThat(actualDirectoryPath)
                .describedAs("Directory should be removed")
                .doesNotExist();
    }

    @Test
    @DisplayName("Should remain Parent after Item delete when Item exists and has parent")
    void test_03() throws IOException {
        final Directory bravoDirectory = Directory.builder()
                .id(ItemId.newInstanceUUID())
                .parent(ALPHA)
                .name("bravo")
                .build();

        createDirectory(bravoDirectory);
        assertThatCode(() -> getDeleteItemPort().deleteItem(bravoDirectory))
                .describedAs("Should delete Item w/o failures")
                .doesNotThrowAnyException();

        Assertions.assertThat(getDirectoryPath(ALPHA))
                .describedAs("Should remain parent directory")
                .exists();
    }

    @Test
    @DisplayName("Should delete item when Item exists and have deep hierarchy of parents")
    void test_04() throws IOException {
        final Directory directory = Stream.of("bravo", "charlie", "delta", "echo")
                .reduce(
                        ALPHA,
                        (parent0, name) -> Directory.builder()
                                .id(ItemId.newInstanceUUID())
                                .name(name)
                                .parent(parent0)
                                .build(),
                        (d1, d2) -> d2
                );

        final Path actualDirectoryPath = createDirectory(directory);
        assertThatCode(() -> getDeleteItemPort().deleteItem(directory))
                .describedAs("Should delete Item w/o failures")
                .doesNotThrowAnyException();

        Assertions.assertThat(actualDirectoryPath)
                .describedAs("Directory should be removed")
                .doesNotExist();
    }

    @Test
    @DisplayName("Should delete Item when Item is Directory")
    void test_05() throws IOException {
        final Path actualDirectoryPath = createDirectory(ALPHA);
        assertThatCode(() -> getDeleteItemPort().deleteItem(ALPHA))
                .describedAs("Should delete Item w/o failures")
                .doesNotThrowAnyException();

        Assertions.assertThat(actualDirectoryPath)
                .describedAs("Directory should be removed")
                .doesNotExist();
    }

    @Test
    @DisplayName("Should delete Item when Item is File")
    void test_06() throws IOException {
        final File file = File.builder()
                .id(ItemId.newInstanceUUID())
                .name("sample")
                .parent(ALPHA)
                .build();
        final Path actualFilePath = createFile(file);

        assertThatCode(() -> getDeleteItemPort().deleteItem(file))
                .describedAs("Should delete Item w/o failures")
                .doesNotThrowAnyException();

        Assertions.assertThat(actualFilePath)
                .describedAs("File should be removed")
                .doesNotExist();
    }

    @Test
    @DisplayName("Should delete Directory when Directory has children")
    void test_07() throws IOException {
        final Directory bravoDirectory = Directory.builder()
                .id(ItemId.newInstanceUUID())
                .name("bravo")
                .parent(ALPHA)
                .build();
        createDirectory(bravoDirectory);

        final File sampleFile = File.builder()
                .id(ItemId.newInstanceUUID())
                .name("sample.txt")
                .parent(ALPHA)
                .build();
        createFile(sampleFile);

        assertThatCode(() -> getDeleteItemPort().deleteItem(ALPHA))
                .describedAs("Should delete Item w/o failures")
                .doesNotThrowAnyException();

        Assertions.assertThat(getDirectoryPath(ALPHA))
                .describedAs("File should be removed")
                .doesNotExist();
    }

    @Test
    @DisplayName("Should fail item deletion when Item not exists")
    void test_08() {
        assertThatCode(() -> getDeleteItemPort().deleteItem(ALPHA))
                .isInstanceOf(AbstractServiceException.class);
    }

    @Test
    @DisplayName("Should fail item deletion when Item parent not exists")
    void test_09() {
        final File sampleFile = File.builder()
                .id(ItemId.newInstanceUUID())
                .name("sample.txt")
                .parent(ALPHA)
                .build();

        assertThatCode(() -> getDeleteItemPort().deleteItem(sampleFile))
                .isInstanceOf(AbstractServiceException.class);
    }
}