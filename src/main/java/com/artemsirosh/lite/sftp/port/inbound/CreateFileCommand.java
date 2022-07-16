package com.artemsirosh.lite.sftp.port.inbound;

import com.artemsirosh.lite.sftp.domain.Directory;
import com.artemsirosh.lite.sftp.domain.ItemId;
import lombok.Builder;
import lombok.Value;

import java.time.Instant;

import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;

/**
 * File creation command. Contains attributes for file creation.
 */
@Value
@Builder
public class CreateFileCommand {

    /**
     * Future file name. Cannot be {@code null}.
     */
    @NonNull
    String name;

    /**
     * Future file last modification date-time. If absent creation date-time
     * will be used.
     */
    @Nullable
    Instant lastModified;

    /**
     * Parent {@link Directory} identifier. Cannot be {@code null}.
     */
    @NonNull
    ItemId parentId;

    /**
     * Future file content. Cannot be {@code null}.
     */
    @NonNull
    byte[] content;
}
