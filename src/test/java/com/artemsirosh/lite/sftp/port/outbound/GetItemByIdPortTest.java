package com.artemsirosh.lite.sftp.port.outbound;

import com.artemsirosh.lite.sftp.adapter.outbound.ItemRepository;
import com.artemsirosh.lite.sftp.domain.File;
import com.artemsirosh.lite.sftp.domain.ItemId;
import com.artemsirosh.lite.sftp.errors.AbstractServiceException;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Map;

class GetItemByIdPortTest {

    private static final ItemId ALPHA_FILE_ID = ItemId.newInstanceUUID();
    private static final File ALPHA_FILE = File.builder()
            .id(ALPHA_FILE_ID)
            .name("alpha")
            .build();

    private final GetItemByIdPort service = new ItemRepository(Map.of(ALPHA_FILE_ID, ALPHA_FILE));

    @Test
    @DisplayName("Should retrieve Item w/o failures when Item existed")
    void test_00() {
        Assertions.assertThatCode(() -> service.getItemById(ALPHA_FILE_ID))
                .doesNotThrowAnyException();
    }

    @Test
    @DisplayName("Should retrieve proper Item when Item existed")
    void test_01() {
        Assertions.assertThat(service.getItemById(ALPHA_FILE_ID))
                .isEqualTo(ALPHA_FILE);
    }

    @Test
    @DisplayName("Should fail w/ error when Item not existed")
    void test_02() {
        Assertions.assertThatCode(() -> service.getItemById(ItemId.newInstanceUUID()))
                .isInstanceOf(AbstractServiceException.class);
    }
}