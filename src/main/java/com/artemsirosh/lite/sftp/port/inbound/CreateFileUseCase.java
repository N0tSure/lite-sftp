package com.artemsirosh.lite.sftp.port.inbound;

import com.artemsirosh.lite.sftp.domain.File;
import org.springframework.lang.NonNull;

/**
 * Use case for a new file creation.
 */
public interface CreateFileUseCase {

    /**
     * Creates a new {@link File} with given attributes.
     * @param command attributes for file creation
     * @return created file model
     */
    @NonNull
    File createFile(@NonNull final CreateFileCommand command);

}
