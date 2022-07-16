package com.artemsirosh.lite.sftp.port.inbound;

import com.artemsirosh.lite.sftp.domain.Item;
import org.springframework.lang.NonNull;

/**
 * Use case for {@link Item} deletion.
 */
public interface DeleteItemUseCase {

    /**
     * Deletes {@link Item} given in parameter.
     * @param command contains data to find deleting item
     */
    @NonNull
    Item deleteItem(@NonNull final DeleteItemCommand command);

}
