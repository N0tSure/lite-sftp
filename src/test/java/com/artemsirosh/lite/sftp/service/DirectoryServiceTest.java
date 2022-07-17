package com.artemsirosh.lite.sftp.service;

import com.artemsirosh.lite.sftp.domain.Directory;
import com.artemsirosh.lite.sftp.domain.DirectoryListing;
import com.artemsirosh.lite.sftp.domain.ItemId;
import com.artemsirosh.lite.sftp.errors.AbstractServiceException;
import com.artemsirosh.lite.sftp.domain.File;
import com.artemsirosh.lite.sftp.port.inbound.CreateDirectoryCommand;
import com.artemsirosh.lite.sftp.port.inbound.DeleteItemCommand;
import com.artemsirosh.lite.sftp.port.inbound.GetDirectoryChildrenQuery;
import com.artemsirosh.lite.sftp.port.outbound.CreateDirectoryPort;
import com.artemsirosh.lite.sftp.port.outbound.DeleteItemPort;
import com.artemsirosh.lite.sftp.port.outbound.GetDirectoryListingPort;
import com.artemsirosh.lite.sftp.port.outbound.GetItemByIdPort;
import com.artemsirosh.lite.sftp.port.outbound.RegisterItemPort;
import com.artemsirosh.lite.sftp.port.outbound.UnregisterItemPort;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.time.Instant;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.notNull;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class DirectoryServiceTest {

    private static final ItemId PARENT_ID = ItemId.newInstanceUUID();
    private static final Directory ALPHA_PARENT = Directory.builder()
            .id(PARENT_ID)
            .name("alpha")
            .build();

    private static final CreateDirectoryCommand BRAVO_COMMAND = new CreateDirectoryCommand("bravo", PARENT_ID);

    @Mock
    private RegisterItemPort registerItemPort;

    @Mock
    private CreateDirectoryPort createDirectoryPort;

    @Mock
    private DeleteItemPort deleteItemPort;

    @Mock
    private GetDirectoryListingPort getDirectoryListingPort;

    @Mock
    private GetItemByIdPort getItemByIdPort;

    @Mock
    private UnregisterItemPort unregisterItemPort;

    @InjectMocks
    private DirectoryService service;

    @Captor
    private ArgumentCaptor<Directory> directoryCaptor;

    @Test
    @Tag("CreateDirectoryUseCase")
    @DisplayName("Should create a new directory")
    void test_00() {

        final ItemId parentItemId = ItemId.fromString("foo");
        final var expectedDirName = "bravo";

        final var parentDirectory = Directory.builder()
                .id(parentItemId)
                .name("alpha")
                .build();

        when(getItemByIdPort.getItemById(parentItemId)).thenReturn(parentDirectory);

        assertThatCode(() -> service.createDirectory(
                        new CreateDirectoryCommand(expectedDirName, parentItemId)
                ))
                .doesNotThrowAnyException();

        verify(createDirectoryPort).createDirectory(directoryCaptor.capture());

        final Directory capturedDirectory = directoryCaptor.getValue();

        assertThat(capturedDirectory)
                .describedAs("A new directory should be null")
                .isNotNull();

        assertThat(capturedDirectory.getName())
                .describedAs("New directory name")
                .isEqualTo(expectedDirName);

        assertThat(capturedDirectory.getParent())
                .describedAs("New directory parent")
                .isEqualTo(parentDirectory);

    }

    @Test
    @Tag("CreateDirectoryUseCase")
    @DisplayName("Should register new Directory")
    void test_01() {
        final ItemId parentItemId = ItemId.fromString("foo");
        final var expectedDirName = "bravo";

        final Directory parentDirectory = Directory.builder()
                .id(parentItemId)
                .name("alpha")
                .build();

        when(getItemByIdPort.getItemById(parentItemId)).thenReturn(parentDirectory);
        assertThatCode(() -> service.createDirectory(new CreateDirectoryCommand(expectedDirName, parentItemId)))
                .doesNotThrowAnyException();

        verify(registerItemPort).register(notNull());
    }

    @Test
    @Tag("CreateDirectoryUseCase")
    @DisplayName("Should not create and not register Directory when no such parent item")
    void test_02() {
        when(getItemByIdPort.getItemById(any())).thenThrow(new IllegalArgumentException("Not found"));

        assertThatCode(() -> service.createDirectory(BRAVO_COMMAND))
                .isNotNull();

        verify(createDirectoryPort, never()).createDirectory(any());
        verify(registerItemPort, never()).register(any());
    }

    @Test
    @Tag("CreateDirectoryUseCase")
    @DisplayName("Should not register Directory Id when error due creation")
    void test_03() {
        final ItemId parentItemId = ItemId.fromString("foo");
        final var expectedDirName = "bravo";

        final Directory parentDirectory = Directory.builder()
                .id(parentItemId)
                .name("alpha")
                .build();

        when(getItemByIdPort.getItemById(parentItemId)).thenReturn(parentDirectory);
        doThrow(new UncheckedIOException(new IOException())).when(createDirectoryPort).createDirectory(any());

        assertThatCode(() -> service.createDirectory(new CreateDirectoryCommand(expectedDirName, parentItemId)))
                .isNotNull();

        verify(registerItemPort, never()).register(any());
    }

    @Test
    @Tag("CreateDirectoryUseCase")
    @DisplayName("Should re-throw exception when get item port failure due directory creation")
    void test_10() {
        when(getItemByIdPort.getItemById(any())).thenThrow(new IllegalArgumentException("Not found"));

        assertThatCode(() -> service.createDirectory(BRAVO_COMMAND))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    @Tag("CreateDirectoryUseCase")
    @DisplayName("Should throw exception when outbound port rules violation occurs due directory creation")
    void test_11() {
        when(getItemByIdPort.getItemById(any())).thenThrow(new TestException());
        assertThatCode(() -> service.createDirectory(BRAVO_COMMAND))
                .isInstanceOf(AbstractServiceException.class);
    }

    @Test
    @Tag("CreateDirectoryUseCase")
    @DisplayName("Should throw exception when create port rule violation due directory creation")
    void test_12() {
        when(getItemByIdPort.getItemById(eq(PARENT_ID))).thenReturn(ALPHA_PARENT);
        doThrow(new TestException()).when(createDirectoryPort).createDirectory(any());

        assertThatCode(() -> service.createDirectory(BRAVO_COMMAND))
                .isInstanceOf(AbstractServiceException.class);
    }

    @Test
    @Tag("CreateDirectoryUseCase")
    @DisplayName("Should re-throw exception when create port failure due directory creation")
    void test_13() {
        when(getItemByIdPort.getItemById(eq(PARENT_ID))).thenReturn(ALPHA_PARENT);
        doThrow(new IllegalArgumentException("Foo")).when(createDirectoryPort).createDirectory(any());

        assertThatCode(() -> service.createDirectory(BRAVO_COMMAND))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    @Tag("CreateDirectoryUseCase")
    @DisplayName("Should throw exception when register port rule violation due directory creation")
    void test_14() {
        when(getItemByIdPort.getItemById(eq(PARENT_ID))).thenReturn(ALPHA_PARENT);
        doThrow(new TestException()).when(registerItemPort).register(any());

        assertThatCode(() -> service.createDirectory(BRAVO_COMMAND))
                .isInstanceOf(AbstractServiceException.class);
    }

    @Test
    @Tag("CreateDirectoryUseCase")
    @DisplayName("Should re-throw exception when register port failure due directory creation")
    void test_15() {
        when(getItemByIdPort.getItemById(eq(PARENT_ID))).thenReturn(ALPHA_PARENT);
        doThrow(new IllegalArgumentException("Foo")).when(registerItemPort).register(any());

        assertThatCode(() -> service.createDirectory(BRAVO_COMMAND))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    @Tag("DeleteItemUseCase")
    @DisplayName("Should delete and unregister Directory")
    void test_04() {
        final ItemId itemId = ItemId.fromString("foo");

        final Directory directory = Directory.builder()
                .id(itemId)
                .name("alpha")
                .build();

        when(getItemByIdPort.getItemById(itemId)).thenReturn(directory);

        assertThatCode(() -> service.deleteItem(new DeleteItemCommand(itemId)))
                .doesNotThrowAnyException();

        verify(deleteItemPort).deleteItem(directory);
        verify(unregisterItemPort).unregister(itemId);
    }

    @Test
    @Tag("DeleteItemUseCase")
    @DisplayName("Should not delete and unregister Directory when id wasn't found")
    void test_05() {
        when(getItemByIdPort.getItemById(any())).thenThrow(new IllegalArgumentException("Not found"));

        assertThatCode(() -> service.deleteItem(new DeleteItemCommand(ItemId.fromString("foo"))))
                .isNotNull();

        verify(deleteItemPort, never()).deleteItem(any());
        verify(unregisterItemPort, never()).unregister(any());
    }

    @Test
    @Tag("DeleteItemUseCase")
    @DisplayName("Should not unregister Directory when error occur due deleting")
    void test_06() {
        final ItemId itemId = ItemId.fromString("foo");

        final Directory directory = Directory.builder()
                .id(itemId)
                .name("alpha")
                .build();

        when(getItemByIdPort.getItemById(itemId)).thenReturn(directory);
        doThrow(new UncheckedIOException(new IOException())).when(deleteItemPort).deleteItem(directory);

        assertThatCode(() -> service.deleteItem(new DeleteItemCommand(itemId)))
                .isNotNull();

        verify(unregisterItemPort, never()).unregister(itemId);
    }

    @Test
    @Tag("DeleteItemUseCase")
    @DisplayName("Should throw exception when get item port rules violation due item deleting")
    void test_17() {
        when(getItemByIdPort.getItemById(any())).thenThrow(new TestException());
        assertThatCode(() -> service.deleteItem(new DeleteItemCommand(ItemId.newInstanceUUID())))
                .isInstanceOf(AbstractServiceException.class);
    }

    @Test
    @Tag("DeleteItemUseCase")
    @DisplayName("Should re-throw exception when get item port failure due item deleting")
    void test_18() {
        when(getItemByIdPort.getItemById(any())).thenThrow(new IllegalArgumentException());
        assertThatCode(() -> service.deleteItem(new DeleteItemCommand(ItemId.newInstanceUUID())))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    @Tag("DeleteItemUseCase")
    @DisplayName("Should throw exception when delete item port rules violation due item deleting")
    void test_19() {
        when(getItemByIdPort.getItemById(PARENT_ID)).thenReturn(ALPHA_PARENT);
        doThrow(new TestException()).when(deleteItemPort).deleteItem(ALPHA_PARENT);

        assertThatCode(() -> service.deleteItem(new DeleteItemCommand(PARENT_ID)))
                .isInstanceOf(AbstractServiceException.class);
    }

    @Test
    @Tag("DeleteItemUseCase")
    @DisplayName("Should re-throw exception when delete item port failure due item deleting")
    void test_20() {
        when(getItemByIdPort.getItemById(PARENT_ID)).thenReturn(ALPHA_PARENT);
        doThrow(new IllegalArgumentException()).when(deleteItemPort).deleteItem(ALPHA_PARENT);

        assertThatCode(() -> service.deleteItem(new DeleteItemCommand(PARENT_ID)))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    @Tag("DeleteItemUseCase")
    @DisplayName("Should throw exception when unregister item port rules violation due item deleting")
    void test_21() {
        when(getItemByIdPort.getItemById(PARENT_ID)).thenReturn(ALPHA_PARENT);
        doThrow(new TestException()).when(unregisterItemPort).unregister(PARENT_ID);

        assertThatCode(() -> service.deleteItem(new DeleteItemCommand(PARENT_ID)))
                .isInstanceOf(AbstractServiceException.class);
    }

    @Test
    @Tag("DeleteItemUseCase")
    @DisplayName("Should re-throw exception when unregister item port failure due item deleting")
    void test_22() {
        when(getItemByIdPort.getItemById(PARENT_ID)).thenReturn(ALPHA_PARENT);
        doThrow(new IllegalArgumentException()).when(unregisterItemPort).unregister(PARENT_ID);

        assertThatCode(() -> service.deleteItem(new DeleteItemCommand(PARENT_ID)))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    @Tag("GetDirectoryListingUseCase")
    @DisplayName("Should return directory listing")
    void test_07() {
        final ItemId directoryId = ItemId.fromString("foo");

        final Directory directory = Directory.builder()
                .id(directoryId)
                .name("alpha")
                .build();

        final DirectoryListing expectedListing = new DirectoryListing(directory, Set.of(
                File.builder()
                        .id(ItemId.fromString("bravo"))
                        .name("bravo")
                        .modifiedDate(Instant.EPOCH)
                        .parent(directory)
                        .build(),
                Directory.builder()
                        .id(ItemId.fromString("charlie"))
                        .name("charlie")
                        .parent(directory)
                        .build()
        ));

        when(getItemByIdPort.getItemById(directoryId)).thenReturn(directory);
        when(getDirectoryListingPort.getListing(directory)).thenReturn(expectedListing);

        final DirectoryListing actual = service.getDirectoryListing(new GetDirectoryChildrenQuery(directoryId));

        Assertions.assertThat(actual)
                .describedAs("Listing directory")
                .isEqualTo(expectedListing);
    }

    @Test
    @Tag("GetDirectoryListingUseCase")
    @DisplayName("Should throw exception when root Directory not found")
    void test_08() {
        final ItemId directoryId = ItemId.fromString("foo");
        when(getItemByIdPort.getItemById(directoryId)).thenThrow(new IllegalArgumentException("Not found"));

        assertThatCode(() -> service.getDirectoryListing(new GetDirectoryChildrenQuery(directoryId)))
                .isNotNull();
    }

    @Test
    @Tag("GetDirectoryListingUseCase")
    @DisplayName("Should throw exception in case of port error")
    void test_09() {
        final var directoryId = ItemId.fromString("foo");

        final var directory = Directory.builder()
                .id(directoryId)
                .name("alpha")
                .build();

        when(getItemByIdPort.getItemById(directoryId)).thenReturn(directory);
        when(getDirectoryListingPort.getListing(directory)).thenThrow(new UncheckedIOException(new IOException()));

        assertThatCode(() -> service.getDirectoryListing(new GetDirectoryChildrenQuery(directoryId)))
                .isNotNull();
    }

    @Test
    @Tag("GetDirectoryListingUseCase")
    @DisplayName("Should throw exception when requested item is not a directory")
    void test_27() {
        final ItemId itemId = ItemId.newInstanceUUID();
        final File charlieFile = File.builder()
                .id(itemId)
                .name("charlie")
                .parent(ALPHA_PARENT)
                .modifiedDate(Instant.now())
                .build();

        when(getItemByIdPort.getItemById(itemId)).thenReturn(charlieFile);
        assertThatCode(() -> service.getDirectoryListing(new GetDirectoryChildrenQuery(itemId)))
                .isInstanceOf(AbstractServiceException.class);
    }

    @Test
    @Tag("GetDirectoryListingUseCase")
    @DisplayName("Should throw exception when get item port rules violation due directory listing")
    void test_23() {
        when(getItemByIdPort.getItemById(PARENT_ID)).thenThrow(new TestException());

        assertThatCode(() -> service.getDirectoryListing(new GetDirectoryChildrenQuery(PARENT_ID)))
                .isInstanceOf(AbstractServiceException.class);
    }

    @Test
    @Tag("GetDirectoryListingUseCase")
    @DisplayName("Should re-throw exception when get item port failure due directory listing")
    void test_24() {
        when(getItemByIdPort.getItemById(PARENT_ID)).thenThrow(new IllegalArgumentException());

        assertThatCode(() -> service.getDirectoryListing(new GetDirectoryChildrenQuery(PARENT_ID)))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    @Tag("GetDirectoryListingUseCase")
    @DisplayName("Should throw exception when get listing port rules violation due directory listing")
    void test_25() {
        when(getItemByIdPort.getItemById(PARENT_ID)).thenReturn(ALPHA_PARENT);
        when(getDirectoryListingPort.getListing(ALPHA_PARENT)).thenThrow(new TestException());

        assertThatCode(() -> service.getDirectoryListing(new GetDirectoryChildrenQuery(PARENT_ID)))
                .isInstanceOf(AbstractServiceException.class);
    }

    @Test
    @Tag("GetDirectoryListingUseCase")
    @DisplayName("Should re-throw exception when get item port failure due directory listing")
    void test_26() {
        when(getItemByIdPort.getItemById(PARENT_ID)).thenReturn(ALPHA_PARENT);
        when(getDirectoryListingPort.getListing(ALPHA_PARENT)).thenThrow(new IllegalArgumentException());

        assertThatCode(() -> service.getDirectoryListing(new GetDirectoryChildrenQuery(PARENT_ID)))
                .isInstanceOf(IllegalArgumentException.class);
    }

}