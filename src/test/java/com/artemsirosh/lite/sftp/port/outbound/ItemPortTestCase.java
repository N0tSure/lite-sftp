package com.artemsirosh.lite.sftp.port.outbound;

import com.artemsirosh.lite.sftp.adapter.outbound.ItemRepository;
import com.artemsirosh.lite.sftp.domain.File;
import com.artemsirosh.lite.sftp.domain.Item;
import com.artemsirosh.lite.sftp.domain.ItemId;

import java.util.Map;

abstract class ItemPortTestCase {

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

    private ItemRepository itemRepository;

    protected ItemPortTestCase() {
        reset();
    }

    protected GetItemByIdPort getGetItemByIdPort() {
        return itemRepository;
    }

    protected RegisterItemPort getRegisterItemPort() {
        return itemRepository;
    }

    protected Item getRegisteredItem() {
        return ALPHA_FILE;
    }

    protected ItemId getRegisteredItemId() {
        return ALPHA_FILE_ID;
    }

    protected UnregisterItemPort getUnregisterItemPort() {
        return itemRepository;
    }

    protected Item getUnregisteredItem() {
        return BRAVO_FILE;
    }

    protected ItemId getUnregisteredItemId() {
        return BRAVO_FILE_ID;
    }

    protected void reset() {
        itemRepository = new ItemRepository(Map.of(getRegisteredItemId(), getRegisteredItem()));
    }
}