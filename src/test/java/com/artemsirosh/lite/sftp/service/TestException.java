package com.artemsirosh.lite.sftp.service;

import com.artemsirosh.lite.sftp.errors.AbstractServiceException;

class TestException extends AbstractServiceException {
    TestException() {
        super("Test exception");
    }
}
