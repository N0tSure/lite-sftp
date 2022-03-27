package com.artemsirosh.lite.sftp.domain;

import java.util.LinkedList;
import java.util.List;

/**
 * Represents file system item.
 */
public interface Item {

    /**
     * Returns item identifier.
     */
    ItemId getId();

    /**
     * Returns parent item.
     */
    Item getParent();

    /**
     * Returns item's name.
     */
    String getName();

    /**
     * Returns flag that tells whether this item directory.
     *
     * @return {@code true} if this item is directory
     */
    boolean isDirectory();

    /**
     * Returns sequence of item names representing a path to item. It starts
     * from parent name to current item name.
     *
     * @return path to current item
     */
    default List<String> calculatePath() {
        final var path = new LinkedList<String>();
        Item item = this;
        while (item != null) {
            path.addFirst(item.getName());
            item = item.getParent();
        }

        return path;
    }
}
