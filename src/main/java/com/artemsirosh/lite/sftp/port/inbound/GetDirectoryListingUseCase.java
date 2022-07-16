package com.artemsirosh.lite.sftp.port.inbound;

import com.artemsirosh.lite.sftp.domain.DirectoryListing;
import org.springframework.lang.NonNull;

/**
 * Allows to lookup directory contents. Similar to {@code ls} coreutils
 * command.
 */
public interface GetDirectoryListingUseCase {

    /**
     * Performs query for directory's children.
     * @param query directory listing attributes
     * @return directory listing view
     */
    @NonNull
    DirectoryListing getDirectoryListing(@NonNull final GetDirectoryChildrenQuery query);

}
