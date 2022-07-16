package com.artemsirosh.lite.sftp.port.inbound;

import com.artemsirosh.lite.sftp.domain.ItemId;
import lombok.Value;

/**
 * This command contains attributes for directory creation.
 */
@Value
public class CreateDirectoryCommand {

    /**
     * Future directory name.
     */
    String name;

    /**
     * Future directory's parent directory.
     */
    ItemId parentId;
}
