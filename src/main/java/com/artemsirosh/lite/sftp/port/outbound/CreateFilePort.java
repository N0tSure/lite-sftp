package com.artemsirosh.lite.sftp.port.outbound;

import com.artemsirosh.lite.sftp.domain.File;
import com.artemsirosh.lite.sftp.domain.FileContent;
import org.springframework.lang.NonNull;

/**
 * Creates a new file with content.
 */
public interface CreateFilePort {

    /**
     * Creates a new file using file and content models.
     * 
     * @param file {@link File} model
     * @param content {@link FileContent} model
     */
    void createFile(@NonNull final File file, @NonNull final FileContent content);
}
