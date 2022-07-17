package com.artemsirosh.lite.sftp.service;

import com.artemsirosh.lite.sftp.errors.AbstractServiceException;

class ServiceException extends AbstractServiceException {
    public ServiceException(String message) {
        super(message);
    }
}
