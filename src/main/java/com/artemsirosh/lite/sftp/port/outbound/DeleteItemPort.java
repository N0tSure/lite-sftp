package com.artemsirosh.lite.sftp.port.outbound;

import com.artemsirosh.lite.sftp.domain.FileContent;
import com.artemsirosh.lite.sftp.domain.Item;
import org.springframework.lang.NonNull;

/**
 * Deletes any item: {@link File} or {@link FileContent}.
 */
public interface DeleteItemPort {

    /**
     * Deletes given item.
     * 
     * @param item {@link Item} model
     */
    void deleteItem(@NonNull final Item item);
}
