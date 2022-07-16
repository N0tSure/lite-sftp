package com.artemsirosh.lite.sftp.port.inbound;

import com.artemsirosh.lite.sftp.domain.File;
import com.artemsirosh.lite.sftp.domain.ItemId;
import lombok.Value;

/**
 * Represnts data needed for {@link File} location for content retrieving.
 */
@Value
public class GetFileContentQuery {

    /**
     * Identifier of file.
     */
    ItemId fileId;
}
