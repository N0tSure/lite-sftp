package com.artemsirosh.lite.sftp.domain;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.RequiredArgsConstructor;
import lombok.Value;

import java.time.Instant;

/**
 * Implementation of {@link Item} represents a generic file system item holding
 * data.
 */
@Value
@Builder
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class File implements Item {

    ItemId id;
    Item parent;
    String name;

    /**
     * Date and time when current File was modified.
     */
    Instant modifiedDate;

    @Override
    public boolean isDirectory() {
        return false;
    }
}
