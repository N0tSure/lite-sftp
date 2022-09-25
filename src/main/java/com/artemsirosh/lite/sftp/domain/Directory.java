package com.artemsirosh.lite.sftp.domain;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.Value;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

/**
 * Implementation of {@link Item} represents kind of items that can contain
 * another items.
 */
@Value
public class Directory implements Item {

    ItemId id;
    Item parent;
    String name;

    /**
     * Set of {@link Item}s that belongs to current Directory.
     */
    Set<ChildItem> children;

    public static Directory.Builder builder() {
        return new Builder();
    }


    @Override
    public ItemId getId() {
        return id;
    }

    @Override
    public Item getParent() {
        return parent;
    }

    @Override
    public boolean isDirectory() {
        return true;
    }

    @Override
    public String getName() {
        return name;
    }

    @Value
    @AllArgsConstructor(access = AccessLevel.PRIVATE)
    public static class ChildItem implements Item {

        @EqualsAndHashCode.Exclude
        ItemId id;

        @EqualsAndHashCode.Exclude
        Item parent;

        @EqualsAndHashCode.Include
        String name;

        @EqualsAndHashCode.Exclude
        boolean isDirectory;

        @EqualsAndHashCode.Exclude
        Directory directory;

        @EqualsAndHashCode.Exclude
        File fileItem;

        @Override
        public boolean isDirectory() {
            return isDirectory;
        }

        public Optional<File> asFile() {
            return Optional.ofNullable(fileItem);
        }

        public Optional<Directory> asDirectory() {
            return Optional.ofNullable(directory);
        }
    }

    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    public static class Builder {

        ItemId id = ItemId.newInstanceUUID();
        Item parent;
        String name;
        Set<ChildItem> children = new HashSet<>();

        public Builder id(final ItemId id) {
            this.id = id;
            return this;
        }

        public Builder parent(final Item item) {
            this.parent = item;
            return this;
        }

        public Builder name(final String name) {
            this.name = name;
            return this;
        }

        public Builder addItem(final Directory item) {
            children.add(new ChildItem(
                    item.getId(),
                    item.getParent(),
                    item.getName(),
                    item.isDirectory(),
                    item,
                    null
            ));
            return this;
        }

        public Builder addItem(final File item) {
            children.add(new ChildItem(
                    item.getId(),
                    item.getParent(),
                    item.getName(),
                    item.isDirectory(),
                    null,
                    item
            ));
            return this;
        }

        public Builder addDirectoryItems(final Iterable<Directory> items) {
            for (Directory item : items) {
                addItem(item);
            }
            return this;
        }

        public Builder addFileItems(final Iterable<File> items) {
            for (final File item : items) {
                addItem(item);
            }
            return this;
        }

        public Directory build() {
            return new Directory(id, parent, name, children);
        }
    }
}
