package com.artemsirosh.lite.sftp.domain;

import lombok.Value;

import java.util.Set;

/**
 * Listing of {@link Directory}'s content.
 */
@Value
public class DirectoryListing {

    Directory directory;
    Set<Item> items;
}
