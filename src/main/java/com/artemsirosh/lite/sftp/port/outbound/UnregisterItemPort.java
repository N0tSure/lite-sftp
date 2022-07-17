package com.artemsirosh.lite.sftp.port.outbound;

import com.artemsirosh.lite.sftp.domain.ItemId;
import org.springframework.lang.NonNull;

/**
 * Cancels registration of item in internal registry.
 */
public interface UnregisterItemPort {

    /**
     * Will cancel {@link Item} registration.
     * 
     * @param id {@link Item} identifier
     */
    void unregister(@NonNull final ItemId id);
}
