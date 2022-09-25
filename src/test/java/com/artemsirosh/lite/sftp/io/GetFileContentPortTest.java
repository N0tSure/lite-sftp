package com.artemsirosh.lite.sftp.io;

import com.artemsirosh.lite.sftp.domain.Directory;
import com.artemsirosh.lite.sftp.domain.File;
import com.artemsirosh.lite.sftp.domain.FileContent;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;

class GetFileContentPortTest extends LocalFileSystemItemPortTestSuite {

    private static final Directory ALPHA_PARENT = Directory.builder()
            .name("alpha")
            .build();

    private static final File CHARLIE_THE_FILE = File.builder()
            .name("charlie.txt")
            .parent(ALPHA_PARENT)
            .build();

    private static final FileContent CONTENT = new FileContent(new byte[]{4, 8, 15, 16, 23, 42});

    @TempDir
    Path workingDir;

    @BeforeEach
    void setUp() throws IOException {
        initialize(workingDir);
        final Path directoryPath = createDirectory(ALPHA_PARENT);
        final Path filePath = directoryPath.resolve(CHARLIE_THE_FILE.getName());
        Files.write(filePath, CONTENT.getContent());
    }

    @Test
    @DisplayName("Should retrieve file content w/o failures")
    void test_00() {
        Assertions.assertThatCode(() -> getGetFileContentPort().getFileContent(CHARLIE_THE_FILE))
                .doesNotThrowAnyException();
    }

    @Test
    @DisplayName("Should retrieve file with correct content")
    void test_01() {
        final FileContent actual = getGetFileContentPort().getFileContent(CHARLIE_THE_FILE);
        assertThat(actual)
                .extracting("content")
                .isEqualTo(CONTENT.getContent());
    }

    @Test
    @DisplayName("Should retrieve file content w/o parent")
    void test_02() throws IOException {
        final File foxtrotFile = File.builder()
                .name("foxtrot")
                .build();

        Files.write(workingDir.resolve(foxtrotFile.getName()), CONTENT.getContent());
        final FileContent actual = getGetFileContentPort().getFileContent(CHARLIE_THE_FILE);
        assertThat(actual)
                .extracting("content")
                .isEqualTo(CONTENT.getContent());
    }

    @Test
    @DisplayName("Should retrieve file having deep hierarchy of parents")
    void test_03() throws IOException {
        Directory parent = ALPHA_PARENT;
        for (final String name : List.of("bravo", "charlie", "delta")) {
            parent = Directory.builder()
                    .name(name)
                    .parent(parent)
                    .build();
        }

        final var file = File.builder()
                .name("echo")
                .parent(parent)
                .build();

        createFile(file, new ByteArrayInputStream(CONTENT.getContent()));
        final FileContent actual = getGetFileContentPort().getFileContent(file);
        assertThat(actual)
                .extracting("content")
                .isEqualTo(CONTENT.getContent());
    }

    @Test
    @DisplayName("Should fail when there is no such file")
    void test_04() {
        assertThatCode(() -> getGetFileContentPort().getFileContent(File.builder().name("foo").build()))
                .isNotNull();
    }
}
