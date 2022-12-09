package com.artemsirosh.lite.sftp.adapter.outbound;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class ItemRepositoryTest {

    @Test
    @DisplayName("Should instantiate ItemRepository without error")
    void test_00() {
        Assertions.assertThatCode(() -> ItemRepository.class.getDeclaredConstructor().newInstance())
                .doesNotThrowAnyException();
    }
}