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
     * @throws com.artemsirosh.lite.sftp.errors.AbstractServiceException when:<br/>
     *         1) Directory parent not exists<br/>
     *         2) Same directory already exists
     */
    void createDirectory(@NonNull final Directory directory);
}
