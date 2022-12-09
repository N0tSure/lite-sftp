package com.artemsirosh.lite.sftp.domain;

import com.google.common.collect.ImmutableList;
import lombok.Value;
import org.springframework.lang.NonNull;

import java.nio.ByteBuffer;
import java.nio.charset.CharacterCodingException;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Base64;
import java.util.LinkedList;
import java.util.List;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.UUID;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

/**
 * Item's identifier.
 */
@Value
public class ItemId {

    private static final String DELIMITER = "/";

    List<CharSequence> path;

    private ItemId(final List<CharSequence> path) {
        this.path = path;
    }

    /**
     * Creates a new ItemId using {@link UUID}.
     */
    public static ItemId newInstanceUUID() {
        return ItemId.of(UUID.randomUUID().toString());
    }

    /**
     * Creates a new ItemId from string.
     * @param value identifier of item as string
     */
    public static ItemId fromString(final String value) {
        return ItemId.of(value);
    }

    @NonNull
    public static ItemId of(@NonNull final CharSequence pathElement, final CharSequence... pathElements) {
        final List<CharSequence> path = Stream.concat(Stream.of(pathElement), Arrays.stream(pathElements))
                .collect(ImmutableList.toImmutableList());

        return new ItemId(path);
    }

    @NonNull
    public static ItemId of(@NonNull final Iterable<CharSequence> pathElements) {
        final List<CharSequence> path = StreamSupport.stream(pathElements.spliterator(), false)
                .collect(ImmutableList.toImmutableList());

        return new ItemId(path);
    }

    @NonNull
    public static ItemId fromBase64String(@NonNull final String encodedPath) {
        final byte[] decodedPath = Base64.getUrlDecoder()
                .decode(encodedPath);

        final var path = new String(decodedPath, StandardCharsets.UTF_8).split(DELIMITER);
        return new ItemId(ImmutableList.copyOf(path));
    }

    public List<String> getItemPath() {
        return List.of();
    }
}
