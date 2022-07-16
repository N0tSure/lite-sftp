package com.artemsirosh.lite.sftp.port.inbound;

import org.springframework.lang.NonNull;

import com.artemsirosh.lite.sftp.domain.ItemId;
import lombok.Value;

/**
 * Query for directory listing.
 */
@Value
public class GetDirectoryChildrenQuery {

    /**
     * Identifier of directory, which content should be listed. Cannot
     * be {@code null}.
     */
    @NonNull
    ItemId directoryId;
}
