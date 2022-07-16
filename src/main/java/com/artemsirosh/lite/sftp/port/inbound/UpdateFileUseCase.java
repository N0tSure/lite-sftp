package com.artemsirosh.lite.sftp.port.inbound;

import com.artemsirosh.lite.sftp.domain.File;
import org.springframework.lang.NonNull;

/**
 * Use case for updating a {@link File}.
 */
public interface UpdateFileUseCase {

    /**
     * Updates file attrubutes specified by {@link UpdateFileCommand}.
     * @param command file updating attributes
     * @return updated {@link File}
     */
    @NonNull
    File updateFile(@NonNull final UpdateFileCommand command);

}
