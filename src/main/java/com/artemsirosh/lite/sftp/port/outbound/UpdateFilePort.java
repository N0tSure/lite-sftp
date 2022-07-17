package com.artemsirosh.lite.sftp.port.outbound;

import com.artemsirosh.lite.sftp.domain.File;
import com.artemsirosh.lite.sftp.domain.FileContent;
import org.springframework.lang.NonNull;

/**
 * Updates file or file content.
 */
public interface UpdateFilePort {

    /**
     * Updates file.
     * 
     * @param file {@link File} model
     */
    void updateFile(@NonNull final File file);

    /**
     * Updates file content.
     * 
     * @param content {@link FileContent} model
     */
    void updateFileContent(@NonNull final FileContent content);
}
