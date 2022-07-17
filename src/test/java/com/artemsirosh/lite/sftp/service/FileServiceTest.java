package com.artemsirosh.lite.sftp.service;

import com.artemsirosh.lite.sftp.domain.Directory;
import com.artemsirosh.lite.sftp.domain.Item;
import com.artemsirosh.lite.sftp.domain.ItemId;
import com.artemsirosh.lite.sftp.errors.AbstractServiceException;
import com.artemsirosh.lite.sftp.port.outbound.CreateFilePort;
import com.artemsirosh.lite.sftp.port.outbound.GetFileContentPort;
import com.artemsirosh.lite.sftp.port.outbound.GetItemByIdPort;
import com.artemsirosh.lite.sftp.port.outbound.RegisterItemPort;
import com.artemsirosh.lite.sftp.domain.File;
import com.artemsirosh.lite.sftp.domain.FileContent;
import com.artemsirosh.lite.sftp.port.inbound.CreateFileCommand;
import com.artemsirosh.lite.sftp.port.inbound.GetFileContentQuery;
import com.artemsirosh.lite.sftp.port.inbound.UpdateFileCommand;
import com.artemsirosh.lite.sftp.port.outbound.UpdateFilePort;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestFactory;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.time.Instant;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.DynamicTest.dynamicTest;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class FileServiceTest {

    private final static ItemId PARENT_ID = ItemId.newInstanceUUID();
    private final static Item PARENT = Directory.builder()
            .id(PARENT_ID)
            .name("alpha")
            .build();

    private final static byte[] CAFE_BABE = new byte[]{0xC, 0xA, 0xF, 0xE, 0xB, 0xA, 0xB, 0xE};
    private final static File CHARLIE_THE_FILE = File.builder()
            .id(ItemId.newInstanceUUID())
            .parent(PARENT)
            .name("charlie")
            .modifiedDate(Instant.parse("2007-09-01T10:00:00Z"))
            .build();

    private final static CreateFileCommand CREATE_CHARLIE_COMMAND = CreateFileCommand.builder()
            .name(CHARLIE_THE_FILE.getName())
            .content(CAFE_BABE)
            .lastModified(CHARLIE_THE_FILE.getModifiedDate())
            .parentId(PARENT_ID)
            .build();

    @Mock
    private CreateFilePort createFilePort;

    @Mock
    private GetItemByIdPort getItemByIdPort;

    @Mock
    private RegisterItemPort registerItemPort;

    @Mock
    private GetFileContentPort getFileContentPort;

    @Mock
    private UpdateFilePort updateFilePort;

    @InjectMocks
    private FileService fileService;

    @TestFactory
    @Tag("CreateFileUseCase")
    @DisplayName("Should create a new file")
    Stream<DynamicTest> test_00() {
        final Instant lastModified = Instant.parse("2007-09-01T10:00:00Z");
        final CreateFileCommand createFileCommand = CreateFileCommand.builder()
                .name("bravo")
                .lastModified(lastModified)
                .parentId(PARENT_ID)
                .content(new byte[0])
                .build();

        given(getItemByIdPort.getItemById(eq(PARENT_ID))).willReturn(PARENT);

        final File actual = fileService.createFile(createFileCommand);

        return Stream.of(
                dynamicTest(
                        "Must have id",
                        () -> assertThat(actual.getId()).isNotNull()
                ),
                dynamicTest(
                        "Must have expected filename",
                        () -> assertThat(actual.getName()).isEqualTo(createFileCommand.getName())
                ),
                dynamicTest(
                        "Must have expected last modified timestamp",
                        () -> assertThat(actual.getModifiedDate()).isEqualTo(lastModified)
                ),
                dynamicTest(
                        "Must have expected parent",
                        () -> assertThat(actual.getParent()).isEqualTo(PARENT)
                ),
                dynamicTest(
                        "Must be file",
                        () -> assertThat(actual.isDirectory()).isFalse()
                )
        );
    }

    @Test
    @Tag("CreateFileUseCase")
    @DisplayName("Should register new file as item")
    void test_01() {
        final CreateFileCommand createFileCommand = CreateFileCommand.builder()
                .name("bravo")
                .lastModified(Instant.now())
                .parentId(PARENT_ID)
                .content(new byte[0])
                .build();

        given(getItemByIdPort.getItemById(eq(PARENT_ID))).willReturn(PARENT);
        final File actual = fileService.createFile(createFileCommand);

        assertThatCode(() -> fileService.createFile(createFileCommand))
                .describedAs("Should create file without failures")
                .doesNotThrowAnyException();

        verify(registerItemPort).register(eq(actual));
    }

    @Test
    @Tag("CreateFileUseCase")
    @DisplayName("Should pass to file create port file info and content")
    void test_02() {
        given(getItemByIdPort.getItemById(eq(PARENT_ID))).willReturn(PARENT);

        final File actual = fileService.createFile(
                CreateFileCommand.builder()
                        .name("charlie")
                        .lastModified(Instant.now())
                        .parentId(PARENT_ID)
                        .content(CAFE_BABE)
                        .build()
        );

        verify(createFilePort).createFile(eq(actual), eq(new FileContent(CAFE_BABE)));
    }

    @Test
    @Tag("CreateFileUseCase")
    @DisplayName("Shouldn't register item when error thrown")
    void test_03() {
        given(getItemByIdPort.getItemById(eq(PARENT_ID))).willReturn(PARENT);
        doThrow(new UncheckedIOException(new IOException())).when(createFilePort).createFile(any(), any());

        assertThatCode(() -> fileService.createFile(
                CreateFileCommand.builder()
                        .name("charlie")
                        .lastModified(Instant.now())
                        .parentId(PARENT_ID)
                        .content(new byte[0])
                        .build()
        ))
                .describedAs("Should throw exception")
                .isNotNull();

        verify(registerItemPort, never()).register(any());
    }

    @Test
    @Tag("CreateFileUseCase")
    @DisplayName("Should re-throw error when get item port rules violated due file creation")
    void test_20() {
        doThrow(new TestException()).when(getItemByIdPort).getItemById(PARENT_ID);

        final CreateFileCommand createCharlieCommand = CreateFileCommand.builder()
                .name(CHARLIE_THE_FILE.getName())
                .content(CAFE_BABE)
                .lastModified(CHARLIE_THE_FILE.getModifiedDate())
                .parentId(PARENT_ID)
                .build();

        assertThatCode(() -> fileService.createFile(createCharlieCommand))
                .isInstanceOf(AbstractServiceException.class);
    }

    @Test
    @Tag("CreateFileUseCase")
    @DisplayName("Should re-throw error when get item port failures due file creation")
    void test_21() {
        doThrow(new RuntimeException()).when(getItemByIdPort).getItemById(PARENT_ID);

        assertThatCode(() -> fileService.createFile(CREATE_CHARLIE_COMMAND))
                .isNotNull();
    }

    @Test
    @Tag("CreateFileUseCase")
    @DisplayName("Should re-throw error when create file port rules violated due file creation")
    void test_22() {
        given(getItemByIdPort.getItemById(PARENT_ID)).willReturn(PARENT);
        doThrow(new TestException()).when(createFilePort).createFile(any(), any());

        assertThatCode(() -> fileService.createFile(CREATE_CHARLIE_COMMAND))
                .isInstanceOf(AbstractServiceException.class);
    }

    @Test
    @Tag("CreateFileUseCase")
    @DisplayName("Should re-throw error when create file port failures due file creation")
    void test_23() {
        given(getItemByIdPort.getItemById(PARENT_ID)).willReturn(PARENT);
        doThrow(new RuntimeException()).when(createFilePort).createFile(any(), any());

        assertThatCode(() -> fileService.createFile(CREATE_CHARLIE_COMMAND))
                .isNotNull();
    }

    @Test
    @Tag("CreateFileUseCase")
    @DisplayName("Should re-throw error when register port rules violated due file creation")
    void test_24() {
        given(getItemByIdPort.getItemById(PARENT_ID)).willReturn(PARENT);
        doThrow(new TestException()).when(registerItemPort).register(any());

        assertThatCode(() -> fileService.createFile(CREATE_CHARLIE_COMMAND))
                .isInstanceOf(AbstractServiceException.class);
    }

    @Test
    @Tag("CreateFileUseCase")
    @DisplayName("Should re-throw error when register port failures due file creation")
    void test_25() {
        given(getItemByIdPort.getItemById(PARENT_ID)).willReturn(PARENT);
        doThrow(new RuntimeException()).when(registerItemPort).register(any());

        assertThatCode(() -> fileService.createFile(CREATE_CHARLIE_COMMAND))
                .isNotNull();
    }

    @Test
    @Tag("GetFileContentUseCase")
    @DisplayName("Should return file content")
    void test_04() {
        final ItemId fileId = ItemId.newInstanceUUID();
        final var expectedContent = new FileContent(CAFE_BABE);

        given(getItemByIdPort.getItemById(fileId)).willReturn(CHARLIE_THE_FILE);
        given(getFileContentPort.getFileContent(CHARLIE_THE_FILE)).willReturn(expectedContent);

        final FileContent actualContent = fileService.getFileContent(new GetFileContentQuery(fileId));

        assertThat(actualContent)
                .isEqualTo(expectedContent);
    }

    @Test
    @Tag("GetFileContentUseCase")
    @DisplayName("Should throw exception if file not found")
    void test_05() {
        final ItemId fileId = ItemId.newInstanceUUID();
        given(getItemByIdPort.getItemById(eq(fileId))).willThrow(new IllegalArgumentException());

        assertThatCode(() -> fileService.getFileContent(new GetFileContentQuery(fileId)))
                .isNotNull();
    }

    @Test
    @Tag("GetFileContentUseCase")
    @DisplayName("Should throw exception if requested item is directory")
    void test_06() {
        final Directory item = Directory.builder()
                .id(ItemId.newInstanceUUID())
                .name("alpha")
                .build();

        given(getItemByIdPort.getItemById(eq(item.getId()))).willReturn(item);

        assertThatCode(() -> fileService.getFileContent(new GetFileContentQuery(item.getId())))
                .isNotNull();
    }

    @Test
    @Tag("GetFileContentUseCase")
    @DisplayName("Should throw exception if port cannot retrieve content")
    void test_07() {
        given(getItemByIdPort.getItemById(eq(CHARLIE_THE_FILE.getId()))).willReturn(CHARLIE_THE_FILE);
        given(getFileContentPort.getFileContent(eq(CHARLIE_THE_FILE))).willThrow(new IllegalArgumentException());

        assertThatCode(() -> fileService.getFileContent(new GetFileContentQuery(CHARLIE_THE_FILE.getId())))
                .isNotNull();
    }

    @Test
    @Tag("GetFileContentUseCase")
    @DisplayName("Should re-throw error when get item port rules violated due content retrieving")
    void test_26() {
        given(getItemByIdPort.getItemById(CHARLIE_THE_FILE.getId())).willThrow(new TestException());

        assertThatCode(() -> fileService.getFileContent(new GetFileContentQuery(CHARLIE_THE_FILE.getId())))
                .isInstanceOf(AbstractServiceException.class);
    }

    @Test
    @Tag("GetFileContentUseCase")
    @DisplayName("Should re-throw error when get item port failures due content retrieving")
    void test_27() {
        given(getItemByIdPort.getItemById(CHARLIE_THE_FILE.getId())).willThrow(new RuntimeException());

        assertThatCode(() -> fileService.getFileContent(new GetFileContentQuery(CHARLIE_THE_FILE.getId())))
                .isNotNull();
    }

    @Test
    @Tag("GetFileContentUseCase")
    @DisplayName("Should re-throw error when get content port rules violated due content retrieving")
    void test_28() {
        given(getItemByIdPort.getItemById(CHARLIE_THE_FILE.getId())).willReturn(CHARLIE_THE_FILE);
        doThrow(new TestException()).when(getFileContentPort).getFileContent(any());

        assertThatCode(() -> fileService.getFileContent(new GetFileContentQuery(CHARLIE_THE_FILE.getId())))
                .isInstanceOf(AbstractServiceException.class);
    }

    @Test
    @Tag("GetFileContentUseCase")
    @DisplayName("Should re-throw error when get content port failures due content retrieving")
    void test_29() {
        given(getItemByIdPort.getItemById(CHARLIE_THE_FILE.getId())).willReturn(CHARLIE_THE_FILE);
        doThrow(new RuntimeException()).when(getFileContentPort).getFileContent(any());

        assertThatCode(() -> fileService.getFileContent(new GetFileContentQuery(CHARLIE_THE_FILE.getId())))
                .isNotNull();
    }

    @Test
    @Tag("UpdateFileUseCase")
    @DisplayName("Should return updated file when update file")
    void test_08() {
        final File updatedFile = File.builder()
                .id(CHARLIE_THE_FILE.getId())
                .parent(CHARLIE_THE_FILE.getParent())
                .name("delta")
                .modifiedDate(CHARLIE_THE_FILE.getModifiedDate().plusMillis(100L))
                .build();

        given(getItemByIdPort.getItemById(eq(CHARLIE_THE_FILE.getId()))).willReturn(CHARLIE_THE_FILE);

        final File actualFile = fileService.updateFile(
                UpdateFileCommand.builder()
                        .fileId(CHARLIE_THE_FILE.getId())
                        .name(updatedFile.getName())
                        .lastModified(updatedFile.getModifiedDate())
                        .content(CAFE_BABE)
                        .build()
        );

        assertThat(actualFile)
                .isEqualTo(updatedFile);
    }

    @Test
    @Tag("UpdateFileUseCase")
    @DisplayName("Should update file with new data")
    void test_09() {
        final FileContent expectedContent = new FileContent(CAFE_BABE);
        final File updatedFile = File.builder()
                .id(CHARLIE_THE_FILE.getId())
                .parent(CHARLIE_THE_FILE.getParent())
                .name("delta")
                .modifiedDate(CHARLIE_THE_FILE.getModifiedDate().plusMillis(100L))
                .build();

        given(getItemByIdPort.getItemById(eq(CHARLIE_THE_FILE.getId()))).willReturn(CHARLIE_THE_FILE);

        assertThatCode(() -> fileService.updateFile(
                UpdateFileCommand.builder()
                        .fileId(CHARLIE_THE_FILE.getId())
                        .name(updatedFile.getName())
                        .lastModified(updatedFile.getModifiedDate())
                        .content(expectedContent.getContent())
                        .build()
        ))
                .describedAs("Should update file without errors")
                .doesNotThrowAnyException();

        verify(updateFilePort).updateFile(updatedFile);
    }

    @Test
    @Tag("UpdateFileUseCase")
    @DisplayName("Should register new file instance in registry")
    void test_10() {
        final FileContent expectedContent = new FileContent(CAFE_BABE);
        final File updatedFile = File.builder()
                .id(CHARLIE_THE_FILE.getId())
                .parent(CHARLIE_THE_FILE.getParent())
                .name("delta")
                .modifiedDate(CHARLIE_THE_FILE.getModifiedDate().plusMillis(100L))
                .build();

        given(getItemByIdPort.getItemById(eq(CHARLIE_THE_FILE.getId()))).willReturn(CHARLIE_THE_FILE);

        assertThatCode(() -> fileService.updateFile(
                UpdateFileCommand.builder()
                        .fileId(CHARLIE_THE_FILE.getId())
                        .name(updatedFile.getName())
                        .lastModified(updatedFile.getModifiedDate())
                        .content(expectedContent.getContent())
                        .build()
        ))
                .describedAs("Should update file without errors")
                .doesNotThrowAnyException();

        verify(registerItemPort).register(updatedFile);
    }

    @Test
    @Tag("UpdateFileUseCase")
    @DisplayName("Should fail update when file not found")
    void test_11() {
        given(getItemByIdPort.getItemById(eq(CHARLIE_THE_FILE.getId()))).willReturn(CHARLIE_THE_FILE);

        assertThatCode(() -> fileService.updateFile(
                UpdateFileCommand.builder()
                        .fileId(ItemId.newInstanceUUID())
                        .name("echo")
                        .lastModified(Instant.parse("2017-07-12T22:00:00Z"))
                        .content(new byte[]{1, 3})
                        .build()
        ))
                .isNotNull();
        verify(registerItemPort, never()).register(any());
    }

    @Test
    @Tag("UpdateFileUseCase")
    @DisplayName("Should fail update when updating item is directory")
    void test_12() {
        final Directory item = Directory.builder()
                .id(ItemId.newInstanceUUID())
                .name("alpha")
                .parent(PARENT)
                .build();

        given(getItemByIdPort.getItemById(eq(item.getId()))).willReturn(item);

        assertThatCode(() -> fileService.updateFile(
                UpdateFileCommand.builder()
                        .fileId(item.getId())
                        .name("echo")
                        .lastModified(Instant.parse("2017-07-12T22:00:00Z"))
                        .content(new byte[]{1, 3})
                        .build()
        ))
                .isNotNull();

        verify(registerItemPort, never()).register(any());
    }

    @Test
    @Tag("UpdateFileUseCase")
    @DisplayName("Should fail update when port failure occurs")
    void test_13() {
        final FileContent expectedContent = new FileContent(CAFE_BABE);
        final File updatedFile = File.builder()
                .id(CHARLIE_THE_FILE.getId())
                .parent(CHARLIE_THE_FILE.getParent())
                .name("delta")
                .modifiedDate(CHARLIE_THE_FILE.getModifiedDate().plusMillis(100L))
                .build();

        given(getItemByIdPort.getItemById(eq(CHARLIE_THE_FILE.getId()))).willReturn(CHARLIE_THE_FILE);
        doThrow(new IllegalArgumentException()).when(updateFilePort).updateFile(eq(updatedFile));

        assertThatCode(() -> fileService.updateFile(
                UpdateFileCommand.builder()
                        .fileId(CHARLIE_THE_FILE.getId())
                        .name(updatedFile.getName())
                        .lastModified(updatedFile.getModifiedDate())
                        .content(expectedContent.getContent())
                        .build()
        ))
                .isNotNull();

        verify(registerItemPort, never()).register(any());
    }

    @Test
    @Tag("UpdateFileUseCase")
    @DisplayName("Should update file name")
    void test_14() {
        final var expectedName = "foxtrot";
        given(getItemByIdPort.getItemById(eq(CHARLIE_THE_FILE.getId()))).willReturn(CHARLIE_THE_FILE);
        final var actualFile = fileService.updateFile(
                UpdateFileCommand.builder()
                        .fileId(CHARLIE_THE_FILE.getId())
                        .name(expectedName)
                        .build()
        );

        assertThat(actualFile.getName())
                .isEqualTo(expectedName);

        verify(updateFilePort).updateFile(argThat(file -> expectedName.equals(file.getName())));
    }

    @Test
    @Tag("UpdateFileUseCase")
    @DisplayName("Should remain old name when new not provided")
    void test_16() {
        given(getItemByIdPort.getItemById(eq(CHARLIE_THE_FILE.getId()))).willReturn(CHARLIE_THE_FILE);

        final var actualFile = fileService.updateFile(
                UpdateFileCommand.builder()
                        .fileId(CHARLIE_THE_FILE.getId())
                        .lastModified(Instant.now())
                        .build()
        );

        assertThat(actualFile.getName())
                .isEqualTo(CHARLIE_THE_FILE.getName());

        verify(updateFilePort).updateFile(argThat(file -> CHARLIE_THE_FILE.getName().equals(file.getName())));
    }

    @Test
    @Tag("UpdateFileUseCase")
    @DisplayName("Should update last modified date-time")
    void test_15() {
        final var expectedLastModified = Instant.parse("2017-07-12T22:00:00Z");
        given(getItemByIdPort.getItemById(eq(CHARLIE_THE_FILE.getId()))).willReturn(CHARLIE_THE_FILE);

        final var actualFile = fileService.updateFile(
                UpdateFileCommand.builder()
                        .fileId(CHARLIE_THE_FILE.getId())
                        .lastModified(expectedLastModified)
                        .build()
        );

        assertThat(actualFile.getModifiedDate())
                .isEqualTo(expectedLastModified);

        verify(updateFilePort).updateFile(argThat(file -> expectedLastModified.equals(file.getModifiedDate())));
    }

    @Test
    @Tag("UpdateFileUseCase")
    @DisplayName("Should remain old last modified date-time when new wasn't provided")
    void test_17() {
        final var expectedLastModified = CHARLIE_THE_FILE.getModifiedDate();
        given(getItemByIdPort.getItemById(eq(CHARLIE_THE_FILE.getId()))).willReturn(CHARLIE_THE_FILE);

        final var actualFile = fileService.updateFile(
                UpdateFileCommand.builder()
                        .fileId(CHARLIE_THE_FILE.getId())
                        .name("foxtrot")
                        .build()
        );

        assertThat(actualFile.getModifiedDate())
                .isEqualTo(expectedLastModified);

        verify(updateFilePort).updateFile(argThat(file -> expectedLastModified.equals(file.getModifiedDate())));
    }

    @Test
    @Tag("UpdateFileUseCase")
    @DisplayName("Should update file content")
    void test_18() {
        final var expectedContent = new FileContent(CAFE_BABE);
        given(getItemByIdPort.getItemById(eq(CHARLIE_THE_FILE.getId()))).willReturn(CHARLIE_THE_FILE);
        assertThatCode(() -> fileService.updateFile(
                UpdateFileCommand.builder()
                        .fileId(CHARLIE_THE_FILE.getId())
                        .content(CAFE_BABE)
                        .build())
        )
                .describedAs("Should update file without errors")
                .doesNotThrowAnyException();

        verify(updateFilePort).updateFileContent(eq(expectedContent));
    }

    @Test
    @Tag("UpdateFileUseCase")
    @DisplayName("Should remain file content when new one not provided")
    void test_19() {
        given(getItemByIdPort.getItemById(eq(CHARLIE_THE_FILE.getId()))).willReturn(CHARLIE_THE_FILE);
        assertThatCode(() -> fileService.updateFile(
                UpdateFileCommand.builder()
                        .fileId(CHARLIE_THE_FILE.getId())
                        .name("foxtrot")
                        .build())
        )
                .describedAs("Should update file without errors")
                .doesNotThrowAnyException();

        verify(updateFilePort, never()).updateFileContent(any());
    }

    @Test
    @Tag("UpdateFileUseCase")
    @DisplayName("Should re-throw error when get item port rules violated due file updating")
    void test_30() {
        given(getItemByIdPort.getItemById(CHARLIE_THE_FILE.getId())).willThrow(new TestException());
        assertThatCode(
                () -> fileService.updateFile(UpdateFileCommand.builder()
                        .fileId(CHARLIE_THE_FILE.getId())
                        .build())
        )
                .isInstanceOf(AbstractServiceException.class);
    }

    @Test
    @Tag("UpdateFileUseCase")
    @DisplayName("Should re-throw error when get item port failures due file updating")
    void test_31() {
        given(getItemByIdPort.getItemById(CHARLIE_THE_FILE.getId())).willThrow(new RuntimeException());
        assertThatCode(
                () -> fileService.updateFile(UpdateFileCommand.builder()
                        .fileId(CHARLIE_THE_FILE.getId())
                        .build())
        )
                .isNotNull();
    }

    @Test
    @Tag("UpdateFileUseCase")
    @DisplayName("Should re-throw error when update file port rules violated due file updating")
    void test_32() {
        given(getItemByIdPort.getItemById(eq(CHARLIE_THE_FILE.getId()))).willReturn(CHARLIE_THE_FILE);
        doThrow(new TestException()).when(updateFilePort).updateFile(CHARLIE_THE_FILE);
        assertThatCode(
                () -> fileService.updateFile(UpdateFileCommand.builder()
                        .fileId(CHARLIE_THE_FILE.getId())
                        .build())
        )
                .isInstanceOf(AbstractServiceException.class);
    }

    @Test
    @Tag("UpdateFileUseCase")
    @DisplayName("Should re-throw error when update file port failures due file updating")
    void test_33() {
        given(getItemByIdPort.getItemById(eq(CHARLIE_THE_FILE.getId()))).willReturn(CHARLIE_THE_FILE);
        doThrow(new RuntimeException()).when(updateFilePort).updateFile(CHARLIE_THE_FILE);
        assertThatCode(
                () -> fileService.updateFile(UpdateFileCommand.builder()
                        .fileId(CHARLIE_THE_FILE.getId())
                        .build())
        )
                .isNotNull();
    }

    @Test
    @Tag("UpdateFileUseCase")
    @DisplayName("Should re-throw error when update file port rules violated due content updating")
    void test_34() {
        given(getItemByIdPort.getItemById(eq(CHARLIE_THE_FILE.getId()))).willReturn(CHARLIE_THE_FILE);
        doThrow(new TestException()).when(updateFilePort).updateFileContent(new FileContent(CAFE_BABE));

        assertThatCode(
                () -> fileService.updateFile(
                        UpdateFileCommand.builder()
                                .fileId(CHARLIE_THE_FILE.getId())
                                .content(CAFE_BABE)
                                .build()
                )
        )
                .isInstanceOf(AbstractServiceException.class);
    }

    @Test
    @Tag("UpdateFileUseCase")
    @DisplayName("Should re-throw error when update file port failures due content updating")
    void test_35() {
        given(getItemByIdPort.getItemById(eq(CHARLIE_THE_FILE.getId()))).willReturn(CHARLIE_THE_FILE);
        doThrow(new RuntimeException()).when(updateFilePort).updateFileContent(new FileContent(CAFE_BABE));
        assertThatCode(
                () -> fileService.updateFile(
                        UpdateFileCommand.builder()
                                .fileId(CHARLIE_THE_FILE.getId())
                                .content(CAFE_BABE)
                                .build()
                )
        )
                .isNotNull();
    }

    @Test
    @Tag("UpdateFileUseCase")
    @DisplayName("Should re-throw error when register port rules violated due file updating")
    void test_36() {
        given(getItemByIdPort.getItemById(eq(CHARLIE_THE_FILE.getId()))).willReturn(CHARLIE_THE_FILE);
        doThrow(new TestException()).when(registerItemPort).register(CHARLIE_THE_FILE);

        assertThatCode(
                () -> fileService.updateFile(
                        UpdateFileCommand.builder()
                                .fileId(CHARLIE_THE_FILE.getId())
                                .build()
                )
        )
                .isInstanceOf(AbstractServiceException.class);
    }

    @Test
    @Tag("UpdateFileUseCase")
    @DisplayName("Should re-throw error when register port failures due file updating")
    void test_37() {
        given(getItemByIdPort.getItemById(eq(CHARLIE_THE_FILE.getId()))).willReturn(CHARLIE_THE_FILE);
        doThrow(new RuntimeException()).when(registerItemPort).register(CHARLIE_THE_FILE);
        assertThatCode(
                () -> fileService.updateFile(
                        UpdateFileCommand.builder()
                                .fileId(CHARLIE_THE_FILE.getId())
                                .build()
                )
        )
                .isNotNull();
    }
}