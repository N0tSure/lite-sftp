package com.artemsirosh.lite.sftp.port.outbound;

import com.artemsirosh.lite.sftp.adapter.outbound.ItemRepository;
import com.artemsirosh.lite.sftp.domain.File;
import com.artemsirosh.lite.sftp.domain.ItemId;
import com.artemsirosh.lite.sftp.errors.AbstractServiceException;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Map;

class RegisterItemPortTest {

    private static final ItemId ALPHA_FILE_ID = ItemId.fromString("alpha1");
    private static final ItemId BRAVO_FILE_ID = ItemId.fromString("bravo2");
    private static final File ALPHA_FILE = File.builder()
            .id(ALPHA_FILE_ID)
            .name("alpha")
            .build();

    private static final File BRAVO_FILE = File.builder()
            .id(BRAVO_FILE_ID)
            .name("bravo")
            .build();

    private final RegisterItemPort service = new ItemRepository(Map.of(ALPHA_FILE_ID, ALPHA_FILE));
    private final GetItemByIdPort getItemByIdPort = (GetItemByIdPort) service;

    @Test
    @DisplayName("Should register Item w/o failure when item not registered")
    void test_00() {
        Assertions.assertThatCode(() -> service.register(BRAVO_FILE))
                .doesNotThrowAnyException();
    }

    @Test
    @DisplayName("Should retrieve Item when it was registered")
    void test_01() {
        Assertions.assertThatCode(() -> service.register(BRAVO_FILE))
                .describedAs("Should register w/ failures")
                .doesNotThrowAnyException();

        Assertions.assertThat(getItemByIdPort.getItemById(BRAVO_FILE_ID))
                .describedAs("Should retrieve registered item")
                .isEqualTo(BRAVO_FILE);
    }

    @Test
    @DisplayName("Should fail registration when Item already registered")
    void test_02() {
        Assertions.assertThatCode(() -> service.register(ALPHA_FILE))
                .isInstanceOf(AbstractServiceException.class);
    }
}