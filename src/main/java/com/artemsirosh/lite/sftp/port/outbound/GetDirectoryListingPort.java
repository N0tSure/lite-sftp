package com.artemsirosh.lite.sftp.port.outbound;

import com.artemsirosh.lite.sftp.domain.Directory;
import com.artemsirosh.lite.sftp.domain.DirectoryListing;

/**
 * Returns directory listing for particular directory.
 */
public interface GetDirectoryListingPort {

    /**
     * Returns directory listing.
     * 
     * @param directory {@link Directory} model
     * @return {@link DirectoryListing} for given directory
     */
    DirectoryListing getListing(final Directory directory);
}
