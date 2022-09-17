package com.artemsirosh.lite.sftp.port.outbound;

import com.artemsirosh.lite.sftp.errors.AbstractServiceException;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class RegisterItemPortTest extends ItemPortTestCase {

    @Override
    @AfterEach
    protected void reset() {
        super.reset();
    }

    @Test
    @DisplayName("Should register Item w/o failure when item not registered")
    void test_00() {
        Assertions.assertThatCode(() -> getRegisterItemPort().register(getUnregisteredItem()))
                .doesNotThrowAnyException();
    }

    @Test
    @DisplayName("Should retrieve Item when it was registered")
    void test_01() {
        Assertions.assertThatCode(() -> getRegisterItemPort().register(getUnregisteredItem()))
                .describedAs("Should register w/ failures")
                .doesNotThrowAnyException();

        Assertions.assertThat(getGetItemByIdPort().getItemById(getUnregisteredItemId()))
                .describedAs("Should retrieve registered item")
                .isEqualTo(getUnregisteredItem());
    }

    @Test
    @DisplayName("Should fail registration when Item already registered")
    void test_02() {
        Assertions.assertThatCode(() -> getRegisterItemPort().register(getRegisteredItem()))
                .isInstanceOf(AbstractServiceException.class);
    }
}