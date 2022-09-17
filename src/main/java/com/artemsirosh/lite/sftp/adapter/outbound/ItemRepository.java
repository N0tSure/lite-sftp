package com.artemsirosh.lite.sftp.adapter.outbound;

import com.artemsirosh.lite.sftp.domain.ItemId;
import com.artemsirosh.lite.sftp.errors.AbstractServiceException;
import com.artemsirosh.lite.sftp.port.outbound.GetItemByIdPort;
import com.artemsirosh.lite.sftp.port.outbound.UnregisterItemPort;
import com.artemsirosh.lite.sftp.domain.Item;
import com.artemsirosh.lite.sftp.port.outbound.RegisterItemPort;
import org.springframework.lang.NonNull;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

public class ItemRepository implements GetItemByIdPort, RegisterItemPort, UnregisterItemPort {

    private final Map<ItemId, Item> storage;

    public ItemRepository() {
        this.storage = new ConcurrentHashMap<>();
    }

    public ItemRepository(final Map<ItemId, Item> storage) {
        this.storage = new ConcurrentHashMap<>(storage);
    }

    @Override
    @NonNull
    public Item getItemById(@NonNull final ItemId id) {
        return Optional.ofNullable(storage.get(id))
                .orElseThrow(() -> new ItemNotRegistered(id));
    }

    @Override
    public void register(@NonNull final Item item) {
        if (!storage.containsKey(item.getId())) {
            storage.put(item.getId(), item);
        } else {
            throw new ItemAlreadyRegistered(item);
        }
    }

    @Override
    public void unregister(@NonNull ItemId id) {
        if (storage.containsKey(id)) {
            storage.remove(id);
        } else {
            throw new ItemNotRegistered(id);
        }
    }

    private static class ItemNotRegistered extends AbstractServiceException {
        private ItemNotRegistered(final ItemId id) {
            super("Item with id: '" + id + "' not registered");
        }
    }

    private static class ItemAlreadyRegistered extends AbstractServiceException {
        private ItemAlreadyRegistered(final Item item) {
            super("Item '" + item + "' already registered");
        }
    }
}
