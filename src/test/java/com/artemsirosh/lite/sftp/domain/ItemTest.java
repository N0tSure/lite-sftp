package com.artemsirosh.lite.sftp.domain;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class ItemTest {

    @Test
    @DisplayName("Should calculate item path properly")
    void test_00() {

        @RequiredArgsConstructor
        class TestItem implements Item {

            private final ItemId id = ItemId.newInstanceUUID();
            private final Item parent;
            private final String name;

            @Override
            public ItemId getId() {
                return id;
            }

            @Override
            public Item getParent() {
                return parent;
            }

            @Override
            public String getName() {
                return name;
            }

            @Override
            public boolean isDirectory() {
                return true;
            }
        }


        final var alpha = new TestItem(null, "alpha");
        final var bravo = new TestItem(alpha, "bravo");
        final var charlie = new TestItem(bravo, "charlie");


        assertThat(charlie.calculatePath())
                .describedAs("Should have 2 parents")
                .containsExactly("alpha", "bravo");

        assertThat(bravo.calculatePath())
                .describedAs("Should have one parent")
                .containsOnly("alpha");

        assertThat(alpha.calculatePath())
                .describedAs("Should have no parents")
                .isEmpty();

    }
}