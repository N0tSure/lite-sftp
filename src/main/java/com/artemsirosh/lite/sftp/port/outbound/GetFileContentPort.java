package com.artemsirosh.lite.sftp.port.outbound;

import org.springframework.lang.NonNull;

import com.artemsirosh.lite.sftp.domain.File;
import com.artemsirosh.lite.sftp.domain.FileContent;

/**
 * Finds and returns content of the file.
 */
public interface GetFileContentPort {

    /**
     * Returns content of the file {@link FileContent}.
     * @param file {@link File} model
     */
    @NonNull
    FileContent getFileContent(@NonNull final File file);
}
