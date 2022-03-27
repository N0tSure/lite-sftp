package com.artemsirosh.lite.sftp.domain;

import lombok.RequiredArgsConstructor;
import lombok.Value;

@Value
@RequiredArgsConstructor
public class FileContent {

    /**
     * File's content.
     */
    byte[] content;

    /**
     * File's content checksum.
     */
    byte[] checksum;

    public FileContent(final byte[] content) {
        this(content, new byte[]{});
    }
}
