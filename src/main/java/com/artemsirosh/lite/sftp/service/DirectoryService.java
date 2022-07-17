package com.artemsirosh.lite.sftp.service;

import com.artemsirosh.lite.sftp.domain.Directory;
import com.artemsirosh.lite.sftp.domain.DirectoryListing;
import com.artemsirosh.lite.sftp.domain.Item;
import com.artemsirosh.lite.sftp.domain.ItemId;
import com.artemsirosh.lite.sftp.port.inbound.CreateDirectoryCommand;
import com.artemsirosh.lite.sftp.port.inbound.CreateDirectoryUseCase;
import com.artemsirosh.lite.sftp.port.inbound.DeleteItemCommand;
import com.artemsirosh.lite.sftp.port.inbound.DeleteItemUseCase;
import com.artemsirosh.lite.sftp.port.inbound.GetDirectoryChildrenQuery;
import com.artemsirosh.lite.sftp.port.inbound.GetDirectoryListingUseCase;
import com.artemsirosh.lite.sftp.port.outbound.RegisterItemPort;
import com.artemsirosh.lite.sftp.port.outbound.CreateDirectoryPort;
import com.artemsirosh.lite.sftp.port.outbound.DeleteItemPort;
import com.artemsirosh.lite.sftp.port.outbound.GetDirectoryListingPort;
import com.artemsirosh.lite.sftp.port.outbound.GetItemByIdPort;
import com.artemsirosh.lite.sftp.port.outbound.UnregisterItemPort;
import lombok.RequiredArgsConstructor;
import org.springframework.lang.NonNull;

@RequiredArgsConstructor
public class DirectoryService implements CreateDirectoryUseCase, DeleteItemUseCase, GetDirectoryListingUseCase {

    private final RegisterItemPort registerItemPort;
    private final CreateDirectoryPort createDirectoryPort;
    private final DeleteItemPort deleteItemPort;
    private final GetDirectoryListingPort getDirectoryListingPort;
    private final GetItemByIdPort getItemByIdPort;
    private final UnregisterItemPort unregisterItemPort;

    @Override
    @NonNull
    public Directory createDirectory(@NonNull final CreateDirectoryCommand command) {
        final Item parent = getItemByIdPort.getItemById(command.getParentId());
        final Directory directory = Directory.builder()
                .id(ItemId.newInstanceUUID())
                .name(command.getName())
                .parent(parent)
                .build();

        createDirectoryPort.createDirectory(directory);
        registerItemPort.register(directory);
        return directory;
    }

    @Override
    @NonNull
    public Item deleteItem(@NonNull final DeleteItemCommand command) {
        final Item item = getItemByIdPort.getItemById(command.getId());
        deleteItemPort.deleteItem(item);
        unregisterItemPort.unregister(item.getId());
        return item;
    }

    @Override
    @NonNull
    public DirectoryListing getDirectoryListing(@NonNull final GetDirectoryChildrenQuery query) {
        final Item item = getItemByIdPort.getItemById(query.getDirectoryId());
        if (item.isDirectory()) {
            return getDirectoryListingPort.getListing((Directory) item);
        } else {
            throw new ServiceException("Item with id: '" + query.getDirectoryId() + "' isn't directory");
        }
    }
}
