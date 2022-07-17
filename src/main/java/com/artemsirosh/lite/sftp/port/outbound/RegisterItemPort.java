package com.artemsirosh.lite.sftp.port.outbound;

import com.artemsirosh.lite.sftp.domain.Item;
import org.springframework.lang.NonNull;

/**
 * Register {@link Item} with internal registry.
 */
public interface RegisterItemPort {

    /**
     * Registres item.
     * 
     * @param item {@link Item} model
     */
    void register(@NonNull final Item item);
}
