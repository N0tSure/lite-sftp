package com.artemsirosh.lite.sftp.domain;

import lombok.Value;

import java.util.UUID;

/**
 * Item's identifier.
 */
@Value
public class ItemId {

    String value;

    /**
     * Creates a new ItemId using {@link UUID}.
     */
    public static ItemId newInstanceUUID() {
        return new ItemId(UUID.randomUUID().toString());
    }

    /**
     * Creates a new ItemId from string.
     * @param value identifier of item as string
     */
    public static ItemId fromString(final String value) {
        return new ItemId(value);
    }


}
