package com.artemsirosh.lite.sftp.persistence;

import java.time.Instant;
import java.util.HashMap;
import java.util.Optional;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.artemsirosh.lite.sftp.domain.Directory;
import com.artemsirosh.lite.sftp.domain.File;
import com.artemsirosh.lite.sftp.domain.Item;
import com.artemsirosh.lite.sftp.domain.ItemId;
import com.artemsirosh.lite.sftp.errors.AbstractServiceException;
import com.artemsirosh.lite.sftp.port.outbound.GetItemByIdPort;
import com.artemsirosh.lite.sftp.port.outbound.RegisterItemPort;
import com.artemsirosh.lite.sftp.port.outbound.UnregisterItemPort;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;

class InMemoryItemRegistryTest {

    private final static ItemId ALPHA_ID = ItemId.fromString("alpha");
    private final static ItemId BRAVO_ID = ItemId.fromString("bravo");
    private final static Item ALPHA_DIR = Directory.builder()
        .id(ALPHA_ID)
        .name("alpha")
        .build();
    
    private final static Item CHARLIE_THE_FILE = File.builder()
        .id(ItemId.newInstanceUUID())
        .name("charlie")
        .parent(ALPHA_DIR)
        .modifiedDate(Instant.now())
        .build();

    private GetItemByIdPort getItemByIdPort;
    private RegisterItemPort registerItemPort;
    private UnregisterItemPort unregisterItemPort;

    @BeforeEach
    void setUpStorage() {
        final var storage = new HashMap<ItemId, Item>();

        final Directory bravo =  Directory.builder()
            .id(BRAVO_ID)
            .name("bravo")
            .parent(ALPHA_DIR)
            .build();

        storage.put(ALPHA_ID, ALPHA_DIR);
        storage.put(BRAVO_ID, bravo);

        final var registry = new InMemoryItemRegistry(storage);

        this.getItemByIdPort = registry;
        this.registerItemPort = registry;
        this.unregisterItemPort = registry;
    }

    @Test
    @DisplayName("Must find stored item by id w/o errors")
    void test_00() {
        Assertions.assertThatCode(() -> getItemByIdPort.getItemById(ALPHA_ID))
            .doesNotThrowAnyException();
    }

    @Test
    @DisplayName("Must find proper item")
    void test_01() {
        final Item actual = getItemByIdPort.getItemById(ALPHA_ID);

        assertThat(actual)
            .describedAs("Must be not null")
            .isNotNull();

        assertThat(actual)
            .describedAs("Must have proper name")
            .extracting("name")
            .isEqualTo("alpha");

        assertThat(actual)
            .describedAs("Shouldn't have parent")
            .extracting("parent")
            .isNull();
    }

    @Test
    @DisplayName("Must fail getting item when item not exists")
    void test_02() {
        assertThatCode(() -> getItemByIdPort.getItemById(ItemId.newInstanceUUID()))
            .isInstanceOf(AbstractServiceException.class);
    }

    @Test
    @DisplayName("Must fail attempting find item by null id")
    void test_03() {
        assertThatCode(() -> getItemByIdPort.getItemById(null))
            .isInstanceOf(AbstractServiceException.class);
    }

    @Test
    @DisplayName("Must not fail when register a sample item")
    void test_04() {
        assertThatCode(() -> registerItemPort.register(CHARLIE_THE_FILE))
            .doesNotThrowAnyException();
    }

    @Test
    @DisplayName("Must able to find when did register item")
    void test_05() {
        assertThatCode(() -> registerItemPort.register(CHARLIE_THE_FILE))
            .describedAs("Shouldn't fail registering")
            .doesNotThrowAnyException();

        assertThatCode(() -> getItemByIdPort.getItemById(CHARLIE_THE_FILE.getId()))
            .describedAs("Should find registered item")
            .doesNotThrowAnyException();
    }

    @Test
    @DisplayName("Must replace old registered item with new one")
    void test_06() {
        final ItemId charlieId = CHARLIE_THE_FILE.getId();
        final Instant updateLastModifiedDatetime = Instant.parse("2020-10-21T21:31:22Z");
        final File updatedFile = File.builder()
            .id(charlieId)
            .name(CHARLIE_THE_FILE.getName())
            .modifiedDate(updateLastModifiedDatetime)
            .parent(CHARLIE_THE_FILE.getParent())
            .build();

        assertThatCode(() -> registerItemPort.register(updatedFile))
            .describedAs("Shouldn't fail registering")
            .doesNotThrowAnyException();
        
        final File actual = (File) getItemByIdPort.getItemById(charlieId);
        assertThat(actual)
            .extracting("modifiedDate")
            .describedAs("Should has update last modified date-time")
            .isEqualTo(updateLastModifiedDatetime);
    }

    @Test
    @DisplayName("Must remove from registry item w/o failures")
    void test_07() {
        assertThatCode(() -> unregisterItemPort.unregister(BRAVO_ID))
            .doesNotThrowAnyException();
    }

    @Test
    @DisplayName("Must no able to find item when did remove item from registry")
    void test_08() {
        assertThatCode(() -> unregisterItemPort.unregister(BRAVO_ID))
            .describedAs("Shouldn't fail unregister operation")
            .doesNotThrowAnyException();
        
        assertThatCode(() -> getItemByIdPort.getItemById(BRAVO_ID))
            .describedAs("Shouldn't find unregistered item")
            .isNotNull();
    }

    @Test
    @DisplayName("Must skip operation silently when attempt to remove non-registered item")
    void test_09() {
        assertThatCode(() -> unregisterItemPort.unregister(ItemId.newInstanceUUID()))
            .describedAs("Shouldn't fail unregister operation")
            .doesNotThrowAnyException();
    }

    private static class TestException extends AbstractServiceException {
        TestException() {
            super("Test exception");
        }
    }
}
