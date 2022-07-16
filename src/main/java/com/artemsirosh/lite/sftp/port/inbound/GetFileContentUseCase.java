package com.artemsirosh.lite.sftp.port.inbound;

import com.artemsirosh.lite.sftp.domain.File;
import com.artemsirosh.lite.sftp.domain.FileContent;
import org.springframework.lang.NonNull;

/**
 * Use case for {@link File} content retrieving.
 */
public interface GetFileContentUseCase {

    /**
     * Returns instance of {@link FileContent}.
     * @param query data for file locating
     */
    @NonNull
    FileContent getFileContent(@NonNull final GetFileContentQuery query);

}
