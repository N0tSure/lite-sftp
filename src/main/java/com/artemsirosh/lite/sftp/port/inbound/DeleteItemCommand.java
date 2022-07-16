package com.artemsirosh.lite.sftp.port.inbound;

import org.springframework.lang.NonNull;

import com.artemsirosh.lite.sftp.domain.ItemId;
import lombok.Value;

/**
 * Contains data for locate data for deletion.
 */
@Value
public class DeleteItemCommand {

    /**
     * Deleting file {@link ItemId}. Cannot be {@code null}.
     */
    @NonNull
    ItemId id;
}
