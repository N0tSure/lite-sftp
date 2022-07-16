package com.artemsirosh.lite.sftp.port.inbound;

import org.springframework.lang.NonNull;

import com.artemsirosh.lite.sftp.domain.ItemId;
import lombok.Value;

/**
 * This command contains attributes for directory creation.
 */
@Value
public class CreateDirectoryCommand {

    /**
     * Future directory name. Cannot be {@code null}.
     */
    @NonNull
    String name;

    /**
     * Future directory's parent directory. Cannot be {@code null}.
     */
    @NonNull
    ItemId parentId;
}
