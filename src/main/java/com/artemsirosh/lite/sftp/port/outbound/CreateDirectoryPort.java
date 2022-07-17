package com.artemsirosh.lite.sftp.port.outbound;

import com.artemsirosh.lite.sftp.domain.Directory;
import org.springframework.lang.NonNull;

/**
 * This component creates a new {@link Directory}.
 */
public interface CreateDirectoryPort {

    /**
     * Creates new directory using passed model.
     * 
     * @param directory {@link Directory} model
     */
    void createDirectory(@NonNull final Directory directory);
}
