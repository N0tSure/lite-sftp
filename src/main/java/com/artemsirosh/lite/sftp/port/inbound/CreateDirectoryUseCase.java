package com.artemsirosh.lite.sftp.port.inbound;

import com.artemsirosh.lite.sftp.domain.Directory;
import org.springframework.lang.NonNull;

/**
 * Use case for a new directory creation.
 */
public interface CreateDirectoryUseCase {

    /**
     * Creates a new {@link Directory}.
     * @param command future directory's attributes
     * @return created directory model
     */
    @NonNull
    Directory createDirectory(@NonNull final CreateDirectoryCommand command);

}
