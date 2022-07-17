package com.artemsirosh.lite.sftp.adapter.outbound;

import com.artemsirosh.lite.sftp.domain.ItemId;
import com.artemsirosh.lite.sftp.port.outbound.GetItemByIdPort;
import com.artemsirosh.lite.sftp.port.outbound.UnregisterItemPort;
import com.artemsirosh.lite.sftp.domain.Item;
import com.artemsirosh.lite.sftp.port.outbound.RegisterItemPort;
import org.springframework.lang.NonNull;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

public class ItemRepository implements GetItemByIdPort, RegisterItemPort, UnregisterItemPort {

    private final Map<ItemId, Item> storage = new ConcurrentHashMap<>();

    @Override
    @NonNull
    public Item getItemById(@NonNull final ItemId id) {
        return Optional.ofNullable(storage.get(id))
                .orElseThrow(() -> new IllegalArgumentException("Item with id: '" + id + "' not found"));
    }

    @Override
    public void register(@NonNull final Item item) {
        storage.put(item.getId(), item);
    }

    @Override
    public void unregister(@NonNull ItemId id) {
        storage.remove(id);
    }
}
