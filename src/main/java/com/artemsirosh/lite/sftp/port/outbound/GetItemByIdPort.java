package com.artemsirosh.lite.sftp.port.outbound;

import com.artemsirosh.lite.sftp.domain.ItemId;
import com.artemsirosh.lite.sftp.domain.Item;
import org.springframework.lang.NonNull;

/**
 * Find an item by {@link ItemId}.
 */
public interface GetItemByIdPort {

    /**
     * Returns found item by item id.
     * 
     * @param id {@link Item} identifier
     * @throws com.artemsirosh.lite.sftp.errors.AbstractServiceException when item not found
     */
    @NonNull
    Item getItemById(@NonNull final ItemId id);
}
