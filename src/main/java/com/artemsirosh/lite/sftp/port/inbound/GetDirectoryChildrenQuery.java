package com.artemsirosh.lite.sftp.port.inbound;

import com.artemsirosh.lite.sftp.domain.ItemId;
import lombok.Value;

/**
 * Query for directory listing.
 */
@Value
public class GetDirectoryChildrenQuery {

    /**
     * Identifier of directory, which content should be listed.
     */
    ItemId directoryId;
}
