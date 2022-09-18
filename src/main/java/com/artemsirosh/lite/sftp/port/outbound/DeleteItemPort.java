package com.artemsirosh.lite.sftp.port.outbound;

import com.artemsirosh.lite.sftp.domain.FileContent;
import com.artemsirosh.lite.sftp.domain.Item;
import org.springframework.lang.NonNull;

/**
 * Deletes any item: {@link com.artemsirosh.lite.sftp.domain.File} or
 * {@link FileContent}.
 */
public interface DeleteItemPort {

    /**
     * Deletes given item.
     * 
     * @param item {@link Item} model
     * @throws com.artemsirosh.lite.sftp.errors.AbstractServiceException when
     *         Item not exists
     */
    void deleteItem(@NonNull final Item item);
}
