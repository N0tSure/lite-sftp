package com.artemsirosh.lite.sftp.port.outbound;

import com.artemsirosh.lite.sftp.domain.Item;
import org.springframework.lang.NonNull;

/**
 * Register {@link Item} with internal registry.
 */
public interface RegisterItemPort {

    /**
     * Registers item.
     * 
     * @param item {@link Item} model
     * @throws com.artemsirosh.lite.sftp.errors.AbstractServiceException if item already registered
     */
    void register(@NonNull final Item item);
}
