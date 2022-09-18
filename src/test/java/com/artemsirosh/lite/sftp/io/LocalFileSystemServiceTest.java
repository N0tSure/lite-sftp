package com.artemsirosh.lite.sftp.io;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

class LocalFileSystemServiceTest {

    @Test
    @DisplayName("Should initialize instance when proper root directory provided")
    void test_00(@TempDir final Path rootDir) {
        Assertions.assertThatCode(() -> new LocalFileSystemService(rootDir).init())
                .doesNotThrowAnyException();
    }

    @Test
    @DisplayName("Should fail instance initialisation when root directory is file")
    void test_01(@TempDir final Path rootDir) throws IOException {
        final Path badRootDir = Files.createFile(rootDir.resolve("sample.txt"));
        Assertions.assertThatCode(() -> new LocalFileSystemService(badRootDir).init())
                .isNotNull();
    }

    @Test
    @DisplayName("Should fail instance initialisation when root directory doesn't exist")
    void test_02() {
        final Path badRootDir = Path.of("sample.txt");
        Assertions.assertThatCode(() -> new LocalFileSystemService(badRootDir).init())
                .isNotNull();
    }
}