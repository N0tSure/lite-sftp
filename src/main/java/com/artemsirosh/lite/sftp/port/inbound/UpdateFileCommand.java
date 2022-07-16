package com.artemsirosh.lite.sftp.port.inbound;

import com.artemsirosh.lite.sftp.domain.ItemId;
import lombok.Builder;
import lombok.Value;

import java.time.Instant;
import java.util.Optional;

import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;

/**
 * Attributes for file updating.
 */
@Value
@Builder
public class UpdateFileCommand {

    /**
     * Identifier of file. Cannot be {@code null}.
     */
    @NonNull
    ItemId fileId;

    @Nullable
    String name;

    @Nullable
    Instant lastModified;

    @Nullable
    byte[] content;

    /**
     * New file's name.
     */
    public Optional<String> getName() {
        return Optional.ofNullable(name);
    }

    /**
     * New last modification date-time.
     */
    public Optional<Instant> getLastModified() {
        return Optional.ofNullable(lastModified);
    }

    /**
     * New file content.
     */
    public Optional<byte[]> getContent() {
        return Optional.ofNullable(content);
    }
}
