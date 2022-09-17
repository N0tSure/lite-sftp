package com.artemsirosh.lite.sftp.port.outbound;

import com.artemsirosh.lite.sftp.domain.ItemId;
import org.springframework.lang.NonNull;

/**
 * Cancels registration of item in internal registry.
 */
public interface UnregisterItemPort {

    /**
     * Will cancel {@link com.artemsirosh.lite.sftp.domain.Item} registration.
     * 
     * @param id {@link com.artemsirosh.lite.sftp.domain.Item} identifier
     * @throws com.artemsirosh.lite.sftp.errors.AbstractServiceException when item not registered
     */
    void unregister(@NonNull final ItemId id);
}
