package com.artemsirosh.lite.sftp.port.outbound;

import com.artemsirosh.lite.sftp.errors.AbstractServiceException;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class UnregisterItemPortTest extends ItemPortTestCase {

    @Override
    @AfterEach
    protected void reset() {
        super.reset();
    }

    @Test
    @DisplayName("Should unregister Item w/o failures when Item registered")
    void test_00() {
        Assertions.assertThatCode(() -> getUnregisterItemPort().unregister(getRegisteredItemId()))
                .doesNotThrowAnyException();
    }

    @Test
    @DisplayName("Should not retrieve unregistered Item when Item registered")
    void test_01() {
        Assertions.assertThatCode(() -> getUnregisterItemPort().unregister(getRegisteredItemId()))
                .describedAs("Should unregister Item w/o failures")
                .doesNotThrowAnyException();

        Assertions.assertThatCode(() -> getGetItemByIdPort().getItemById(getRegisteredItemId()))
                .describedAs("Shouldn fail Item retrieving when Item unregistered")
                .isNotNull();

    }

    @Test
    @DisplayName("Should fail unregister Item when Item wasn't registered")
    void test_02() {
        Assertions.assertThatCode(() -> getUnregisterItemPort().unregister(getUnregisteredItemId()))
                .isInstanceOf(AbstractServiceException.class);
    }
}