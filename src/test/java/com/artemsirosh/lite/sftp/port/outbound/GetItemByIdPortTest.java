package com.artemsirosh.lite.sftp.port.outbound;

import com.artemsirosh.lite.sftp.errors.AbstractServiceException;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class GetItemByIdPortTest extends ItemPortTestCase {

    @Test
    @DisplayName("Should retrieve Item w/o failures when Item existed")
    void test_00() {
        Assertions.assertThatCode(() -> getGetItemByIdPort().getItemById(getRegisteredItemId()))
                .doesNotThrowAnyException();
    }

    @Test
    @DisplayName("Should retrieve proper Item when Item existed")
    void test_01() {
        Assertions.assertThat(getGetItemByIdPort().getItemById(getRegisteredItemId()))
                .isEqualTo(getRegisteredItem());
    }

    @Test
    @DisplayName("Should fail w/ error when Item not existed")
    void test_02() {
        Assertions.assertThatCode(() -> getGetItemByIdPort().getItemById(getUnregisteredItemId()))
                .isInstanceOf(AbstractServiceException.class);
    }
}