package com.artemsirosh.lite.sftp.persistence;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.lang.NonNull;

import com.artemsirosh.lite.sftp.domain.Item;
import com.artemsirosh.lite.sftp.domain.ItemId;
import com.artemsirosh.lite.sftp.errors.AbstractServiceException;
import com.artemsirosh.lite.sftp.port.outbound.GetItemByIdPort;
import com.artemsirosh.lite.sftp.port.outbound.RegisterItemPort;
import com.artemsirosh.lite.sftp.port.outbound.UnregisterItemPort;

public class InMemoryItemRegistry implements GetItemByIdPort, RegisterItemPort, UnregisterItemPort {

    private final Map<ItemId, Item> storage;

    public InMemoryItemRegistry(final Map<ItemId, Item> preloadedData) {
        this.storage = new ConcurrentHashMap<>(preloadedData);
    }

    @NonNull
    @Override
    public Item getItemById(@NonNull final ItemId id) {
        final Optional<ItemId> opt = Optional.ofNullable(id);
        if (opt.isPresent()) {
            return opt.map(storage::get).orElseThrow(() -> new InMemStorageException(id));
        } else {
            throw new InvalidItemIdException();
        }
    }

    @Override
    public void register(Item item) {
        storage.put(item.getId(), item);
    }

    @Override
    public void unregister(ItemId id) {
        storage.remove(id);
    }

    private static class InvalidItemIdException extends AbstractServiceException {
        private InvalidItemIdException() {
            super("ItemId is null");
        }
    }

    private static class InMemStorageException extends AbstractServiceException {

        private InMemStorageException(final ItemId id) {
            super("Item with id '" + id +  "' not found");
        }
    }
}
