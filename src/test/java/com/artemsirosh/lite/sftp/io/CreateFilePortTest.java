package com.artemsirosh.lite.sftp.io;

import com.artemsirosh.lite.sftp.domain.Directory;
import com.artemsirosh.lite.sftp.domain.File;
import com.artemsirosh.lite.sftp.domain.FileContent;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;

class CreateFilePortTest extends LocalFileSystemItemPortTestSuite {

    private static final Directory ALPHA = Directory.builder()
            .name("alpha")
            .build();

    private static final File CHARLIE_THE_FILE = File.builder()
            .name("charlie")
            .parent(ALPHA)
            .build();

    private static final FileContent CONTENT = new FileContent(new byte[]{4, 8, 15, 16, 23, 42});

    @TempDir
    Path rootDir;

    @BeforeEach
    void setUp() {
        initialize(rootDir);
    }

    @Test
    @DisplayName("Should create File w/o failures when a new file provided")
    void test_00() throws IOException {
        createDirectory(ALPHA);
        assertThatCode(() -> getCreateFilePort().createFile(CHARLIE_THE_FILE, CONTENT))
                .doesNotThrowAnyException();
    }

    @Test
    @DisplayName("Should create a new local file when a File created")
    void test_01() throws IOException {
        final Path parentPath = createDirectory(ALPHA);
        assertThatCode(() -> getCreateFilePort().createFile(CHARLIE_THE_FILE, CONTENT))
                .describedAs("Should create a file w/o failures")
                .doesNotThrowAnyException();

        assertThat(parentPath.resolve(CHARLIE_THE_FILE.getName()))
                .describedAs("A new file should exists")
                .exists();
    }

    @Test
    @DisplayName("Should create a new local file with expected content when a File created")
    void test_02() throws IOException {
        final Path parentPath = createDirectory(ALPHA);
        assertThatCode(() -> getCreateFilePort().createFile(CHARLIE_THE_FILE, CONTENT))
                .describedAs("Should create a file w/o failures")
                .doesNotThrowAnyException();

        assertThat(parentPath.resolve(CHARLIE_THE_FILE.getName()))
                .describedAs("File should has expected content")
                .hasBinaryContent(CONTENT.getContent());
    }

    @Test
    @DisplayName("Should create a file with a deep hierarchy of parents")
    void test_04() throws IOException {
        Directory parent = ALPHA;
        for (final String name: List.of("bravo", "charlie", "delta")) {
           parent = Directory.builder()
                   .name(name)
                   .parent(parent)
                   .build();
        }

        final Path parentDirectoryPath = createDirectory(parent);
        final File actualFile = File.builder()
                .name("echo")
                .parent(parent)
                .build();

        assertThatCode(() -> getCreateFilePort().createFile(actualFile, CONTENT))
                .describedAs("Should create a file w/o failures")
                .doesNotThrowAnyException();

        assertThat(parentDirectoryPath.resolve(actualFile.getName()))
                .describedAs("A new file should exists")
                .exists();
    }

}
